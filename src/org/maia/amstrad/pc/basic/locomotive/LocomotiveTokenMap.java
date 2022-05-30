package org.maia.amstrad.pc.basic.locomotive;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocomotiveTokenMap {

	private Map<Integer, Token> byteCodeMap;

	private Map<Character, Set<Token>> firstCharIndex;

	public LocomotiveTokenMap() {
		this.byteCodeMap = new HashMap<Integer, Token>(512);
		this.firstCharIndex = new HashMap<Character, Set<Token>>(26);
		loadBasicTokens();
		loadExtendedTokens();
	}

	private void loadBasicTokens() {
		register(new Token((byte) 0x80, "AFTER"));
		register(new Token((byte) 0x81, "AUTO"));
		register(new Token((byte) 0x82, "BORDER"));
		register(new Token((byte) 0x83, "CALL"));
		register(new Token((byte) 0x84, "CAT"));
		register(new Token((byte) 0x85, "CHAIN"));
		register(new Token((byte) 0x86, "CLEAR"));
		register(new Token((byte) 0x87, "CLG"));
		register(new Token((byte) 0x88, "CLOSEIN"));
		register(new Token((byte) 0x89, "CLOSEOUT"));
		register(new Token((byte) 0x8a, "CLS"));
		register(new Token((byte) 0x8b, "CONT"));
		register(new Token((byte) 0x8c, "DATA"));
		register(new Token((byte) 0x8d, "DEF"));
		register(new Token((byte) 0x8e, "DEFINT"));
		register(new Token((byte) 0x8f, "DEFREAL"));
		register(new Token((byte) 0x90, "DEFSTR"));
		register(new Token((byte) 0x91, "DEG"));
		register(new Token((byte) 0x92, "DELETE"));
		register(new Token((byte) 0x93, "DIM"));
		register(new Token((byte) 0x94, "DRAW"));
		register(new Token((byte) 0x95, "DRAWR"));
		register(new Token((byte) 0x96, "EDIT"));
		register(new Token((byte) 0x97, "ELSE"));
		register(new Token((byte) 0x98, "END"));
		register(new Token((byte) 0x99, "ENT"));
		register(new Token((byte) 0x9a, "ENV"));
		register(new Token((byte) 0x9b, "ERASE"));
		register(new Token((byte) 0x9c, "ERROR"));
		register(new Token((byte) 0x9d, "EVERY"));
		register(new Token((byte) 0x9e, "FOR"));
		register(new Token((byte) 0x9f, "GOSUB"));
		register(new Token((byte) 0xa0, "GOTO"));
		register(new Token((byte) 0xa1, "IF"));
		register(new Token((byte) 0xa2, "INK"));
		register(new Token((byte) 0xa3, "INPUT"));
		register(new Token((byte) 0xa4, "KEY"));
		register(new Token((byte) 0xa5, "LET"));
		register(new Token((byte) 0xa6, "LINE"));
		register(new Token((byte) 0xa7, "LIST"));
		register(new Token((byte) 0xa8, "LOAD"));
		register(new Token((byte) 0xa9, "LOCATE"));
		register(new Token((byte) 0xaa, "MEMORY"));
		register(new Token((byte) 0xab, "MERGE"));
		register(new Token((byte) 0xac, "MID$"));
		register(new Token((byte) 0xad, "MODE"));
		register(new Token((byte) 0xae, "MOVE"));
		register(new Token((byte) 0xaf, "MOVER"));
		register(new Token((byte) 0xb0, "NEXT"));
		register(new Token((byte) 0xb1, "NEW"));
		register(new Token((byte) 0xb2, "ON"));
		register(new Token((byte) 0xb3, "ON BREAK"));
		register(new Token((byte) 0xb4, "ON ERROR GOTO"));
		register(new Token((byte) 0xb5, "SQ"));
		register(new Token((byte) 0xb6, "OPENIN"));
		register(new Token((byte) 0xb7, "OPENOUT"));
		register(new Token((byte) 0xb8, "ORIGIN"));
		register(new Token((byte) 0xb9, "OUT"));
		register(new Token((byte) 0xba, "PAPER"));
		register(new Token((byte) 0xbb, "PEN"));
		register(new Token((byte) 0xbc, "PLOT"));
		register(new Token((byte) 0xbd, "PLOTR"));
		register(new Token((byte) 0xbe, "POKE"));
		register(new Token((byte) 0xbf, "PRINT"));
		register(new Token((byte) 0xc0, "'"));
		register(new Token((byte) 0xc1, "RAD"));
		register(new Token((byte) 0xc2, "RANDOMIZE"));
		register(new Token((byte) 0xc3, "READ"));
		register(new Token((byte) 0xc4, "RELEASE"));
		register(new Token((byte) 0xc5, "REM"));
		register(new Token((byte) 0xc6, "RENUM"));
		register(new Token((byte) 0xc7, "RESTORE"));
		register(new Token((byte) 0xc8, "RESUME"));
		register(new Token((byte) 0xc9, "RETURN"));
		register(new Token((byte) 0xca, "RUN"));
		register(new Token((byte) 0xcb, "SAVE"));
		register(new Token((byte) 0xcc, "SOUND"));
		register(new Token((byte) 0xcd, "SPEED"));
		register(new Token((byte) 0xce, "STOP"));
		register(new Token((byte) 0xcf, "SYMBOL"));
		register(new Token((byte) 0xd0, "TAG"));
		register(new Token((byte) 0xd1, "TAGOFF"));
		register(new Token((byte) 0xd2, "TROFF"));
		register(new Token((byte) 0xd3, "TRON"));
		register(new Token((byte) 0xd4, "WAIT"));
		register(new Token((byte) 0xd5, "WEND"));
		register(new Token((byte) 0xd6, "WHILE"));
		register(new Token((byte) 0xd7, "WIDTH"));
		register(new Token((byte) 0xd8, "WINDOW"));
		register(new Token((byte) 0xd9, "WRITE"));
		register(new Token((byte) 0xda, "ZONE"));
		register(new Token((byte) 0xdb, "DI"));
		register(new Token((byte) 0xdc, "EI"));
		register(new Token((byte) 0xdd, "FILL"));
		register(new Token((byte) 0xde, "GRAPHICS"));
		register(new Token((byte) 0xdf, "MASK"));
		register(new Token((byte) 0xe0, "FRAME"));
		register(new Token((byte) 0xe1, "CURSOR"));
		register(new Token((byte) 0xe3, "ERL"));
		register(new Token((byte) 0xe4, "FN"));
		register(new Token((byte) 0xe5, "SPC"));
		register(new Token((byte) 0xe6, "STEP"));
		register(new Token((byte) 0xe7, "SWAP"));
		register(new Token((byte) 0xea, "TAB"));
		register(new Token((byte) 0xeb, "THEN"));
		register(new Token((byte) 0xec, "TO"));
		register(new Token((byte) 0xed, "USING"));
	}

	private void loadExtendedTokens() {
		register(new Token((byte) 0xff, (byte) 0x00, "ABS"));
		register(new Token((byte) 0xff, (byte) 0x01, "ASC"));
		register(new Token((byte) 0xff, (byte) 0x02, "ATN"));
		register(new Token((byte) 0xff, (byte) 0x03, "CHR$"));
		register(new Token((byte) 0xff, (byte) 0x04, "CINT"));
		register(new Token((byte) 0xff, (byte) 0x05, "COS"));
		register(new Token((byte) 0xff, (byte) 0x06, "CREAL"));
		register(new Token((byte) 0xff, (byte) 0x07, "EXP"));
		register(new Token((byte) 0xff, (byte) 0x08, "FIX"));
		register(new Token((byte) 0xff, (byte) 0x09, "FRE"));
		register(new Token((byte) 0xff, (byte) 0x0a, "INKEY"));
		register(new Token((byte) 0xff, (byte) 0x0b, "INP"));
		register(new Token((byte) 0xff, (byte) 0x0c, "INT"));
		register(new Token((byte) 0xff, (byte) 0x0d, "JOY"));
		register(new Token((byte) 0xff, (byte) 0x0e, "LEN"));
		register(new Token((byte) 0xff, (byte) 0x0f, "LOG"));
		register(new Token((byte) 0xff, (byte) 0x10, "LOG10"));
		register(new Token((byte) 0xff, (byte) 0x11, "LOWER$"));
		register(new Token((byte) 0xff, (byte) 0x12, "PEEK"));
		register(new Token((byte) 0xff, (byte) 0x13, "REMAIN"));
		register(new Token((byte) 0xff, (byte) 0x14, "SGN"));
		register(new Token((byte) 0xff, (byte) 0x15, "SIN"));
		register(new Token((byte) 0xff, (byte) 0x16, "SPACE$"));
		register(new Token((byte) 0xff, (byte) 0x17, "SQ"));
		register(new Token((byte) 0xff, (byte) 0x18, "SQR"));
		register(new Token((byte) 0xff, (byte) 0x19, "STR$"));
		register(new Token((byte) 0xff, (byte) 0x1a, "TAN"));
		register(new Token((byte) 0xff, (byte) 0x1b, "UNT"));
		register(new Token((byte) 0xff, (byte) 0x1c, "UPPER$"));
		register(new Token((byte) 0xff, (byte) 0x1d, "VAL"));
		register(new Token((byte) 0xff, (byte) 0x40, "EOF"));
		register(new Token((byte) 0xff, (byte) 0x41, "ERR"));
		register(new Token((byte) 0xff, (byte) 0x42, "HIMEM"));
		register(new Token((byte) 0xff, (byte) 0x43, "INKEY$"));
		register(new Token((byte) 0xff, (byte) 0x44, "PI"));
		register(new Token((byte) 0xff, (byte) 0x45, "RND"));
		register(new Token((byte) 0xff, (byte) 0x46, "TIME"));
		register(new Token((byte) 0xff, (byte) 0x47, "XPOS"));
		register(new Token((byte) 0xff, (byte) 0x48, "YPOS"));
		register(new Token((byte) 0xff, (byte) 0x49, "DERR"));
		register(new Token((byte) 0xff, (byte) 0x71, "BIN$"));
		register(new Token((byte) 0xff, (byte) 0x72, "DEC$"));
		register(new Token((byte) 0xff, (byte) 0x73, "HEX$"));
		register(new Token((byte) 0xff, (byte) 0x74, "INSTR"));
		register(new Token((byte) 0xff, (byte) 0x75, "LEFT$"));
		register(new Token((byte) 0xff, (byte) 0x76, "MAX"));
		register(new Token((byte) 0xff, (byte) 0x77, "MIN"));
		register(new Token((byte) 0xff, (byte) 0x78, "POS"));
		register(new Token((byte) 0xff, (byte) 0x79, "RIGHT$"));
		register(new Token((byte) 0xff, (byte) 0x7a, "ROUND"));
		register(new Token((byte) 0xff, (byte) 0x7b, "STRING$"));
		register(new Token((byte) 0xff, (byte) 0x7c, "TEST"));
		register(new Token((byte) 0xff, (byte) 0x7d, "TESTR"));
		register(new Token((byte) 0xff, (byte) 0x7e, "COPYCHR$"));
		register(new Token((byte) 0xff, (byte) 0x7f, "VPOS"));
	}

	public void register(Token token) {
		getByteCodeMap().put(getTokenIndex(token), token);
		Character fc = token.getFirstCharacter();
		Set<Token> set = getFirstCharIndex().get(fc);
		if (set == null) {
			set = new HashSet<Token>(8);
			getFirstCharIndex().put(fc, set);
		}
		set.add(token);
	}

	public Token getToken(byte codeByte) {
		return getByteCodeMap().get(getTokenIndex(codeByte));
	}

	public Token getToken(byte prefixByte, byte codeByte) {
		return getByteCodeMap().get(getTokenIndex(prefixByte, codeByte));
	}

	public Set<Token> getTokensStartingWith(char c) {
		Set<Token> set = getFirstCharIndex().get(c);
		if (set == null)
			return Collections.emptySet();
		else
			return set;
	}

	private Integer getTokenIndex(Token token) {
		return getTokenIndex(token.getPrefixByte(), token.getCodeByte());
	}

	private Integer getTokenIndex(byte codeByte) {
		return getTokenIndex((byte) 0, codeByte);
	}

	private Integer getTokenIndex(byte prefixByte, byte codeByte) {
		return Integer.valueOf((prefixByte << 8) & 0xff00 | (codeByte & 0xff));
	}

	private Map<Integer, Token> getByteCodeMap() {
		return byteCodeMap;
	}

	private Map<Character, Set<Token>> getFirstCharIndex() {
		return firstCharIndex;
	}

	public static class Token {

		private byte prefixByte;

		private byte codeByte;

		private String sourceForm;

		public Token(byte codeByte, String sourceForm) {
			this((byte) 0, codeByte, sourceForm);
		}

		public Token(byte prefixByte, byte codeByte, String sourceForm) {
			this.prefixByte = prefixByte;
			this.codeByte = codeByte;
			this.sourceForm = sourceForm;
		}

		@Override
		public String toString() {
			return getSourceForm();
		}

		@Override
		public int hashCode() {
			return 31 + ((sourceForm == null) ? 0 : sourceForm.hashCode());
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			return getSourceForm().equals(other.getSourceForm());
		}

		public Character getFirstCharacter() {
			return getSourceForm().charAt(0);
		}

		public boolean isBasicToken() {
			return getPrefixByte() == 0;
		}

		public boolean isExtendedToken() {
			return !isBasicToken();
		}

		public byte getPrefixByte() {
			return prefixByte;
		}

		public byte getCodeByte() {
			return codeByte;
		}

		public String getSourceForm() {
			return sourceForm;
		}

	}

}