package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;

public class LocomotiveBasicByteCode extends BasicByteCode {

	public LocomotiveBasicByteCode(byte[] bytes) {
		super(BasicLanguage.LOCOMOTIVE_BASIC, fit(bytes));
	}

	private static byte[] fit(byte[] bytes) {
		byte[] fitted = null;
		if (bytes.length < 2) {
			fitted = new byte[2];
		} else {
			int[] indices = getLineOffsetIndices(bytes);
			if (indices.length == 0) {
				fitted = new byte[2];
			} else {
				int i = indices[indices.length - 1];
				int n = (bytes[i] & 0xff) | ((bytes[i + 1] << 8) & 0xff00);
				i += n;
				if (i == bytes.length - 2) {
					fitted = bytes; // already fitted
				} else {
					fitted = new byte[i + 2];
					System.arraycopy(bytes, 0, fitted, 0, Math.min(fitted.length, bytes.length));
				}
			}
		}
		// last word should be 0x0000
		fitted[fitted.length - 2] = 0;
		fitted[fitted.length - 1] = 0;
		return fitted;
	}

	private static int[] getLineOffsetIndices(byte[] bytes) {
		if (bytes.length < 2) {
			return new int[0];
		} else {
			int[] lineOffsets = new int[bytes.length];
			int lineIndex = 0;
			int i = 0;
			int n = (bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
			while (n > 0) {
				lineOffsets[lineIndex++] = i;
				i += n;
				if (i + 1 < bytes.length) {
					n = (bytes[i] & 0xff) | ((bytes[i + 1] << 8) & 0xff00);
				} else {
					n = 0;
				}
			}
			int[] result = new int[lineIndex];
			System.arraycopy(lineOffsets, 0, result, 0, result.length);
			return result;
		}
	}

	/**
	 * Retrieves the line offsets in the byte code
	 * 
	 * @return The indices in the byte array representing the start of the code lines. A code line starts with a word
	 *         whose value represents the byte length of that line.
	 */
	public int[] getLineOffsetIndices() {
		return getLineOffsetIndices(getBytes());
	}

	/**
	 * Replaces line pointers (0x1d) with absolute line numbers (0x1e)
	 * 
	 * @throws BasicException
	 *             When byte code interpretation is faulty
	 */
	public void resolveLinePointers() throws BasicException {
		new LinePointerResolver().resolve(this);
	}

	@Override
	public String toString() {
		return new LocomotiveBasicByteCodeFormatter().format(this).toString();
	}

	private class LinePointerResolver extends LocomotiveBasicDecompiler {

		public LinePointerResolver() {
		}

		public void resolve(LocomotiveBasicByteCode byteCode) throws BasicException {
			decompile(byteCode);
		}

		@Override
		protected void encounteredLinePointer(int bytecodeOffset, int addressPointer, int lineNumber) {
			setByte(bytecodeOffset, (byte) 0x1e);
			setWord(bytecodeOffset + 1, lineNumber);
		}

	}

}