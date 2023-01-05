package org.maia.amstrad.basic.locomotive;

import java.util.HashMap;
import java.util.Map;

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

	@Override
	public LocomotiveBasicByteCode clone() {
		return (LocomotiveBasicByteCode) super.clone();
	}

	@Override
	public String toString() {
		return new LocomotiveBasicByteCodeFormatter().format(this).toString();
	}

	/**
	 * Retrieves the line offsets in the byte code
	 * 
	 * @return The indices in the byte array representing the start of the code lines. A code line starts with a word
	 *         whose value represents the byte length of that line.
	 */
	public synchronized int[] getLineOffsetIndices() {
		return getLineOffsetIndices(getBytes());
	}

	public synchronized void renum(int lineNumberStart, int lineNumberStep) throws BasicException {
		int[] indices = getLineOffsetIndices(getBytes());
		if (indices.length == 0)
			return;
		// Create mapping
		Map<Integer, Integer> lineNumberMapping = new HashMap<Integer, Integer>(indices.length);
		for (int i = 0; i < indices.length; i++) {
			int currentLineNumber = getWord(indices[i] + 2);
			int newLineNumber = lineNumberStart + i * lineNumberStep;
			lineNumberMapping.put(currentLineNumber, newLineNumber);
		}
		// Map line number references
		new LineReferenceMapper(lineNumberMapping).map();
		// Map line numbers
		for (int i = 0; i < indices.length; i++) {
			int j = indices[i] + 2;
			setWord(j, lineNumberMapping.get(getWord(j)));
		}
	}

	public synchronized void merge(LocomotiveBasicByteCode byteCodeToMerge) throws BasicException {
		// Sanitize line number references
		sanitizeLineReferences();
		LocomotiveBasicByteCode other = byteCodeToMerge.clone();
		other.sanitizeLineReferences();
		// Merge
		int bc = getByteCount(), bcOther = other.getByteCount();
		byte[] mergedBytes = new byte[bc + bcOther - 2];
		int mbc = 0;
		int[] indices = getLineOffsetIndices(getBytes());
		int[] indicesOther = getLineOffsetIndices(other.getBytes());
		int n = indices.length, nOther = indicesOther.length;
		int i = 0, iOther = 0;
		while (i < n && iOther < nOther) {
			int from = indices[i];
			int fromOther = indicesOther[iOther];
			int to = i + 1 < n ? indices[i + 1] : bc - 2;
			int toOther = iOther + 1 < nOther ? indicesOther[iOther + 1] : bcOther - 2;
			int lnr = getWord(from + 2);
			int lnrOther = other.getWord(fromOther + 2);
			if (lnr < lnrOther) {
				System.arraycopy(getBytes(), from, mergedBytes, mbc, to - from);
				mbc += to - from;
				i++;
			} else {
				System.arraycopy(other.getBytes(), fromOther, mergedBytes, mbc, toOther - fromOther);
				mbc += toOther - fromOther;
				iOther++;
				if (lnr == lnrOther)
					i++;
			}
		}
		if (i < n) {
			System.arraycopy(getBytes(), indices[i], mergedBytes, mbc, bc - indices[i]);
		} else if (iOther < nOther) {
			System.arraycopy(other.getBytes(), indicesOther[iOther], mergedBytes, mbc, bcOther - indicesOther[iOther]);
		}
		setBytes(fit(mergedBytes));
	}

	/**
	 * Sanitizes this byte code so as to remove any runtime modifications.
	 * 
	 * Sanitizing involves the following operations :
	 * <ul>
	 * <li>Replaces line pointers (0x1d) with absolute line numbers (0x1e)</li>
	 * <li>Clears the memory offsets for variables</li>
	 * </ul>
	 * 
	 * Sanitization preserves the exact byte count.
	 * 
	 * @throws BasicException
	 *             When byte code interpretation is faulty
	 */
	public synchronized void sanitize() throws BasicException {
		new LineReferenceAndVariableSanitizer().sanitize();
	}

	private void sanitizeLineReferences() throws BasicException {
		new LineReferenceSanitizer().sanitize();
	}

	private abstract class Sanitizer extends LocomotiveBasicDecompiler {

		protected Sanitizer() {
		}

		public void sanitize() throws BasicException {
			decompile(LocomotiveBasicByteCode.this);
		}

	}

	private class LineReferenceSanitizer extends Sanitizer {

		public LineReferenceSanitizer() {
		}

		@Override
		protected void encounteredLineNumberReferenceByAddress(int bytecodeOffset, int addressPointer, int lineNumber) {
			setByte(bytecodeOffset, (byte) 0x1e); // make absolute
			setWord(bytecodeOffset + 1, lineNumber);
		}

	}

	private class LineReferenceAndVariableSanitizer extends LineReferenceSanitizer {

		public LineReferenceAndVariableSanitizer() {
		}

		@Override
		protected void encounteredVariable(int bytecodeOffset, byte variableTypeCode,
				CharSequence variableNameWithoutTypeIndicator) {
			setWord(bytecodeOffset + 1, 0); // clear memory offset
		}

	}

	private class LineReferenceMapper extends LocomotiveBasicDecompiler {

		Map<Integer, Integer> lineNumberMapping;

		public LineReferenceMapper(Map<Integer, Integer> lineNumberMapping) {
			this.lineNumberMapping = lineNumberMapping;
		}

		public void map() throws BasicException {
			decompile(LocomotiveBasicByteCode.this);
		}

		@Override
		protected void encounteredLineNumberReferenceByAddress(int bytecodeOffset, int addressPointer, int lineNumber) {
			setByte(bytecodeOffset, (byte) 0x1e); // make absolute
			setWord(bytecodeOffset + 1, mapLineNumber(lineNumber));
		}

		@Override
		protected void encounteredLineNumberReferenceByValue(int bytecodeOffset, int lineNumber) {
			setWord(bytecodeOffset + 1, mapLineNumber(lineNumber));
		}

		private int mapLineNumber(int lineNumber) {
			return getLineNumberMapping().get(lineNumber);
		}

		private Map<Integer, Integer> getLineNumberMapping() {
			return lineNumberMapping;
		}

	}

}