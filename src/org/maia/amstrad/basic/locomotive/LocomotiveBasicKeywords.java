package org.maia.amstrad.basic.locomotive;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LocomotiveBasicKeywords {

	private Map<Integer, LocomotiveBasicKeyword> byteCodeMap;

	private Map<String, LocomotiveBasicKeyword> sourceFormMap;

	private Map<String, Set<LocomotiveBasicKeyword>> multiSymbolMap; // indexed on first symbol

	private static LocomotiveBasicKeywords instance;

	private static final byte EXTENDED_PREFIX_BYTE = (byte) 0xff; // prefix byte for the extended keywords

	public static LocomotiveBasicKeywords getInstance() {
		if (instance == null) {
			setInstance(new LocomotiveBasicKeywords());
		}
		return instance;
	}

	private static synchronized void setInstance(LocomotiveBasicKeywords keywords) {
		if (instance == null) {
			instance = keywords;
		}
	}

	private LocomotiveBasicKeywords() {
		this.byteCodeMap = new HashMap<Integer, LocomotiveBasicKeyword>(512);
		this.sourceFormMap = new HashMap<String, LocomotiveBasicKeyword>(512);
		this.multiSymbolMap = new HashMap<String, Set<LocomotiveBasicKeyword>>(16);
		loadBasicKeywords();
		loadExtendedKeywords();
	}

	private void loadBasicKeywords() {
		register(new LocomotiveBasicKeyword((byte) 0x80, "AFTER"));
		register(new LocomotiveBasicKeyword((byte) 0x81, "AUTO"));
		register(new LocomotiveBasicKeyword((byte) 0x82, "BORDER"));
		register(new LocomotiveBasicKeyword((byte) 0x83, "CALL"));
		register(new LocomotiveBasicKeyword((byte) 0x84, "CAT"));
		register(new LocomotiveBasicKeyword((byte) 0x85, "CHAIN"));
		register(new LocomotiveBasicKeyword((byte) 0x86, "CLEAR"));
		register(new LocomotiveBasicKeyword((byte) 0x87, "CLG"));
		register(new LocomotiveBasicKeyword((byte) 0x88, "CLOSEIN"));
		register(new LocomotiveBasicKeyword((byte) 0x89, "CLOSEOUT"));
		register(new LocomotiveBasicKeyword((byte) 0x8a, "CLS"));
		register(new LocomotiveBasicKeyword((byte) 0x8b, "CONT"));
		register(new LocomotiveBasicKeyword((byte) 0x8c, "DATA"));
		register(new LocomotiveBasicKeyword((byte) 0x8d, "DEF"));
		register(new LocomotiveBasicKeyword((byte) 0x8e, "DEFINT"));
		register(new LocomotiveBasicKeyword((byte) 0x8f, "DEFREAL"));
		register(new LocomotiveBasicKeyword((byte) 0x90, "DEFSTR"));
		register(new LocomotiveBasicKeyword((byte) 0x91, "DEG"));
		register(new LocomotiveBasicKeyword((byte) 0x92, "DELETE"));
		register(new LocomotiveBasicKeyword((byte) 0x93, "DIM"));
		register(new LocomotiveBasicKeyword((byte) 0x94, "DRAW"));
		register(new LocomotiveBasicKeyword((byte) 0x95, "DRAWR"));
		register(new LocomotiveBasicKeyword((byte) 0x96, "EDIT"));
		register(new LocomotiveBasicKeyword((byte) 0x97, "ELSE"));
		register(new LocomotiveBasicKeyword((byte) 0x98, "END"));
		register(new LocomotiveBasicKeyword((byte) 0x99, "ENT"));
		register(new LocomotiveBasicKeyword((byte) 0x9a, "ENV"));
		register(new LocomotiveBasicKeyword((byte) 0x9b, "ERASE"));
		register(new LocomotiveBasicKeyword((byte) 0x9c, "ERROR"));
		register(new LocomotiveBasicKeyword((byte) 0x9d, "EVERY"));
		register(new LocomotiveBasicKeyword((byte) 0x9e, "FOR"));
		register(new LocomotiveBasicKeyword((byte) 0x9f, "GOSUB"));
		register(new LocomotiveBasicKeyword((byte) 0xa0, "GOTO"));
		register(new LocomotiveBasicKeyword((byte) 0xa1, "IF"));
		register(new LocomotiveBasicKeyword((byte) 0xa2, "INK"));
		register(new LocomotiveBasicKeyword((byte) 0xa3, "INPUT"));
		register(new LocomotiveBasicKeyword((byte) 0xa4, "KEY"));
		register(new LocomotiveBasicKeyword((byte) 0xa5, "LET"));
		register(new LocomotiveBasicKeyword((byte) 0xa6, "LINE"));
		register(new LocomotiveBasicKeyword((byte) 0xa7, "LIST"));
		register(new LocomotiveBasicKeyword((byte) 0xa8, "LOAD"));
		register(new LocomotiveBasicKeyword((byte) 0xa9, "LOCATE"));
		register(new LocomotiveBasicKeyword((byte) 0xaa, "MEMORY"));
		register(new LocomotiveBasicKeyword((byte) 0xab, "MERGE"));
		register(new LocomotiveBasicKeyword((byte) 0xac, "MID$"));
		register(new LocomotiveBasicKeyword((byte) 0xad, "MODE"));
		register(new LocomotiveBasicKeyword((byte) 0xae, "MOVE"));
		register(new LocomotiveBasicKeyword((byte) 0xaf, "MOVER"));
		register(new LocomotiveBasicKeyword((byte) 0xb0, "NEXT"));
		register(new LocomotiveBasicKeyword((byte) 0xb1, "NEW"));
		register(new LocomotiveBasicKeyword((byte) 0xb2, "ON"));
		register(new LocomotiveBasicKeyword((byte) 0xb3, "ON BREAK"));
		register(new LocomotiveBasicKeyword((byte) 0xb4, "ON ERROR GOTO"));
		register(new LocomotiveBasicKeyword((byte) 0xb5, "SQ"));
		register(new LocomotiveBasicKeyword((byte) 0xb6, "OPENIN"));
		register(new LocomotiveBasicKeyword((byte) 0xb7, "OPENOUT"));
		register(new LocomotiveBasicKeyword((byte) 0xb8, "ORIGIN"));
		register(new LocomotiveBasicKeyword((byte) 0xb9, "OUT"));
		register(new LocomotiveBasicKeyword((byte) 0xba, "PAPER"));
		register(new LocomotiveBasicKeyword((byte) 0xbb, "PEN"));
		register(new LocomotiveBasicKeyword((byte) 0xbc, "PLOT"));
		register(new LocomotiveBasicKeyword((byte) 0xbd, "PLOTR"));
		register(new LocomotiveBasicKeyword((byte) 0xbe, "POKE"));
		register(new LocomotiveBasicKeyword((byte) 0xbf, "PRINT"));
		register(new LocomotiveBasicKeyword((byte) 0xc0, "'"));
		register(new LocomotiveBasicKeyword((byte) 0xc1, "RAD"));
		register(new LocomotiveBasicKeyword((byte) 0xc2, "RANDOMIZE"));
		register(new LocomotiveBasicKeyword((byte) 0xc3, "READ"));
		register(new LocomotiveBasicKeyword((byte) 0xc4, "RELEASE"));
		register(new LocomotiveBasicKeyword((byte) 0xc5, "REM"));
		register(new LocomotiveBasicKeyword((byte) 0xc6, "RENUM"));
		register(new LocomotiveBasicKeyword((byte) 0xc7, "RESTORE"));
		register(new LocomotiveBasicKeyword((byte) 0xc8, "RESUME"));
		register(new LocomotiveBasicKeyword((byte) 0xc9, "RETURN"));
		register(new LocomotiveBasicKeyword((byte) 0xca, "RUN"));
		register(new LocomotiveBasicKeyword((byte) 0xcb, "SAVE"));
		register(new LocomotiveBasicKeyword((byte) 0xcc, "SOUND"));
		register(new LocomotiveBasicKeyword((byte) 0xcd, "SPEED"));
		register(new LocomotiveBasicKeyword((byte) 0xce, "STOP"));
		register(new LocomotiveBasicKeyword((byte) 0xcf, "SYMBOL"));
		register(new LocomotiveBasicKeyword((byte) 0xd0, "TAG"));
		register(new LocomotiveBasicKeyword((byte) 0xd1, "TAGOFF"));
		register(new LocomotiveBasicKeyword((byte) 0xd2, "TROFF"));
		register(new LocomotiveBasicKeyword((byte) 0xd3, "TRON"));
		register(new LocomotiveBasicKeyword((byte) 0xd4, "WAIT"));
		register(new LocomotiveBasicKeyword((byte) 0xd5, "WEND"));
		register(new LocomotiveBasicKeyword((byte) 0xd6, "WHILE"));
		register(new LocomotiveBasicKeyword((byte) 0xd7, "WIDTH"));
		register(new LocomotiveBasicKeyword((byte) 0xd8, "WINDOW"));
		register(new LocomotiveBasicKeyword((byte) 0xd9, "WRITE"));
		register(new LocomotiveBasicKeyword((byte) 0xda, "ZONE"));
		register(new LocomotiveBasicKeyword((byte) 0xdb, "DI"));
		register(new LocomotiveBasicKeyword((byte) 0xdc, "EI"));
		register(new LocomotiveBasicKeyword((byte) 0xdd, "FILL"));
		register(new LocomotiveBasicKeyword((byte) 0xde, "GRAPHICS"));
		register(new LocomotiveBasicKeyword((byte) 0xdf, "MASK"));
		register(new LocomotiveBasicKeyword((byte) 0xe0, "FRAME"));
		register(new LocomotiveBasicKeyword((byte) 0xe1, "CURSOR"));
		register(new LocomotiveBasicKeyword((byte) 0xe3, "ERL"));
		register(new LocomotiveBasicKeyword((byte) 0xe4, "FN"));
		register(new LocomotiveBasicKeyword((byte) 0xe5, "SPC"));
		register(new LocomotiveBasicKeyword((byte) 0xe6, "STEP"));
		register(new LocomotiveBasicKeyword((byte) 0xe7, "SWAP"));
		register(new LocomotiveBasicKeyword((byte) 0xea, "TAB"));
		register(new LocomotiveBasicKeyword((byte) 0xeb, "THEN"));
		register(new LocomotiveBasicKeyword((byte) 0xec, "TO"));
		register(new LocomotiveBasicKeyword((byte) 0xed, "USING"));
	}

	private void loadExtendedKeywords() {
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x00, "ABS"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x01, "ASC"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x02, "ATN"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x03, "CHR$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x04, "CINT"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x05, "COS"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x06, "CREAL"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x07, "EXP"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x08, "FIX"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x09, "FRE"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0a, "INKEY"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0b, "INP"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0c, "INT"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0d, "JOY"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0e, "LEN"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x0f, "LOG"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x10, "LOG10"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x11, "LOWER$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x12, "PEEK"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x13, "REMAIN"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x14, "SGN"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x15, "SIN"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x16, "SPACE$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x17, "SQ"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x18, "SQR"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x19, "STR$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1a, "TAN"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1b, "UNT"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1c, "UPPER$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x1d, "VAL"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x40, "EOF"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x41, "ERR"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x42, "HIMEM"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x43, "INKEY$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x44, "PI"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x45, "RND"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x46, "TIME"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x47, "XPOS"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x48, "YPOS"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x49, "DERR"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x71, "BIN$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x72, "DEC$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x73, "HEX$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x74, "INSTR"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x75, "LEFT$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x76, "MAX"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x77, "MIN"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x78, "POS"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x79, "RIGHT$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7a, "ROUND"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7b, "STRING$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7c, "TEST"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7d, "TESTR"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7e, "COPYCHR$"));
		register(new LocomotiveBasicKeyword(EXTENDED_PREFIX_BYTE, (byte) 0x7f, "VPOS"));
	}

	private void register(LocomotiveBasicKeyword keyword) {
		getByteCodeMap().put(getKeywordIndex(keyword), keyword);
		if (!keyword.isExclusiveForDecompile()) {
			getSourceFormMap().put(keyword.getSourceForm(), keyword);
			if (keyword.isMultiSymbol()) {
				String firstSymbol = keyword.getFirstSymbol();
				Set<LocomotiveBasicKeyword> set = getMultiSymbolMap().get(firstSymbol);
				if (set == null) {
					set = new HashSet<LocomotiveBasicKeyword>();
					getMultiSymbolMap().put(firstSymbol, set);
				}
				set.add(keyword);
			}
		}
	}

	public boolean hasKeyword(String sourceForm) {
		return getSourceFormMap().containsKey(sourceForm);
	}

	public LocomotiveBasicKeyword getKeyword(String sourceForm) {
		return getSourceFormMap().get(sourceForm);
	}

	public LocomotiveBasicKeyword getKeyword(byte codeByte) {
		return getByteCodeMap().get(getKeywordIndex(codeByte));
	}

	public LocomotiveBasicKeyword getKeyword(byte prefixByte, byte codeByte) {
		return getByteCodeMap().get(getKeywordIndex(prefixByte, codeByte));
	}

	public void collectKeywordsStartingWithSymbol(String symbol, Collection<LocomotiveBasicKeyword> result) {
		result.clear();
		if (hasKeyword(symbol)) {
			result.add(getKeyword(symbol));
		}
		if (getMultiSymbolMap().containsKey(symbol)) {
			result.addAll(getMultiSymbolMap().get(symbol));
		}
	}

	private Integer getKeywordIndex(LocomotiveBasicKeyword keyword) {
		return getKeywordIndex(keyword.getPrefixByte(), keyword.getCodeByte());
	}

	private Integer getKeywordIndex(byte codeByte) {
		return getKeywordIndex((byte) 0, codeByte);
	}

	private Integer getKeywordIndex(byte prefixByte, byte codeByte) {
		return Integer.valueOf((prefixByte << 8) & 0xff00 | (codeByte & 0xff));
	}

	private Map<Integer, LocomotiveBasicKeyword> getByteCodeMap() {
		return byteCodeMap;
	}

	private Map<String, LocomotiveBasicKeyword> getSourceFormMap() {
		return sourceFormMap;
	}

	private Map<String, Set<LocomotiveBasicKeyword>> getMultiSymbolMap() {
		return multiSymbolMap;
	}

}