package org.maia.amstrad.basic.locomotive;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.io.AmstradIO;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;

public abstract class LocomotiveBasicRuntime extends BasicRuntime implements LocomotiveBasicMemoryMap {

	public static final int MINIMUM_LINE_NUMBER = 1;

	public static final int MAXIMUM_LINE_NUMBER = 65535;

	public LocomotiveBasicRuntime(AmstradPc amstradPc) {
		super(amstradPc);
	}

	public void command_new() {
		interpretKeyboardInputIfReadyAndWait("NEW");
	}

	public void command_clear() {
		interpretKeyboardInputIfReadyAndWait("CLEAR");
	}

	public void command_cls() {
		interpretKeyboardInputIfReadyAndWait("CLS");
	}

	public void command_list() {
		interpretKeyboardInputIfReadyAndWait("LIST");
	}

	@Override
	public void run() {
		waitUntilReady();
		interpretKeyboardInputIfReady("RUN");
	}

	@Override
	public void run(int lineNumber) {
		waitUntilReady();
		interpretKeyboardInputIfReady("RUN " + lineNumber);
	}

	@Override
	public void renew() {
		clearProgramAndVariables();
	}

	@Override
	public void clear() throws BasicException {
		clearVariables();
	}

	@Override
	public BasicLineNumberLinearMapping renum(int lineNumberStart, int lineNumberStep) throws BasicException {
		LocomotiveBasicByteCode byteCode = getUnmodifiedByteCode();
		BasicLineNumberLinearMapping mapping = byteCode.renum(lineNumberStart, lineNumberStep);
		swapByteCode(byteCode);
		return mapping;
	}

	@Override
	public void renum(BasicLineNumberLinearMapping mapping, BasicLineNumberScope scope) throws BasicException {
		LocomotiveBasicByteCode byteCode = getUnmodifiedByteCode();
		byteCode.renum(mapping, scope);
		swapByteCode(byteCode);
	}

	@Override
	protected LocomotiveBasicSourceCode readSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException {
		return new LocomotiveBasicSourceCode(AmstradIO.readTextFileContents(sourceCodeFile));
	}

	@Override
	protected LocomotiveBasicByteCode readByteCodeFromFile(File byteCodeFile) throws IOException, BasicException {
		return new LocomotiveBasicByteCode(AmstradIO.readBinaryFileContents(byteCodeFile));
	}

	@Override
	protected void loadByteCode(BasicByteCode code) {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Clear
			clearProgramAndVariables();
			// Write byte code
			memory.writeBytes(ADDRESS_BYTECODE_START, code.getBytes());
			// Marking end of byte code
			int addrEnd = ADDRESS_BYTECODE_START + code.getByteCount();
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER_BIS, addrEnd);
			// Marking end of heap space
			memory.writeWord(ADDRESS_HEAP_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_HEAP_END_POINTER_BIS, addrEnd);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	@Override
	public LocomotiveBasicByteCode exportByteCode() throws BasicException {
		LocomotiveBasicByteCode byteCode = getUnmodifiedByteCode();
		byteCode.sanitize();
		return byteCode;
	}

	protected LocomotiveBasicByteCode getUnmodifiedByteCode() {
		LocomotiveBasicByteCode byteCode = null;
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			int len = getByteCodeLength();
			byteCode = new LocomotiveBasicByteCode(memory.readBytes(ADDRESS_BYTECODE_START, len));
		} finally {
			memory.endThreadExclusiveSession();
		}
		return byteCode;
	}

	protected int getByteCodeLength() {
		return getMemory().readWord(ADDRESS_BYTECODE_END_POINTER) - ADDRESS_BYTECODE_START;
	}

	/**
	 * Amends the loaded Basic byte code by merging a given byte code while preserving variables.
	 * 
	 * <p>
	 * This method should be used with extreme care. Merging loaded byte code may lead to unexpected behaviour even
	 * blocking the entire Amstrad computer. In general, it is only safe to merge when no program is being run (in the
	 * Basic "direct modus"). When a program is still running (<em>hot merge</em>) it will only succeed under conditions
	 * that cause no interference with the running Basic interpreter.
	 * </p>
	 * <p>
	 * If it is not needed to preserve variables, it is advised to use the {@link #load(BasicCode)} method, passing a
	 * merged byte code obtained from {@link LocomotiveBasicByteCode#merge(LocomotiveBasicByteCode)}.
	 * </p>
	 * 
	 * @param byteCodeToMerge
	 *            The byte code to merge with the currently loaded byte code
	 * @throws BasicException
	 *             When byte code interpretation is faulty
	 */
	public void swapMergedByteCode(LocomotiveBasicByteCode byteCodeToMerge) throws BasicException {
		LocomotiveBasicByteCode byteCode = getUnmodifiedByteCode();
		byteCode.merge(byteCodeToMerge);
		swapByteCode(byteCode);
	}

	/**
	 * Replaces the loaded Basic byte code with the given byte code while preserving variables.
	 * 
	 * <p>
	 * This method should be used with extreme care. Swapping loaded byte code may lead to unexpected behaviour even
	 * blocking the entire Amstrad computer. In general, it is only safe to swap when no program is being run (in the
	 * Basic "direct modus"). When a program is still running (<em>hot swap</em>) it will only succeed under conditions
	 * that cause no interference with the running Basic interpreter.
	 * </p>
	 * <p>
	 * If it is not needed to preserve variables, it is advised to use the {@link #load(BasicCode)} method.
	 * </p>
	 * 
	 * @param newByteCode
	 *            The byte code to swap in.
	 */
	public void swapByteCode(BasicByteCode newByteCode) {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Move heap space
			moveHeapSpace(newByteCode.getByteCount() - getByteCodeLength());
			// Swap byte code
			memory.writeBytes(ADDRESS_BYTECODE_START, newByteCode.getBytes());
			// Marking end of byte code
			int addrEnd = ADDRESS_BYTECODE_START + newByteCode.getByteCount();
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER_BIS, addrEnd);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	private void moveHeapSpace(int distanceInBytes) {
		if (distanceInBytes == 0)
			return;
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			int heapStart = memory.readWord(ADDRESS_BYTECODE_END_POINTER);
			int heapEnd = memory.readWord(ADDRESS_HEAP_END_POINTER);
			if (heapEnd > heapStart) {
				byte[] heap = memory.readBytes(heapStart, heapEnd - heapStart);
				memory.eraseBytesBetween(heapStart, heapEnd);
				memory.writeBytes(heapStart + distanceInBytes, heap);
			}
			int heapEndNew = heapEnd + distanceInBytes;
			memory.writeWord(ADDRESS_HEAP_END_POINTER, heapEndNew);
			memory.writeWord(ADDRESS_HEAP_END_POINTER_BIS, heapEndNew);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	/**
	 * Similar effect as the Basic <code>NEW</code> instruction
	 */
	protected void clearProgramAndVariables() {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Erase code and heap
			memory.eraseBytesBetween(ADDRESS_BYTECODE_START, memory.readWord(ADDRESS_HEAP_END_POINTER));
			// Reset end of byte code
			int addrEnd = ADDRESS_BYTECODE_START + 2;
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER_BIS, addrEnd);
			// Reset end of heap space
			memory.writeWord(ADDRESS_HEAP_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_HEAP_END_POINTER_BIS, addrEnd);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	/**
	 * Similar effect as the Basic <code>CLEAR</code> instruction
	 */
	protected void clearVariables() throws BasicException {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Sanitize byte code to clear any variable memory pointers
			loadBinaryData(exportByteCode().getBytes(), ADDRESS_BYTECODE_START);
			// Erase heap
			int addrEnd = memory.readWord(ADDRESS_BYTECODE_END_POINTER);
			memory.eraseBytesBetween(addrEnd, memory.readWord(ADDRESS_HEAP_END_POINTER));
			// Reset end of heap space
			memory.writeWord(ADDRESS_HEAP_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_HEAP_END_POINTER_BIS, addrEnd);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	@Override
	public int getDisplayCanvasWidth() {
		return 640;
	}

	@Override
	public int getDisplayCanvasHeight() {
		return 400;
	}

	@Override
	public int getDisplayTextColumns() {
		return 40;
	}

	@Override
	public int getDisplayTextRows() {
		return 25;
	}

	@Override
	public int getDefaultBorderColorIndex() {
		return 1;
	}

	@Override
	public int getDefaultPaperColorIndex() {
		return 1;
	}

	@Override
	public int getDefaultPenColorIndex() {
		return 24;
	}

	@Override
	public int getMinimumLineNumber() {
		return MINIMUM_LINE_NUMBER;
	}

	@Override
	public int getMaximumLineNumber() {
		return MAXIMUM_LINE_NUMBER;
	}

	@Override
	public int getNextAvailableLineNumber(int lineNumberStep) {
		return getUnmodifiedByteCode().getNextAvailableLineNumber(lineNumberStep);
	}

	@Override
	public List<Integer> getAscendingLineNumbers() {
		return getUnmodifiedByteCode().getAscendingLineNumbers();
	}

	@Override
	public LocomotiveBasicCompiler getCompiler() {
		return new LocomotiveBasicCompiler();
	}

	@Override
	public LocomotiveBasicDecompiler getDecompiler() {
		return new LocomotiveBasicDecompiler();
	}

	@Override
	public final BasicLanguage getLanguage() {
		return BasicLanguage.LOCOMOTIVE_BASIC;
	}

}