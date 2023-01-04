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

	public void cls() {
		getKeyboard().enter("CLS");
	}

	public void list() {
		getKeyboard().enter("LIST");
	}

	@Override
	public void run() {
		getKeyboard().enter("RUN");
	}

	@Override
	public void run(int lineNumber) {
		getKeyboard().enter("RUN " + lineNumber);
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
		memory.writeRange(ADDRESS_BYTECODE_START, code.getBytes());
		// Marking end of byte code
		int addr = ADDRESS_BYTECODE_START + code.getByteCount();
		memory.writeWord(ADDRESS_BYTECODE_END_POINTER, addr);
		memory.writeWord(ADDRESS_BYTECODE_END_POINTER_BIS, addr);
		// Marking end of heap space
		memory.writeWord(ADDRESS_HEAP_END_POINTER, addr);
		memory.writeWord(ADDRESS_HEAP_END_POINTER_BIS, addr);
	}

	@Override
	protected LocomotiveBasicByteCode exportByteCode() throws BasicException {
		AmstradMemory memory = getMemory();
		int len = memory.readWord(ADDRESS_BYTECODE_END_POINTER) - ADDRESS_BYTECODE_START;
		LocomotiveBasicByteCode byteCode = new LocomotiveBasicByteCode(memory.readRange(ADDRESS_BYTECODE_START, len));
		byteCode.sanitize(); 
		return byteCode;
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

	private AmstradMemory getMemory() {
		return getAmstradPc().getMemory();
	}

}