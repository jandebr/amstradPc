package org.maia.amstrad.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicCompiler;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicDecompiler;
import org.maia.amstrad.io.AmstradIO;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;

public abstract class BasicRuntime {

	public static final int DISPLAY_CANVAS_WIDTH = 640;

	public static final int DISPLAY_CANVAS_HEIGHT = 400;

	public static final int DISPLAY_TEXT_ROWS = 25;

	public static final int DISPLAY_TEXT_COLUMNS = 40;

	public static final int DEFAULT_BORDER_COLOR_INDEX = 1;

	public static final int DEFAULT_PAPER_COLOR_INDEX = 1;

	public static final int DEFAULT_PEN_COLOR_INDEX = 24;

	public static final int MEMORY_ADDRESS_START_OF_PROGRAM = 0x170;

	public static final int MEMORY_POINTER_END_OF_PROGRAM = 0xAE83; // points to memory address following 0x0000

	public static final int MINIMUM_BASIC_LINE_NUMBER = 0x0001;

	public static final int MAXIMUM_BASIC_LINE_NUMBER = 0xFFFF;

	private AmstradPc amstradPc;

	protected BasicRuntime(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	public void loadSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicSyntaxException {
		loadSourceCode(AmstradIO.readTextFileContents(sourceCodeFile));
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(sourceCodeFile.getParentFile());
		System.out.println("Loaded source code from " + sourceCodeFile.getPath());
	}

	public void loadSourceCode(CharSequence sourceCode) throws BasicSyntaxException {
		if (sourceCode != null) {
			BasicCompiler compiler = new LocomotiveBasicCompiler();
			loadByteCode(compiler.compile(sourceCode));
		}
	}

	public void loadByteCodeFromFile(File byteCodeFile) throws IOException {
		loadByteCode(AmstradIO.readBinaryFileContents(byteCodeFile));
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(byteCodeFile.getParentFile());
		System.out.println("Loaded byte code from " + byteCodeFile.getPath());
	}

	public void loadByteCode(byte[] byteCode) {
		if (byteCode != null) {
			loadFittedByteCode(fitByteCode(byteCode));
		}
	}

	/**
	 * Loads Basic bytecode into memory
	 * 
	 * @param byteCode
	 *            The Basic bytecode to load. The bytecode is <em>fitted</em>, meaning it starts right at the first byte
	 *            of the Basic program and ends with the word 0x0000 for the next (void) line length
	 */
	protected abstract void loadFittedByteCode(byte[] byteCode);

	public void exportSourceCodeToFile(File file) throws IOException, BasicDecompilationException {
		PrintWriter pw = new PrintWriter(file);
		pw.print(exportSourceCode());
		pw.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported source code to " + file.getPath());
	}

	public CharSequence exportSourceCode() throws BasicDecompilationException {
		BasicDecompiler decompiler = new LocomotiveBasicDecompiler();
		return decompiler.decompile(exportByteCode());
	}

	public void exportByteCodeToFile(File file) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		os.write(exportByteCode());
		os.close();
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Exported byte code to " + file.getPath());
	}

	public byte[] exportByteCode() {
		return exportFittedByteCode();
	}

	/**
	 * Exports Basic bytecode from memory
	 * 
	 * @return The exported Basic bytecode. The bytecode is <em>fitted</em>, meaning it starts right at the first byte
	 *         of the Basic program and ends with the word 0x0000 for the next (void) line length
	 */
	protected abstract byte[] exportFittedByteCode();

	protected byte[] fitByteCode(byte[] byteCode) {
		if (byteCode.length < 2) {
			return new byte[2]; // 0x0000
		} else {
			int i = 0;
			int n = (byteCode[0] & 0xff) | ((byteCode[1] << 8) & 0xff00);
			while (n > 0) {
				i += n;
				if (i + 1 < byteCode.length) {
					n = (byteCode[i] & 0xff) | ((byteCode[i + 1] << 8) & 0xff00);
				} else {
					n = 0;
				}
			}
			int len = i + 2;
			if (byteCode.length == len) {
				return byteCode; // already fitted
			} else {
				byte[] fitted = new byte[len];
				System.arraycopy(byteCode, 0, fitted, 0, Math.min(len, byteCode.length));
				return fitted;
			}
		}
	}

	public AmstradKeyboard getKeyboard() {
		return getAmstradPc().getKeyboard();
	}
	
	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}