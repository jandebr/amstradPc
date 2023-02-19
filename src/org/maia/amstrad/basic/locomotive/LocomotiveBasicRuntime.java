package org.maia.amstrad.basic.locomotive;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicMemoryFullException;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.util.AmstradIO;

public abstract class LocomotiveBasicRuntime extends BasicRuntime implements LocomotiveBasicMemoryMap {

	private LocomotiveBasicVariableSpace variableSpace;

	public static final int MINIMUM_LINE_NUMBER = 1;

	public static final int MAXIMUM_LINE_NUMBER = 65535;

	public LocomotiveBasicRuntime(AmstradPc amstradPc) {
		super(amstradPc);
	}

	public void command_new() {
		sendKeyboardInputIfReadyAndWait("NEW");
	}

	public void command_clear() {
		sendKeyboardInputIfReadyAndWait("CLEAR");
	}

	public void command_cls() {
		sendKeyboardInputIfReadyAndWait("CLS");
	}

	public void command_list() {
		sendKeyboardInputIfReadyAndWait("LIST");
	}

	@Override
	public void run() {
		waitUntilReady();
		sendKeyboardInputIfReady("RUN");
	}

	@Override
	public void run(int lineNumber) {
		waitUntilReady();
		sendKeyboardInputIfReady("RUN " + lineNumber);
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
	protected void loadByteCode(BasicByteCode code) throws BasicMemoryFullException {
		int addrEnd = ADDRESS_BYTECODE_START + code.getByteCount();
		checkFitsInsideBasicMemory(addrEnd);
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Clear
			clearProgramAndVariables();
			// Write byte code
			memory.writeBytes(ADDRESS_BYTECODE_START, code.getBytes());
			// Marking end of byte code
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER_BIS, addrEnd);
			// Marking end of variable space
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER_BIS, addrEnd);
		} finally {
			memory.endThreadExclusiveSession();
		}
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
	public BasicLineNumberLinearMapping renum(int lineNumberStart, int lineNumberStep) throws BasicException {
		LocomotiveBasicByteCode byteCode = getUnmodifiedByteCode();
		BasicLineNumberLinearMapping mapping = byteCode.renum(lineNumberStart, lineNumberStep);
		swapByteCode(byteCode);
		return mapping;
	}

	@Override
	public void renum(BasicLineNumberLinearMapping mapping) throws BasicException {
		LocomotiveBasicByteCode byteCode = getUnmodifiedByteCode();
		byteCode.renum(mapping);
		swapByteCode(byteCode);
	}

	protected void swapByteCode(BasicByteCode newByteCode) throws BasicMemoryFullException {
		int addrEnd = ADDRESS_BYTECODE_START + newByteCode.getByteCount();
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Move variable space
			moveVariableSpace(newByteCode.getByteCount() - getByteCodeLength());
			// Swap byte code
			memory.writeBytes(ADDRESS_BYTECODE_START, newByteCode.getBytes());
			// Marking end of byte code
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER_BIS, addrEnd);
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

	protected void checkFitsInsideBasicMemory(int memoryAddress) throws BasicMemoryFullException {
		int addrMax = getHimem();
		if (memoryAddress > addrMax) {
			throw new BasicMemoryFullException();
		}
	}

	protected void checkFitsBelowHeapMemory(int memoryAddress) throws BasicMemoryFullException {
		int addrMax = getMemory().readWord(ADDRESS_HEAP_SPACE_POINTER);
		if (memoryAddress > addrMax) {
			throw new BasicMemoryFullException();
		}
	}

	private void moveVariableSpace(int distanceInBytes) throws BasicMemoryFullException {
		if (distanceInBytes == 0)
			return;
		AmstradMemory memory = getMemory();
		int varStart = memory.readWord(ADDRESS_BYTECODE_END_POINTER);
		int varEnd = memory.readWord(ADDRESS_VARIABLE_SPACE_END_POINTER);
		int varEndNew = varEnd + distanceInBytes;
		checkFitsBelowHeapMemory(varEndNew);
		memory.startThreadExclusiveSession();
		try {
			if (varEnd > varStart) {
				byte[] varSpace = memory.readBytes(varStart, varEnd - varStart);
				memory.eraseBytesBetween(varStart, varEnd);
				memory.writeBytes(varStart + distanceInBytes, varSpace);
			}
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER, varEndNew);
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER_BIS, varEndNew);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	/**
	 * Similar effect as the Basic <code>NEW</code> instruction
	 */
	private void clearProgramAndVariables() {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Reset code and variables
			memory.eraseBytesBetween(ADDRESS_BYTECODE_START, memory.readWord(ADDRESS_VARIABLE_SPACE_END_POINTER));
			int addrEnd = ADDRESS_BYTECODE_START + 2;
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_BYTECODE_END_POINTER_BIS, addrEnd);
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER, addrEnd);
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER_BIS, addrEnd);
			// Reset heap space
			clearHeap();
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	/**
	 * Similar effect as the Basic <code>CLEAR</code> instruction
	 */
	private void clearVariables() throws BasicException {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Sanitize byte code to clear any variable memory pointers
			memory.writeBytes(ADDRESS_BYTECODE_START, exportByteCode().getBytes());
			// Reset variable space
			int codeEnd = memory.readWord(ADDRESS_BYTECODE_END_POINTER);
			memory.eraseBytesBetween(codeEnd, memory.readWord(ADDRESS_VARIABLE_SPACE_END_POINTER));
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER, codeEnd);
			memory.writeWord(ADDRESS_VARIABLE_SPACE_END_POINTER_BIS, codeEnd);
			// Reset heap space
			clearHeap();
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	private void clearHeap() {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			int heapStart = memory.readWord(ADDRESS_HEAP_SPACE_POINTER) + 1;
			int himem = getHimem();
			memory.eraseBytesBetween(heapStart, himem + 1);
			memory.writeWord(ADDRESS_HEAP_SPACE_POINTER, himem);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	public LocomotiveBasicVariableSpace getVariableSpace() {
		if (variableSpace == null) {
			variableSpace = new LocomotiveBasicVariableSpace(getMemory());
		}
		return variableSpace;
	}

	@Override
	public int getHimem() {
		return getMemory().readWord(ADDRESS_HIMEM_POINTER);
	}

	@Override
	public int getFreeMemory() {
		return getMemory().readWord(ADDRESS_HEAP_SPACE_POINTER)
				- getMemory().readWord(ADDRESS_VARIABLE_SPACE_END_POINTER);
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