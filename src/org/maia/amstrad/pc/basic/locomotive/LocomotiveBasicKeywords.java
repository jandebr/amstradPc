package org.maia.amstrad.pc.basic.locomotive;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocomotiveBasicKeywords {

	private Map<Integer, BasicKeyword> byteCodeMap;

	private Map<Character, Set<BasicKeyword>> firstCharIndex;

	public static final byte EXTENDED_PREFIX_BYTE = (byte) 0xff; // prefix byte for the extended keywords

	public LocomotiveBasicKeywords() {
		this.byteCodeMap = new HashMap<Integer, BasicKeyword>(512);
		this.firstCharIndex = new HashMap<Character, Set<BasicKeyword>>(26);
		loadBasicKeywords();
		loadExtendedKeywords();
	}

	private void loadBasicKeywords() {
		register(new BasicKeyword((byte) 0x80, "AFTER"));
		register(new BasicKeyword((byte) 0x81, "AUTO"));
		register(new BasicKeyword((byte) 0x82, "BORDER"));
		register(new BasicKeyword((byte) 0x83, "CALL"));
		register(new BasicKeyword((byte) 0x84, "CAT"));
		register(new BasicKeyword((byte) 0x85, "CHAIN"));
		register(new BasicKeyword((byte) 0x86, "CLEAR"));
		register(new BasicKeyword((byte) 0x87, "CLG"));
		register(new BasicKeyword((byte) 0x88, "CLOSEIN"));
		register(new BasicKeyword((byte) 0x89, "CLOSEOUT"));
		register(new BasicKeyword((byte) 0x8a, "CLS"));
		register(new BasicKeyword((byte) 0x8b, "CONT"));
		register(new BasicKeyword((byte) 0x8c, "DATA"));
		register(new BasicKeyword((byte) 0x8d, "DEF"));
		register(new BasicKeyword((byte) 0x8e, "DEFINT"));
		register(new BasicKeyword((byte) 0x8f, "DEFREAL"));
		register(new BasicKeyword((byte) 0x90, "DEFSTR"));
		register(new BasicKeyword((byte) 0x91, "DEG"));
		register(new BasicKeyword((byte) 0x92, "DELETE"));
		register(new BasicKeyword((byte) 0x93, "DIM"));
		register(new BasicKeyword((byte) 0x94, "DRAW"));
		register(new BasicKeyword((byte) 0x95, "DRAWR"));
		register(new BasicKeyword((byte) 0x96, "EDIT"));
		register(new BasicKeyword((byte) 0x97, "ELSE"));
		register(new BasicKeyword((byte) 0x98, "END"));
		register(new BasicKeyword((byte) 0x99, "ENT"));
		register(new BasicKeyword((byte) 0x9a, "ENV"));
		register(new BasicKeyword((byte) 0x9b, "ERASE"));
		register(new BasicKeyword((byte) 0x9c, "ERROR"));
		register(new BasicKeyword((byte) 0x9d, "EVERY"));
		register(new BasicKeyword((byte) 0x9e, "FOR"));
		register(new BasicKeyword((byte) 0x9f, "GOSUB"));
		register(new BasicKeyword((byte) 0xa0, "GOTO"));
		register(new BasicKeyword((byte) 0xa1, "IF"));
		register(new BasicKeyword((byte) 0xa2, "INK"));
		register(new BasicKeyword((byte) 0xa3, "INPUT"));
		register(new BasicKeyword((byte) 0xa4, "KEY"));
		register(new BasicKeyword((byte) 0xa5, "LET"));
		register(new BasicKeyword((byte) 0xa6, "LINE"));
		register(new BasicKeyword((byte) 0xa7, "LIST"));
		register(new BasicKeyword((byte) 0xa8, "LOAD"));
		register(new BasicKeyword((byte) 0xa9, "LOCATE"));
		register(new BasicKeyword((byte) 0xaa, "MEMORY"));
		register(new BasicKeyword((byte) 0xab, "MERGE"));
		register(new BasicKeyword((byte) 0xac, "MID$"));
		register(new BasicKeyword((byte) 0xad, "MODE"));
		register(new BasicKeyword((byte) 0xae, "MOVE"));
		register(new BasicKeyword((byte) 0xaf, "MOVER"));
		register(new BasicKeyword((byte) 0xb0, "NEXT"));
		register(new BasicKeyword((byte) 0xb1, "NEW"));
		register(new BasicKeyword((byte) 0xb2, "ON"));
		register(new BasicKeyword((byte) 0xb3, "ON BREAK"));
		register(new BasicKeyword((byte) 0xb4, "ON ERROR GOTO"));
		register(new BasicKeyword((byte) 0xb5, "SQ"));
		register(new BasicKeyword((byte) 0xb6, "OPENIN"));
		register(new BasicKeyword((byte) 0xb7, "OPENOUT"));
		register(new BasicKeyword((byte) 0xb8, "ORIGIN"));
		register(new BasicKeyword((byte) 0xb9, "OUT"));
		register(new BasicKeyword((byte) 0xba, "PAPER"));
		register(new BasicKeyword((byte) 0xbb, "PEN"));
		register(new BasicKeyword((byte) 0xbc, "PLOT"));
		register(new BasicKeyword((byte) 0xbd, "PLOTR"));
		register(new BasicKeyword((byte) 0xbe, "POKE"));
		register(new BasicKeyword((byte) 0xbf, "PRINT"));
		register(new BasicKeyword((byte) 0xc0, "'"));
		register(new BasicKeyword((byte) 0xc1, "RAD"));
		register(new BasicKeyword((byte) 0xc2, "RANDOMIZE"));
		register(new BasicKeyword((byte) 0xc3, "READ"));
		register(new BasicKeyword((byte) 0xc4, "RELEASE"));
		register(new BasicKeyword((byte) 0xc5, "REM"));
		register(new BasicKeyword((byte) 0xc6, "RENUM"));
		register(new BasicKeyword((byte) 0xc7, "RESTORE"));
		register(new BasicKeyword((byte) 0xc8, "RESUME"));
		register(new BasicKeyword((byte) 0xc9, "RETURN"));
		register(new BasicKeyword((byte) 0xca, "RUN"));
		register(new BasicKeyword((byte) 0xcb, "SAVE"));
		register(new BasicKeyword((byte) 0xcc, "SOUND"));
		register(new BasicKeyword((byte) 0xcd, "SPEED"));
		register(new BasicKeyword((byte) 0xce, "STOP"));
		register(new BasicKeyword((byte) 0xcf, "SYMBOL"));
		register(new BasicKeyword((byte) 0xd0, "TAG"));
		register(new BasicKeyword((byte) 0xd1, "TAGOFF"));
		register(new BasicKeyword((byte) 0xd2, "TROFF"));
		register(new BasicKeyword((byte) 0xd3, "TRON"));
		register(new BasicKeyword((byte) 0xd4, "WAIT"));
		register(new BasicKeyword((byte) 0xd5, "WEND"));
		register(new BasicKeyword((byte) 0xd6, "WHILE"));
		register(new BasicKeyword((byte) 0xd7, "WIDTH"));
		register(new BasicKeyword((byte) 0xd8, "WINDOW"));
		register(new BasicKeyword((byte) 0xd9, "WRITE"));
		register(new BasicKeyword((byte) 0xda, "ZONE"));
		register(new BasicKeyword((byte) 0xdb, "DI"));
		register(new BasicKeyword((byte) 0xdc, "EI"));
		register(new BasicKeyword((byte) 0xdd, "FILL"));
		register(new BasicKeyword((byte) 0xde, "GRAPHICS"));
		register(new BasicKeyword((byte) 0xdf, "MASK"));
		register(new BasicKeyword((byte) 0xe0, "FRAME"));
		register(new BasicKeyword((byte) 0xe1, "CURSOR"));
		register(new BasicKeyword((byte) 0xe3, "ERL"));
		register(new BasicKeyword((byte) 0xe4, "FN"));
		register(new BasicKeyword((byte) 0xe5, "SPC"));
		register(new BasicKeyword((byte) 0xe6, "STEP"));
		register(new BasicKeyword((byte) 0xe7, "SWAP"));
		register(new BasicKeyword((byte) 0xea, "TAB"));
		register(new BasicKeyword((byte) 0xeb, "THEN"));
		register(new BasicKeyword((byte) 0xec, "TO"));
		register(new BasicKeyword((byte) 0xed, "USING"));
	}

	private void loadExtendedKeywords() {
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x00, "ABS"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x01, "ASC"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x02, "ATN"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x03, "CHR$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x04, "CINT"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x05, "COS"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x06, "CREAL"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x07, "EXP"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x08, "FIX"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x09, "FRE"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0a, "INKEY"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0b, "INP"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0c, "INT"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0d, "JOY"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0e, "LEN"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0f, "LOG"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x10, "LOG10"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x11, "LOWER$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x12, "PEEK"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x13, "REMAIN"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x14, "SGN"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x15, "SIN"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x16, "SPACE$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x17, "SQ"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x18, "SQR"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x19, "STR$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1a, "TAN"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1b, "UNT"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1c, "UPPER$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1d, "VAL"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x40, "EOF"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x41, "ERR"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x42, "HIMEM"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x43, "INKEY$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x44, "PI"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x45, "RND"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x46, "TIME"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x47, "XPOS"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x48, "YPOS"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x49, "DERR"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x71, "BIN$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x72, "DEC$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x73, "HEX$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x74, "INSTR"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x75, "LEFT$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x76, "MAX"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x77, "MIN"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x78, "POS"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x79, "RIGHT$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7a, "ROUND"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7b, "STRING$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7c, "TEST"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7d, "TESTR"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7e, "COPYCHR$"));
		register(new BasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7f, "VPOS"));
	}

	public void register(BasicKeyword keyword) {
		getByteCodeMap().put(getKeywordIndex(keyword), keyword);
		Character fc = keyword.getFirstCharacter();
		Set<BasicKeyword> set = getFirstCharIndex().get(fc);
		if (set == null) {
			set = new HashSet<BasicKeyword>(8);
			getFirstCharIndex().put(fc, set);
		}
		set.add(keyword);
	}

	public BasicKeyword getKeyword(byte codeByte) {
		return getByteCodeMap().get(getKeywordIndex(codeByte));
	}

	public BasicKeyword getKeyword(byte prefixByte, byte codeByte) {
		return getByteCodeMap().get(getKeywordIndex(prefixByte, codeByte));
	}

	public Set<BasicKeyword> getKeywordsStartingWith(char c) {
		Set<BasicKeyword> set = getFirstCharIndex().get(c);
		if (set == null)
			return Collections.emptySet();
		else
			return set;
	}

	private Integer getKeywordIndex(BasicKeyword keyword) {
		return getKeywordIndex(keyword.getPrefixByte(), keyword.getCodeByte());
	}

	private Integer getKeywordIndex(byte codeByte) {
		return getKeywordIndex((byte) 0, codeByte);
	}

	private Integer getKeywordIndex(byte prefixByte, byte codeByte) {
		return Integer.valueOf((prefixByte << 8) & 0xff00 | (codeByte & 0xff));
	}

	private Map<Integer, BasicKeyword> getByteCodeMap() {
		return byteCodeMap;
	}

	private Map<Character, Set<BasicKeyword>> getFirstCharIndex() {
		return firstCharIndex;
	}

	public static class BasicKeyword {

		private byte prefixByte;

		private byte codeByte;

		private String sourceForm;

		public BasicKeyword(byte codeByte, String sourceForm) {
			this((byte) 0, codeByte, sourceForm);
		}

		public BasicKeyword(byte prefixByte, byte codeByte, String sourceForm) {
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
			BasicKeyword other = (BasicKeyword) obj;
			return getSourceForm().equals(other.getSourceForm());
		}

		public Character getFirstCharacter() {
			return getSourceForm().charAt(0);
		}

		public boolean isBasicKeyword() {
			return getPrefixByte() == 0;
		}

		public boolean isExtendedKeyword() {
			return !isBasicKeyword();
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