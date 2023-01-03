package org.maia.amstrad.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;

public abstract class BasicRuntime {

	private AmstradPc amstradPc;

	protected BasicRuntime(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	public final void loadSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException {
		load(readSourceCodeFromFile(sourceCodeFile));
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(sourceCodeFile.getParentFile());
		System.out.println("Loaded source code from " + sourceCodeFile.getPath());
	}

	public final void loadByteCodeFromFile(File byteCodeFile) throws IOException, BasicException {
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

	public abstract void run();

	public abstract void run(int lineNumber);

	public final void exportSourceCodeToFile(File file) throws IOException, BasicException {
		PrintWriter pw = new PrintWriter(file);
		pw.print(exportSourceCode().getText());
		pw.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported source code to " + file.getPath());
	}

	public final void exportByteCodeToFile(File file) throws IOException, BasicException {
		FileOutputStream os = new FileOutputStream(file);
		os.write(exportByteCode().getBytes());
		os.flush();
		os.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported byte code to " + file.getPath());
	}

	public final BasicSourceCode exportSourceCode() throws BasicException {
		return getDecompiler().decompile(exportByteCode());
	}

	protected abstract BasicByteCode exportByteCode() throws BasicException;

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

	public abstract BasicCompiler getCompiler();

	public abstract BasicDecompiler getDecompiler();

	public abstract BasicLanguage getLanguage();

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}