package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicDecompilationException;
import org.maia.amstrad.basic.BasicDecompiler;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.locomotive.token.FloatingPointNumberToken;
import org.maia.amstrad.basic.locomotive.token.FloatingPointTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.token.IntegerTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;

public class LocomotiveBasicDecompiler implements BasicDecompiler, LocomotiveBasicMemoryMap {

	private byte[] byteCode;

	private int byteCodeIndex;

	private boolean RSXenabled;

	public LocomotiveBasicDecompiler() {
		setRSXenabled(false); // default
	}

	@Override
	public LocomotiveBasicSourceCode decompile(BasicByteCode byteCode) throws BasicException {
		if (!byteCode.getLanguage().equals(BasicLanguage.LOCOMOTIVE_BASIC))
			throw new BasicException("Basic language mismatch");
		init(byteCode);
		StringBuilder sourceCode = new StringBuilder(2048);
		int i0 = byteCodeIndex;
		int announcedLineLengthInBytes = nextWord();
		while (announcedLineLengthInBytes > 0) {
			int lineNumber = nextWord();
			encounteredLineNumber(byteCodeIndex - 2, lineNumber);
			CharSequence lineOfCode = nextLineOfCode(lineNumber);
			int lineLengthInBytes = byteCodeIndex - i0;
			if (lineLengthInBytes != announcedLineLengthInBytes) {
				throw new BasicDecompilationException("Announced line byte length " + announcedLineLengthInBytes
						+ " <> actual line byte length " + lineLengthInBytes + " on line number " + lineNumber);
			} else {
				sourceCode.append(lineNumber);
				sourceCode.append(' ');
				sourceCode.append(lineOfCode);
				sourceCode.append('\n');
				addedSourceCodeLine(lineNumber, lineOfCode);
			}
			i0 = byteCodeIndex;
			announcedLineLengthInBytes = nextWord();
		}
		return new LocomotiveBasicSourceCode(sourceCode);
	}

	protected void init(BasicByteCode byteCode) {
		this.byteCode = byteCode.getBytes();
		this.byteCodeIndex = 0;
	}

	protected void encounteredLineNumber(int bytecodeOffset, int lineNumber) {
		// Subclasses may override
	}

	protected void encounteredLineNumberReferenceByAddress(int bytecodeOffset, int addressPointer, int lineNumber) {
		// Subclasses may override
	}

	protected void encounteredLineNumberReferenceByValue(int bytecodeOffset, int lineNumber) {
		// Subclasses may override
	}

	protected void encounteredVariable(int bytecodeOffset, byte variableTypeCode,
			CharSequence variableNameWithoutTypeIndicator) {
		// Subclasses may override
	}

	protected void addedSourceCodeToken(int lineNumber, CharSequence lineSoFar, int linePositionFrom,
			int linePositionUntil, int bytecodeOffset, int bytecodeLength) {
		// Subclasses may override
	}

	protected void addedSourceCodeLine(int lineNumber, CharSequence lineOfCode) {
		// Subclasses may override
	}

	private CharSequence nextLineOfCode(int lineNumber) throws EndOfByteCodeException {
		StringBuilder line = new StringBuilder(256);
		int bytecodeOffset = byteCodeIndex;
		int b = nextByte();
		while (b != 0x00) {
			int linePositionFrom = line.length();
			if (b == 0x01) {
				// statement seperator
				if (!nextByteIsKeywordPrecededByInstructionSeparator()) {
					line.append(InstructionSeparatorToken.SEPARATOR);
				}
			} else if (b >= 0x02 && b <= 0x0d) {
				// variable
				nextWord(); // memory offset
				CharSequence varName = nextSymbolicName();
				line.append(varName);
				if (b == 0x02) {
					line.append(IntegerTypedVariableToken.TYPE_INDICATOR); // integer variable
				} else if (b == 0x03) {
					line.append(StringTypedVariableToken.TYPE_INDICATOR); // string variable
				} else if (b == 0x04) {
					line.append(FloatingPointTypedVariableToken.TYPE_INDICATOR); // floating point variable
				}
				encounteredVariable(bytecodeOffset, (byte) b, varName);
			} else if (b >= 0x0e && b <= 0x18) {
				// number constant 0 to 10 (although 10 is usually 0x19 0x0a)
				line.append(b - 0x0e);
			} else if (b == 0x19) {
				// 8-bit integer decimal value
				line.append(nextByte());
			} else if (b >= 0x1a && b <= 0x1e) {
				// 16-bit integer
				int v = nextWord();
				if (b == 0x1a) {
					line.append(LocomotiveBasicNumericRepresentation.wordToInteger(v)); // signed decimal
				} else if (b == 0x1b) {
					line.append("&X"); // unsigned binary
					line.append(Integer.toBinaryString(v));
				} else if (b == 0x1c) {
					line.append("&"); // unsigned hexadecimal
					line.append(Integer.toHexString(v));
				} else if (b == 0x1d) {
					int lineNr = wordAt(v - ADDRESS_BYTECODE_START + 3); // line pointer (to preceding 0x00)
					line.append(lineNr);
					encounteredLineNumberReferenceByAddress(bytecodeOffset, v, lineNr);
				} else if (b == 0x1e) {
					line.append(v); // line number reference
					encounteredLineNumberReferenceByValue(bytecodeOffset, v);
				}
			} else if (b == 0x1f) {
				// floating point value
				double v = nextFloatingPoint();
				line.append(FloatingPointNumberToken.format(v));
			} else if (b >= 0x20 && b <= 0x7e && !(isRSXenabled() && b == 0x7c)) {
				// ASCII printable symbols
				line.append((char) b);
			} else if (isRSXenabled() && b == 0x7c) {
				// RSX command
				nextByte(); // byte offset
				line.append('|');
				line.append(nextSymbolicName());
			} else if (b >= 0xee && b <= 0xfe) {
				// operator
				line.append(getBasicOperators().getOperator((byte) b));
			} else {
				// keyword
				LocomotiveBasicKeyword keyword = b < 0xff ? getBasicKeywords().getKeyword((byte) b)
						: getBasicKeywords().getKeyword((byte) b, (byte) nextByte());
				if (keyword != null) {
					line.append(keyword.getSourceForm());
				} else {
					line.append("[?!:").append(b).append(']');
				}
			}
			int linePositionUntil = line.length() - 1;
			if (linePositionUntil >= linePositionFrom) {
				int bytecodeLength = byteCodeIndex - bytecodeOffset;
				addedSourceCodeToken(lineNumber, line, linePositionFrom, linePositionUntil, bytecodeOffset,
						bytecodeLength);
				bytecodeOffset = byteCodeIndex;
			}
			b = nextByte();
		}
		return line;
	}

	private boolean nextByteIsKeywordPrecededByInstructionSeparator() {
		if (byteCodeIndex >= byteCode.length)
			return false;
		LocomotiveBasicKeyword keyword = getBasicKeywords().getKeyword(byteCode[byteCodeIndex]);
		if (keyword == null)
			return false;
		return keyword.isPrecededByInstructionSeparator();
	}

	private int nextByte() throws EndOfByteCodeException {
		if (byteCodeIndex >= byteCode.length)
			throw new EndOfByteCodeException();
		return byteCode[byteCodeIndex++] & 0xff;
	}

	private int nextWord() throws EndOfByteCodeException {
		int word = wordAt(byteCodeIndex);
		byteCodeIndex += 2;
		return word;
	}

	private int wordAt(int index) throws EndOfByteCodeException {
		if (index >= byteCode.length - 1)
			throw new EndOfByteCodeException();
		return (byteCode[index] & 0xff) | ((byteCode[index + 1] << 8) & 0xff00);
	}

	private double nextFloatingPoint() throws EndOfByteCodeException {
		// Mantissa and exponent
		byte m1 = (byte) nextByte();
		byte m2 = (byte) nextByte();
		byte m3 = (byte) nextByte();
		byte m4 = (byte) nextByte();
		byte e = (byte) nextByte();
		return LocomotiveBasicNumericRepresentation.bytesToFloatingPoint(m1, m2, m3, m4, e);
	}

	private CharSequence nextSymbolicName() throws EndOfByteCodeException {
		StringBuilder name = new StringBuilder(16);
		int b;
		do {
			b = nextByte();
			char c = (char) (b & 0x7f);
			name.append(c);
		} while (b < 128);
		return name;
	}

	private LocomotiveBasicKeywords getBasicKeywords() {
		return LocomotiveBasicKeywords.getInstance();
	}

	private LocomotiveBasicOperators getBasicOperators() {
		return LocomotiveBasicOperators.getInstance();
	}

	public boolean isRSXenabled() {
		return RSXenabled;
	}

	public void setRSXenabled(boolean enabled) {
		RSXenabled = enabled;
	}

	private static class EndOfByteCodeException extends BasicDecompilationException {

		public EndOfByteCodeException() {
			super("Unfinished byte code");
		}

	}

}