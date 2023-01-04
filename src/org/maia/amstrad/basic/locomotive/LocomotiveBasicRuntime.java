package org.maia.amstrad.basic.locomotive;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.io.AmstradIO;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.memory.AmstradMemory;

public class LocomotiveBasicRuntime extends BasicRuntime implements LocomotiveBasicMemoryMap {

	public static final int MINIMUM_LINE_NUMBER = 1;

	public static final int MAXIMUM_LINE_NUMBER = 65535;

	public LocomotiveBasicRuntime(AmstradPc amstradPc) {
		super(amstradPc);
	}

	public void command_new() {
		getKeyboard().enter("NEW");
	}

	public void command_clear() {
		getKeyboard().enter("CLEAR");
	}

	public void command_cls() {
		getKeyboard().enter("CLS");
	}

	public void command_list() {
		getKeyboard().enter("LIST");
	}

	public void command_run() {
		getKeyboard().enter("RUN");
	}

	public void command_run(int lineNumber) {
		getKeyboard().enter("RUN " + lineNumber);
	}

	@Override
	public void run() {
		command_run();
	}

	@Override
	public void run(int lineNumber) {
		command_run(lineNumber);
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
	protected void loadByteCode(BasicByteCode code) throws BasicException {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Clear
			clearProgramAndVariables();
			// Write byte code
			memory.writeRange(ADDRESS_BYTECODE_START, code.getBytes());
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
	protected LocomotiveBasicByteCode exportByteCode() throws BasicException {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		byte[] bytes = null;
		try {
			int len = memory.readWord(ADDRESS_BYTECODE_END_POINTER) - ADDRESS_BYTECODE_START;
			bytes = memory.readRange(ADDRESS_BYTECODE_START, len);
		} finally {
			memory.endThreadExclusiveSession();
		}
		LocomotiveBasicByteCode byteCode = new LocomotiveBasicByteCode(bytes);
		byteCode.sanitize();
		return byteCode;
	}

	private void clearProgramAndVariables() {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Erase code and heap
			memory.eraseBetween(ADDRESS_BYTECODE_START, memory.readWord(ADDRESS_HEAP_END_POINTER));
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

	private void clearVariables() throws BasicException {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			// Sanitize byte code to clear any variable memory pointers
			loadBinaryData(exportByteCode().getBytes(), ADDRESS_BYTECODE_START);
			// Erase heap
			int addrEnd = memory.readWord(ADDRESS_BYTECODE_END_POINTER);
			memory.eraseBetween(addrEnd, memory.readWord(ADDRESS_HEAP_END_POINTER));
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
	public LocomotiveBasicCompiler getCompiler() {
		return new LocomotiveBasicCompiler();
	}

	@Override
	public LocomotiveBasicDecompiler getDecompiler() {
		return new LocomotiveBasicDecompiler();
	}

	@Override
	public BasicLanguage getLanguage() {
		return BasicLanguage.LOCOMOTIVE_BASIC;
	}

	private AmstradKeyboard getKeyboard() {
		return getAmstradPc().getKeyboard();
	}

}