package org.maia.amstrad.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.io.AmstradIO;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.memory.AmstradMemory;

public abstract class BasicRuntime {

	private AmstradPc amstradPc;

	protected BasicRuntime(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	/**
	 * Clears any loaded program code and variables.
	 * <p>
	 * This has a similar effect as when executing the Basic <code>NEW</code> instruction.
	 * </p>
	 * <p>
	 * This method should be used only when no program is being run (in the Basic "direct modus"). When a program is
	 * still running, it may lead to unexpected behaviour even blocking the entire Amstrad computer.
	 * </p>
	 */
	public abstract void renew();

	public final void load(BasicCode code) throws BasicException {
		checkSameLanguage(code);
		if (code instanceof BasicByteCode) {
			loadByteCode((BasicByteCode) code);
		} else if (code instanceof BasicSourceCode) {
			loadByteCode(getCompiler().compile((BasicSourceCode) code));
		} else {
			throw new BasicException("Unrecognized Basic code");
		}
	}

	protected abstract void loadByteCode(BasicByteCode code) throws BasicException;

	public void loadSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException {
		load(readSourceCodeFromFile(sourceCodeFile));
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(sourceCodeFile.getParentFile());
		System.out.println("Loaded source code from " + sourceCodeFile.getPath());
	}

	public void loadByteCodeFromFile(File byteCodeFile) throws IOException, BasicException {
		load(readByteCodeFromFile(byteCodeFile));
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(byteCodeFile.getParentFile());
		System.out.println("Loaded byte code from " + byteCodeFile.getPath());
	}

	protected abstract BasicSourceCode readSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException;

	protected abstract BasicByteCode readByteCodeFromFile(File byteCodeFile) throws IOException, BasicException;

	public final void run(BasicCode code) throws BasicException {
		load(code);
		run();
	}

	public final void run(BasicCode code, int lineNumber) throws BasicException {
		load(code);
		run(lineNumber);
	}

	public abstract void run();

	public abstract void run(int lineNumber);

	public BasicLineNumberLinearMapping renum() throws BasicException {
		return renum(10, 10);
	}

	public abstract BasicLineNumberLinearMapping renum(int lineNumberStart, int lineNumberStep) throws BasicException;

	public void renum(BasicLineNumberLinearMapping mapping) throws BasicException {
		renum(mapping, new BasicLineNumberScope() {

			@Override
			public boolean isInScope(int lineNumber) {
				return true;
			}
		});
	}

	public abstract void renum(BasicLineNumberLinearMapping mapping, BasicLineNumberScope scope) throws BasicException;

	public BasicSourceCode exportSourceCode() throws BasicException {
		return getDecompiler().decompile(exportByteCode());
	}

	public abstract BasicByteCode exportByteCode() throws BasicException;

	public void saveSourceCodeToFile(File file) throws IOException, BasicException {
		PrintWriter pw = new PrintWriter(file);
		pw.print(exportSourceCode().getText());
		pw.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported source code to " + file.getPath());
	}

	public void saveByteCodeToFile(File file) throws IOException, BasicException {
		FileOutputStream os = new FileOutputStream(file);
		os.write(exportByteCode().getBytes());
		os.flush();
		os.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported byte code to " + file.getPath());
	}

	public void loadBinaryFile(File binaryFile, int memoryStartAddress) throws IOException {
		loadBinaryData(AmstradIO.readBinaryFileContents(binaryFile), memoryStartAddress);
	}

	public void loadBinaryData(byte[] data, int memoryStartAddress) {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			memory.writeBytes(memoryStartAddress, data);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	public void saveBinaryFile(File binaryFile, int memoryStartAddress, int memoryLength) throws IOException {
		AmstradIO.writeBinaryFileContents(binaryFile, exportBinaryData(memoryStartAddress, memoryLength));
	}

	public byte[] exportBinaryData(int memoryStartAddress, int memoryLength) {
		AmstradMemory memory = getMemory();
		memory.startThreadExclusiveSession();
		try {
			return memory.readBytes(memoryStartAddress, memoryLength);
		} finally {
			memory.endThreadExclusiveSession();
		}
	}

	public byte peek(int memoryAddress) {
		return getMemory().readByte(memoryAddress);
	}

	public void poke(int memoryAddress, byte value) {
		getMemory().writeByte(memoryAddress, value);
	}

	protected void checkSameLanguage(BasicCode code) throws BasicException {
		if (!code.getLanguage().equals(getLanguage()))
			throw new BasicException("Basic language mismatch");
	}

	public abstract int getDisplayCanvasWidth();

	public abstract int getDisplayCanvasHeight();

	public abstract int getDisplayTextColumns();

	public abstract int getDisplayTextRows();

	public abstract int getDefaultBorderColorIndex();

	public abstract int getDefaultPaperColorIndex();

	public abstract int getDefaultPenColorIndex();

	public abstract int getMinimumLineNumber();

	public abstract int getMaximumLineNumber();

	public int getNextAvailableLineNumber() {
		return getNextAvailableLineNumber(1);
	}

	public abstract int getNextAvailableLineNumber(int lineNumberStep);

	public abstract List<Integer> getAscendingLineNumbers();

	public abstract BasicCompiler getCompiler();

	public abstract BasicDecompiler getDecompiler();

	public abstract BasicLanguage getLanguage();

	protected AmstradMemory getMemory() {
		return getAmstradPc().getMemory();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}