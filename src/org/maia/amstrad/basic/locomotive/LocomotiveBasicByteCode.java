package org.maia.amstrad.basic.locomotive;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;

public class LocomotiveBasicByteCode extends BasicByteCode {

	private static byte[] fit(byte[] bytes) {
		int i = getIndexOfLastLineOffset(bytes);
		if (i < 0) {
			// no lines
			return new byte[2];
		} else {
			byte[] fitted = null;
			int n = (bytes[i] & 0xff) | ((bytes[i + 1] << 8) & 0xff00);
			i += n;
			if (i == bytes.length - 2) {
				fitted = bytes; // already fitted
			} else {
				fitted = new byte[i + 2];
				System.arraycopy(bytes, 0, fitted, 0, Math.min(fitted.length, bytes.length));
			}
			// last word should be 0x0000
			fitted[fitted.length - 2] = 0;
			fitted[fitted.length - 1] = 0;
			return fitted;
		}
	}

	private static int getIndexOfLastLineOffset(byte[] bytes) {
		int index = -1;
		if (bytes.length >= 2) {
			int i = 0;
			int n = (bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);
			while (n > 0) {
				index = i;
				i += n;
				if (i + 1 < bytes.length) {
					n = (bytes[i] & 0xff) | ((bytes[i + 1] << 8) & 0xff00);
				} else {
					n = 0;
				}
			}
		}
		return index;
	}

	public LocomotiveBasicByteCode(byte[] bytes) {
		super(fit(bytes));
	}

	@Override
	public final BasicLanguage getLanguage() {
		return BasicLanguage.LOCOMOTIVE_BASIC;
	}

	@Override
	public LocomotiveBasicByteCode clone() {
		return (LocomotiveBasicByteCode) super.clone();
	}

	@Override
	public String toString() {
		return new LocomotiveBasicByteCodeFormatter().format(this).toString();
	}

	@Override
	public synchronized int getLineCount() {
		if (getByteCount() < 4) {
			return 0;
		} else {
			int lineCount = 0;
			int i = 0;
			int n = getWord(0);
			while (n > 0) {
				lineCount++;
				i += n;
				if (i + 1 < getByteCount()) {
					n = getWord(i);
				} else {
					n = 0;
				}
			}
			return lineCount;
		}
	}

	@Override
	public synchronized int getSmallestLineNumber() {
		if (getByteCount() < 4) {
			return -1;
		} else {
			return getWord(2);
		}
	}

	@Override
	public synchronized int getLargestLineNumber() {
		int i = getIndexOfLastLineOffset();
		if (i < 0) {
			return -1; // no lines
		} else {
			return getWord(i + 2);
		}
	}

	@Override
	public synchronized List<Integer> getAscendingLineNumbers() {
		int[] indices = getLineOffsetIndices();
		if (indices.length == 0) {
			return Collections.emptyList();
		} else {
			List<Integer> lineNumbers = new Vector<Integer>(indices.length);
			for (int i = 0; i < indices.length; i++) {
				lineNumbers.add(getWord(indices[i] + 2));
			}
			return lineNumbers;
		}
	}

	/**
	 * Retrieves the line offsets in the byte code
	 * 
	 * @return The indices in the byte array representing the start of the code lines. A code line starts with a word
	 *         whose value represents the byte length of that line.
	 */
	public synchronized int[] getLineOffsetIndices() {
		int bc = getByteCount();
		if (bc < 4) {
			return new int[0];
		} else {
			int[] lineOffsets = new int[bc];
			int lineIndex = 0;
			int i = 0;
			int n = getWord(0);
			while (n > 0) {
				lineOffsets[lineIndex++] = i;
				i += n;
				if (i + 1 < bc) {
					n = getWord(i);
				} else {
					n = 0;
				}
			}
			int[] result = new int[lineIndex];
			System.arraycopy(lineOffsets, 0, result, 0, result.length);
			return result;
		}
	}

	public synchronized int getIndexOfLastLineOffset() {
		return getIndexOfLastLineOffset(getBytes());
	}

	@Override
	public synchronized void renum(BasicLineNumberLinearMapping mapping) throws BasicException {
		if (isEmpty())
			return;
		// Map line number references (must go first)
		new LineReferenceMapper(mapping).map();
		// Map line numbers (must go second)
		int[] indices = getLineOffsetIndices();
		for (int i = 0; i < indices.length; i++) {
			int j = indices[i] + 2;
			int lineNumber = getWord(j);
			if (mapping.isMapped(lineNumber)) {
				setWord(j, mapping.getNewLineNumber(lineNumber));
			}
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
		int[] indices = getLineOffsetIndices();
		int[] indicesOther = other.getLineOffsetIndices();
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

	/**
	 * Replaces absolute line number references with line pointers.
	 * 
	 * This operation preserves the exact byte count.
	 * 
	 * @throws BasicException
	 *             When byte code interpretation is faulty
	 */
	public synchronized void updateLineReferencesToPointers() throws BasicException {
		new LineReferencePointerMapper().map();
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

		private BasicLineNumberLinearMapping mapping;

		private int currentLineNumber;

		public LineReferenceMapper(BasicLineNumberLinearMapping mapping) {
			this.mapping = mapping;
		}

		public void map() throws BasicException {
			decompile(LocomotiveBasicByteCode.this);
		}

		@Override
		protected void encounteredLineNumber(int bytecodeOffset, int lineNumber) {
			setCurrentLineNumber(lineNumber);
		}

		@Override
		protected void encounteredLineNumberReferenceByAddress(int bytecodeOffset, int addressPointer, int lineNumber) {
			if (getMapping().isMapped(lineNumber)) {
				setByte(bytecodeOffset, (byte) 0x1e); // make absolute
				setWord(bytecodeOffset + 1, getMapping().getNewLineNumber(lineNumber));
			}
		}

		@Override
		protected void encounteredLineNumberReferenceByValue(int bytecodeOffset, int lineNumber) {
			if (getMapping().isMapped(lineNumber)) {
				setWord(bytecodeOffset + 1, getMapping().getNewLineNumber(lineNumber));
			}
		}

		private BasicLineNumberLinearMapping getMapping() {
			return mapping;
		}

		private int getCurrentLineNumber() {
			return currentLineNumber;
		}

		private void setCurrentLineNumber(int currentLineNumber) {
			this.currentLineNumber = currentLineNumber;
		}

	}

	private class LineReferencePointerMapper extends LocomotiveBasicDecompiler {

		private Map<Integer, Integer> lineNumberPointerMap; // maps absolute line numbers to memory addresses

		public LineReferencePointerMapper() {
			this.lineNumberPointerMap = buildLineNumberPointerMap();
		}

		public void map() throws BasicException {
			decompile(LocomotiveBasicByteCode.this);
		}

		@Override
		protected void encounteredLineNumberReferenceByValue(int bytecodeOffset, int lineNumber) {
			if (getLineNumberPointerMap().containsKey(lineNumber)) {
				setByte(bytecodeOffset, (byte) 0x1d); // make pointer
				setWord(bytecodeOffset + 1, getLineNumberPointerMap().get(lineNumber));
			}
		}

		private Map<Integer, Integer> buildLineNumberPointerMap() {
			List<Integer> lineNumbers = getAscendingLineNumbers();
			int[] lineOffsetIndices = getLineOffsetIndices();
			int memoryBaseOffset = ADDRESS_BYTECODE_START - 1;
			Map<Integer, Integer> map = new HashMap<Integer, Integer>(lineNumbers.size());
			for (int i = 0; i < lineNumbers.size(); i++) {
				map.put(lineNumbers.get(i), memoryBaseOffset + lineOffsetIndices[i]);
			}
			return map;
		}

		private Map<Integer, Integer> getLineNumberPointerMap() {
			return lineNumberPointerMap;
		}

	}

}