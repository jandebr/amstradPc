package org.maia.amstrad.pc.basic;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocomotiveBasicDecompiler implements BasicDecompiler {

	private short[] byteCode;

	private int byteCodeIndex;

	private TokenMap tokenMap;

	private NumberFormat floatingPointFormat;

	private boolean RSXenabled;

	public LocomotiveBasicDecompiler() {
		this.tokenMap = createTokenMap();
		this.floatingPointFormat = createFloatingPointFormat();
		setRSXenabled(false); // default
	}

	private TokenMap createTokenMap() {
		TokenMap map = new TokenMap();
		// Basic keywords
		map.register(new Token((short) 0x80, "AFTER"));
		map.register(new Token((short) 0x81, "AUTO"));
		map.register(new Token((short) 0x82, "BORDER"));
		map.register(new Token((short) 0x83, "CALL"));
		map.register(new Token((short) 0x84, "CAT"));
		map.register(new Token((short) 0x85, "CHAIN"));
		map.register(new Token((short) 0x86, "CLEAR"));
		map.register(new Token((short) 0x87, "CLG"));
		map.register(new Token((short) 0x88, "CLOSEIN"));
		map.register(new Token((short) 0x89, "CLOSEOUT"));
		map.register(new Token((short) 0x8a, "CLS"));
		map.register(new Token((short) 0x8b, "CONT"));
		map.register(new Token((short) 0x8c, "DATA"));
		map.register(new Token((short) 0x8d, "DEF"));
		map.register(new Token((short) 0x8e, "DEFINT"));
		map.register(new Token((short) 0x8f, "DEFREAL"));
		map.register(new Token((short) 0x90, "DEFSTR"));
		map.register(new Token((short) 0x91, "DEG"));
		map.register(new Token((short) 0x92, "DELETE"));
		map.register(new Token((short) 0x93, "DIM"));
		map.register(new Token((short) 0x94, "DRAW"));
		map.register(new Token((short) 0x95, "DRAWR"));
		map.register(new Token((short) 0x96, "EDIT"));
		map.register(new Token((short) 0x97, "ELSE"));
		map.register(new Token((short) 0x98, "END"));
		map.register(new Token((short) 0x99, "ENT"));
		map.register(new Token((short) 0x9a, "ENV"));
		map.register(new Token((short) 0x9b, "ERASE"));
		map.register(new Token((short) 0x9c, "ERROR"));
		map.register(new Token((short) 0x9d, "EVERY"));
		map.register(new Token((short) 0x9e, "FOR"));
		map.register(new Token((short) 0x9f, "GOSUB"));
		map.register(new Token((short) 0xa0, "GOTO"));
		map.register(new Token((short) 0xa1, "IF"));
		map.register(new Token((short) 0xa2, "INK"));
		map.register(new Token((short) 0xa3, "INPUT"));
		map.register(new Token((short) 0xa4, "KEY"));
		map.register(new Token((short) 0xa5, "LET"));
		map.register(new Token((short) 0xa6, "LINE"));
		map.register(new Token((short) 0xa7, "LIST"));
		map.register(new Token((short) 0xa8, "LOAD"));
		map.register(new Token((short) 0xa9, "LOCATE"));
		map.register(new Token((short) 0xaa, "MEMORY"));
		map.register(new Token((short) 0xab, "MERGE"));
		map.register(new Token((short) 0xac, "MID$"));
		map.register(new Token((short) 0xad, "MODE"));
		map.register(new Token((short) 0xae, "MOVE"));
		map.register(new Token((short) 0xaf, "MOVER"));
		map.register(new Token((short) 0xb0, "NEXT"));
		map.register(new Token((short) 0xb1, "NEW"));
		map.register(new Token((short) 0xb2, "ON"));
		map.register(new Token((short) 0xb3, "ON BREAK"));
		map.register(new Token((short) 0xb4, "ON ERROR GOTO"));
		map.register(new Token((short) 0xb5, "SQ"));
		map.register(new Token((short) 0xb6, "OPENIN"));
		map.register(new Token((short) 0xb7, "OPENOUT"));
		map.register(new Token((short) 0xb8, "ORIGIN"));
		map.register(new Token((short) 0xb9, "OUT"));
		map.register(new Token((short) 0xba, "PAPER"));
		map.register(new Token((short) 0xbb, "PEN"));
		map.register(new Token((short) 0xbc, "PLOT"));
		map.register(new Token((short) 0xbd, "PLOTR"));
		map.register(new Token((short) 0xbe, "POKE"));
		map.register(new Token((short) 0xbf, "PRINT"));
		map.register(new Token((short) 0xc0, "'"));
		map.register(new Token((short) 0xc1, "RAD"));
		map.register(new Token((short) 0xc2, "RANDOMIZE"));
		map.register(new Token((short) 0xc3, "READ"));
		map.register(new Token((short) 0xc4, "RELEASE"));
		map.register(new Token((short) 0xc5, "REM"));
		map.register(new Token((short) 0xc6, "RENUM"));
		map.register(new Token((short) 0xc7, "RESTORE"));
		map.register(new Token((short) 0xc8, "RESUME"));
		map.register(new Token((short) 0xc9, "RETURN"));
		map.register(new Token((short) 0xca, "RUN"));
		map.register(new Token((short) 0xcb, "SAVE"));
		map.register(new Token((short) 0xcc, "SOUND"));
		map.register(new Token((short) 0xcd, "SPEED"));
		map.register(new Token((short) 0xce, "STOP"));
		map.register(new Token((short) 0xcf, "SYMBOL"));
		map.register(new Token((short) 0xd0, "TAG"));
		map.register(new Token((short) 0xd1, "TAGOFF"));
		map.register(new Token((short) 0xd2, "TROFF"));
		map.register(new Token((short) 0xd3, "TRON"));
		map.register(new Token((short) 0xd4, "WAIT"));
		map.register(new Token((short) 0xd5, "WEND"));
		map.register(new Token((short) 0xd6, "WHILE"));
		map.register(new Token((short) 0xd7, "WIDTH"));
		map.register(new Token((short) 0xd8, "WINDOW"));
		map.register(new Token((short) 0xd9, "WRITE"));
		map.register(new Token((short) 0xda, "ZONE"));
		map.register(new Token((short) 0xdb, "DI"));
		map.register(new Token((short) 0xdc, "EI"));
		map.register(new Token((short) 0xdd, "FILL"));
		map.register(new Token((short) 0xde, "GRAPHICS"));
		map.register(new Token((short) 0xdf, "MASK"));
		map.register(new Token((short) 0xe0, "FRAME"));
		map.register(new Token((short) 0xe1, "CURSOR"));
		map.register(new Token((short) 0xe3, "ERL"));
		map.register(new Token((short) 0xe4, "FN"));
		map.register(new Token((short) 0xe5, "SPC"));
		map.register(new Token((short) 0xe6, "STEP"));
		map.register(new Token((short) 0xe7, "SWAP"));
		map.register(new Token((short) 0xea, "TAB"));
		map.register(new Token((short) 0xeb, "THEN"));
		map.register(new Token((short) 0xec, "TO"));
		map.register(new Token((short) 0xed, "USING"));
		// Additional keywords
		map.register(new Token((short) 0xff, (short) 0x00, "ABS"));
		map.register(new Token((short) 0xff, (short) 0x01, "ASC"));
		map.register(new Token((short) 0xff, (short) 0x02, "ATN"));
		map.register(new Token((short) 0xff, (short) 0x03, "CHR$"));
		map.register(new Token((short) 0xff, (short) 0x04, "CINT"));
		map.register(new Token((short) 0xff, (short) 0x05, "COS"));
		map.register(new Token((short) 0xff, (short) 0x06, "CREAL"));
		map.register(new Token((short) 0xff, (short) 0x07, "EXP"));
		map.register(new Token((short) 0xff, (short) 0x08, "FIX"));
		map.register(new Token((short) 0xff, (short) 0x09, "FRE"));
		map.register(new Token((short) 0xff, (short) 0x0a, "INKEY"));
		map.register(new Token((short) 0xff, (short) 0x0b, "INP"));
		map.register(new Token((short) 0xff, (short) 0x0c, "INT"));
		map.register(new Token((short) 0xff, (short) 0x0d, "JOY"));
		map.register(new Token((short) 0xff, (short) 0x0e, "LEN"));
		map.register(new Token((short) 0xff, (short) 0x0f, "LOG"));
		map.register(new Token((short) 0xff, (short) 0x10, "LOG10"));
		map.register(new Token((short) 0xff, (short) 0x11, "LOWER$"));
		map.register(new Token((short) 0xff, (short) 0x12, "PEEK"));
		map.register(new Token((short) 0xff, (short) 0x13, "REMAIN"));
		map.register(new Token((short) 0xff, (short) 0x14, "SGN"));
		map.register(new Token((short) 0xff, (short) 0x15, "SIN"));
		map.register(new Token((short) 0xff, (short) 0x16, "SPACE$"));
		map.register(new Token((short) 0xff, (short) 0x17, "SQ"));
		map.register(new Token((short) 0xff, (short) 0x18, "SQR"));
		map.register(new Token((short) 0xff, (short) 0x19, "STR$"));
		map.register(new Token((short) 0xff, (short) 0x1a, "TAN"));
		map.register(new Token((short) 0xff, (short) 0x1b, "UNT"));
		map.register(new Token((short) 0xff, (short) 0x1c, "UPPER$"));
		map.register(new Token((short) 0xff, (short) 0x1d, "VAL"));
		map.register(new Token((short) 0xff, (short) 0x40, "EOF"));
		map.register(new Token((short) 0xff, (short) 0x41, "ERR"));
		map.register(new Token((short) 0xff, (short) 0x42, "HIMEM"));
		map.register(new Token((short) 0xff, (short) 0x43, "INKEY$"));
		map.register(new Token((short) 0xff, (short) 0x44, "PI"));
		map.register(new Token((short) 0xff, (short) 0x45, "RND"));
		map.register(new Token((short) 0xff, (short) 0x46, "TIME"));
		map.register(new Token((short) 0xff, (short) 0x47, "XPOS"));
		map.register(new Token((short) 0xff, (short) 0x48, "YPOS"));
		map.register(new Token((short) 0xff, (short) 0x49, "DERR"));
		map.register(new Token((short) 0xff, (short) 0x71, "BIN$"));
		map.register(new Token((short) 0xff, (short) 0x72, "DEC$"));
		map.register(new Token((short) 0xff, (short) 0x73, "HEX$"));
		map.register(new Token((short) 0xff, (short) 0x74, "INSTR"));
		map.register(new Token((short) 0xff, (short) 0x75, "LEFT$"));
		map.register(new Token((short) 0xff, (short) 0x76, "MAX"));
		map.register(new Token((short) 0xff, (short) 0x77, "MIN"));
		map.register(new Token((short) 0xff, (short) 0x78, "POS"));
		map.register(new Token((short) 0xff, (short) 0x79, "RIGHT$"));
		map.register(new Token((short) 0xff, (short) 0x7a, "ROUND"));
		map.register(new Token((short) 0xff, (short) 0x7b, "STRING$"));
		map.register(new Token((short) 0xff, (short) 0x7c, "TEST"));
		map.register(new Token((short) 0xff, (short) 0x7d, "TESTR"));
		map.register(new Token((short) 0xff, (short) 0x7e, "COPYCHR$"));
		map.register(new Token((short) 0xff, (short) 0x7f, "VPOS"));
		return map;
	}

	private NumberFormat createFloatingPointFormat() {
		NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
		fmt.setMaximumFractionDigits(8);
		fmt.setGroupingUsed(false);
		return fmt;
	}

	@Override
	public CharSequence decompile(short[] byteCode) {
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

	protected void init(short[] byteCode) {
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
		short b = nextByte();
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
				line.append(this.floatingPointFormat.format(v));
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
				Token token = b < 0xff ? this.tokenMap.getToken(b) : this.tokenMap.getToken(b, nextByte());
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

	private short nextByte() {
		if (byteCodeIndex >= byteCode.length)
			throw new EndOfInputException();
		return byteCode[byteCodeIndex++];
	}

	private int nextWord() {
		if (byteCodeIndex >= byteCode.length - 1)
			throw new EndOfInputException();
		int word = byteCode[byteCodeIndex++];
		word += 256 * byteCode[byteCodeIndex++];
		return word;
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
		short b;
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

	private static class Token {

		private short prefixByte;

		private short codeByte;

		private CharSequence sourceForm;

		public Token(short codeByte, CharSequence sourceForm) {
			this((short) 0, codeByte, sourceForm);
		}

		public Token(short prefixByte, short codeByte, CharSequence sourceForm) {
			this.prefixByte = prefixByte;
			this.codeByte = codeByte;
			this.sourceForm = sourceForm;
		}

		public short getPrefixByte() {
			return prefixByte;
		}

		public short getCodeByte() {
			return codeByte;
		}

		public CharSequence getSourceForm() {
			return sourceForm;
		}

	}

	private static class TokenMap {

		private Map<Short, Token> internalMap;

		public TokenMap() {
			this.internalMap = new HashMap<Short, Token>(512);
		}

		public void register(Token token) {
			this.internalMap.put(getTokenIndex(token), token);
		}

		public Token getToken(short codeByte) {
			return this.internalMap.get(getTokenIndex(codeByte));
		}

		public Token getToken(short prefixByte, short codeByte) {
			return this.internalMap.get(getTokenIndex(prefixByte, codeByte));
		}

		private Short getTokenIndex(Token token) {
			return getTokenIndex(token.getPrefixByte(), token.getCodeByte());
		}

		private Short getTokenIndex(short codeByte) {
			return getTokenIndex((short) 0, codeByte);
		}

		private Short getTokenIndex(short prefixByte, short codeByte) {
			return Short.valueOf((short) (prefixByte * 256 + codeByte));
		}

	}

}