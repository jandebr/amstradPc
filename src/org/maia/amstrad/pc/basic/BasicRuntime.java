package org.maia.amstrad.pc.basic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.maia.amstrad.pc.AmstradContext;

public abstract class BasicRuntime {

	protected BasicRuntime() {
	}

	public abstract void keyboardType(CharSequence text, boolean waitUntilTyped);

	public void keyboardType(CharSequence text) {
		keyboardType(text, true);
	}

	public void keyboardTypeFileContents(File textFile) throws IOException {
		keyboardType(AmstradContext.readTextFileContents(textFile));
	}

	public void keyboardEnter(CharSequence text, boolean waitUntilTyped) {
		keyboardType(text + "\n", waitUntilTyped);
	}

	public void keyboardEnter(CharSequence text) {
		keyboardEnter(text, true);
	}

	public void keyboardEnter() {
		keyboardEnter("");
	}

	public void cls() {
		keyboardEnter("CLS");
	}

	public void list() {
		keyboardEnter("LIST");
	}

	public void run() {
		keyboardEnter("RUN");
	}

	public void escape() {
		char escape = 0x1b;
		keyboardType("" + escape);
	}

	public void interrupt() {
		escape();
		escape();
	}

	public void loadSourceCodeFromFile(File sourceCodeFile) throws IOException {
		loadSourceCode(AmstradContext.readTextFileContents(sourceCodeFile));
	}

	public void loadSourceCode(CharSequence sourceCode) {
		BasicCompiler compiler = new LocomotiveBasicCompiler();
		loadByteCode(compiler.compile(sourceCode));
	}

	public void loadByteCodeFromFile(File byteCodeFile) throws IOException {
		loadByteCode(AmstradContext.readBinaryFileContents(byteCodeFile));
	}

	public void loadByteCode(byte[] byteCode) {
		loadFittedByteCode(fitByteCode(byteCode));
	}

	/**
	 * Loads Basic bytecode into memory
	 * 
	 * @param byteCode
	 *            The Basic bytecode to load. The bytecode is <em>fitted</em>, meaning it starts right at the first byte
	 *            and ends with the word 0x0000 for the next (void) line length
	 */
	protected abstract void loadFittedByteCode(byte[] byteCode);

	public void exportSourceCodeToFile(File file) throws IOException {
		PrintWriter pw = new PrintWriter(file);
		pw.print(exportSourceCode());
		pw.close();
	}

	public CharSequence exportSourceCode() {
		BasicDecompiler decompiler = new LocomotiveBasicDecompiler();
		return decompiler.decompile(exportByteCode());
	}

	public void exportByteCodeToFile(File file) throws IOException {
		FileOutputStream os = new FileOutputStream(file);
		os.write(exportByteCode());
		os.close();
	}

	public byte[] exportByteCode() {
		return exportFittedByteCode();
	}

	/**
	 * Exports Basic bytecode from memory
	 * 
	 * @return The exported Basic bytecode. The bytecode is <em>fitted</em>, meaning it starts right at the first byte
	 *         and ends with the word 0x0000 for the next (void) line length
	 */
	protected abstract byte[] exportFittedByteCode();

	private byte[] fitByteCode(byte[] byteCode) {
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
	
}