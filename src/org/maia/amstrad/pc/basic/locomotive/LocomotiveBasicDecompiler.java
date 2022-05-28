package org.maia.amstrad.pc.basic.locomotive;

import java.text.NumberFormat;
import java.util.Locale;

import org.maia.amstrad.pc.basic.BasicDecompiler;
import org.maia.amstrad.pc.basic.locomotive.LocomotiveTokenMap.Token;

public class LocomotiveBasicDecompiler extends LocomotiveBasicProcessor implements BasicDecompiler {

	private byte[] byteCode;

	private int byteCodeIndex;

	private boolean RSXenabled;

	public LocomotiveBasicDecompiler() {
		setRSXenabled(false); // default
	}

	@Override
	public CharSequence decompile(byte[] byteCode) {
		init(byteCode);
		StringBuilder sourceCode = new StringBuilder(2048);
		try {
			int i0 = byteCodeIndex;
			int announcedLineLengthInBytes = nextWord();
			while (announcedLineLengthInBytes > 0) {
				int lineNumber = nextWord();
				CharSequence lineOfCode = nextLineOfCode(lineNumber);
				int lineLengthInBytes = byteCodeIndex - i0;
				if (lineLengthInBytes != announcedLineLengthInBytes) {
					System.err.println("Announced line byte length " + announcedLineLengthInBytes
							+ " does not match actual line byte length " + lineLengthInBytes + " on line number "
							+ lineNumber);
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
		} catch (EndOfInputException e) {
			System.err.println(e);
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

	private CharSequence nextLineOfCode(int lineNumber) {
		StringBuilder line = new StringBuilder(256);
		int bytecodeOffset = byteCodeIndex;
		int b = nextByte();
		while (b != 0x00) {
			int linePositionFrom = line.length();
			if (b == 0x01) {
				// statement seperator
				line.append(':');
			} else if (b >= 0x02 && b <= 0x0d) {
				// variable
				nextWord(); // memory offset, always 0
				line.append(nextSymbolicName());
				if (b == 0x02) {
					line.append('%'); // integer variable
				} else if (b == 0x03) {
					line.append('$'); // string variable
				} else if (b == 0x04) {
					line.append('!'); // floating point variable
				}
			} else if (b >= 0x0e && b <= 0x18) {
				// number constant 0 to 10
				line.append(b - 0x0e);
			} else if (b == 0x19) {
				// 8-bit integer decimal value
				line.append(nextByte());
			} else if (b >= 0x1a && b <= 0x1e) {
				// 16-bit integer
				int v = nextWord();
				if (b == 0x1a || b == 0x1d || b == 0x1e) {
					line.append(v);
				} else if (b == 0x1b) {
					line.append("&X"); // binair
					line.append(Integer.toBinaryString(v));
				} else if (b == 0x1c) {
					line.append("&"); // hexadecimal
					line.append(Integer.toHexString(v));
				}
			} else if (b == 0x1f) {
				// floating point value
				double v = nextFloatingPoint();
				line.append(getFloatingPointFormat().format(v));
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
				// token
				Token token = b < 0xff ? getTokenMap().getToken((byte) b) : getTokenMap().getToken((byte) b,
						(byte) nextByte());
				if (token != null) {
					line.append(token.getSourceForm());
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

	private int nextByte() {
		if (byteCodeIndex >= byteCode.length)
			throw new EndOfInputException();
		return byteCode[byteCodeIndex++] & 0xff;
	}

	private int nextWord() {
		if (byteCodeIndex >= byteCode.length - 1)
			throw new EndOfInputException();
		return (byteCode[byteCodeIndex++] & 0xff) | ((byteCode[byteCodeIndex++] << 8) & 0xff00);
	}

	private double nextFloatingPoint() {
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

	private CharSequence nextSymbolicName() {
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

	@SuppressWarnings("serial")
	public static class EndOfInputException extends RuntimeException {

		public EndOfInputException() {
		}

	}

}