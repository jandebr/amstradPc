package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicDecompilationException;
import org.maia.amstrad.basic.BasicDecompiler;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords.BasicKeyword;
import org.maia.amstrad.basic.locomotive.source.FloatingPointNumberToken;

public class LocomotiveBasicDecompiler extends LocomotiveBasicProcessor implements BasicDecompiler {

	private byte[] byteCode;

	private int byteCodeIndex;

	private boolean RSXenabled;

	public LocomotiveBasicDecompiler() {
		setRSXenabled(false); // default
	}

	@Override
	public CharSequence decompile(byte[] byteCode) throws BasicDecompilationException {
		init(byteCode);
		StringBuilder sourceCode = new StringBuilder(2048);
		int i0 = byteCodeIndex;
		int announcedLineLengthInBytes = nextWord();
		while (announcedLineLengthInBytes > 0) {
			int lineNumber = nextWord();
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
		return sourceCode;
	}

	protected void init(byte[] byteCode) {
		this.byteCode = byteCode;
		this.byteCodeIndex = 0;
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
					line.append(':');
				}
			} else if (b >= 0x02 && b <= 0x0d) {
				// variable
				nextWord(); // memory offset
				line.append(nextSymbolicName());
				if (b == 0x02) {
					line.append('%'); // integer variable
				} else if (b == 0x03) {
					line.append('$'); // string variable
				} else if (b == 0x04) {
					line.append('!'); // floating point variable
				}
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
					line.append(v); // decimal
				} else if (b == 0x1b) {
					line.append("&X"); // binary
					line.append(Integer.toBinaryString(v));
				} else if (b == 0x1c) {
					line.append("&"); // hexadecimal
					line.append(Integer.toHexString(v));
				} else if (b == 0x1d) {
					line.append(wordAt(v - BasicRuntime.MEMORY_ADDRESS_START_OF_PROGRAM + 3)); // line pointer
				} else if (b == 0x1e) {
					line.append(v); // line number
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
			} else if (b == 0xee) {
				line.append('>');
			} else if (b == 0xef) {
				line.append('=');
			} else if (b == 0xf0) {
				line.append(">=");
			} else if (b == 0xf1) {
				line.append('<');
			} else if (b == 0xf2) {
				line.append("<>");
			} else if (b == 0xf3) {
				line.append("<=");
			} else if (b == 0xf4) {
				line.append('+');
			} else if (b == 0xf5) {
				line.append('-');
			} else if (b == 0xf6) {
				line.append('*');
			} else if (b == 0xf7) {
				line.append('/');
			} else if (b == 0xf8) {
				line.append('^');
			} else if (b == 0xf9) {
				line.append('\\');
			} else if (b == 0xfa) {
				line.append("AND");
			} else if (b == 0xfb) {
				line.append("MOD");
			} else if (b == 0xfc) {
				line.append("OR");
			} else if (b == 0xfd) {
				line.append("XOR");
			} else if (b == 0xfe) {
				line.append("NOT");
			} else {
				// keyword
				BasicKeyword keyword = b < 0xff ? getBasicKeywords().getKeyword((byte) b)
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
		BasicKeyword keyword = getBasicKeywords().getKeyword(byteCode[byteCodeIndex]);
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
		// Mantissa and sign
		long m1 = nextByte();
		long m2 = nextByte();
		long m3 = nextByte();
		long m4 = nextByte();
		long sign = (m4 & 0x80) == 0 ? 1L : -1L;
		long mantissa = (m4 | 0x80) << 24 | m3 << 16 | m2 << 8 | m1;
		// Exponent
		int e = nextByte() - 128;
		// Value
		return sign * mantissa / Math.pow(2, 32 - e);
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