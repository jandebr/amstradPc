package org.maia.amstrad.basic.locomotive.source;

import org.maia.amstrad.basic.BasicCompilationException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords.BasicKeyword;

public class SourceLineScanner {

	private String text;

	private int position;

	private LocomotiveBasicKeywords basicKeywords;

	private boolean lineNumberCanFollow;

	private boolean insideRemark;

	private boolean insideData;

	public SourceLineScanner(String text, LocomotiveBasicKeywords basicKeywords) {
		this.text = text;
		this.basicKeywords = basicKeywords;
	}

	public boolean isEmpty() {
		return getText().trim().isEmpty();
	}

	public int scanLineNumber() throws BasicCompilationException {
		int p0 = getPosition();
		while (!atEndOfText() && isDecimalDigit(getCurrentChar()))
			advancePosition();
		int p1 = getPosition();
		if (!atEndOfText() && isWhitespace(getCurrentChar()))
			advancePosition();
		try {
			return Integer.parseInt(subText(p0, p1));
		} catch (NumberFormatException e) {
			setPosition(p0);
			throw new BasicCompilationException("Could not parse line number", getText(), getPosition());
		}
	}

	public SourceToken scanToken() throws BasicCompilationException {
		SourceToken token = null;
		char c = getCurrentChar();
		if (c >= 0x20 && c <= 0x7e) {
			if (insideRemark || (insideData && c != InstructionSeparatorToken.SEPARATOR)) {
				token = new LiteralToken(String.valueOf(c));
				advancePosition();
			} else {
				if (c == InstructionSeparatorToken.SEPARATOR) {
					token = new InstructionSeparatorToken();
					advancePosition();
					lineNumberCanFollow = false;
					insideData = false;
				} else if (c == BasicKeywordToken.REMARK_SHORTHAND) {
					String symbol = String.valueOf(c);
					BasicKeyword keyword = getBasicKeywords().getKeyword(symbol);
					token = new BasicKeywordToken(symbol, keyword);
					advancePosition();
					insideRemark = true;
				} else if (c == NumericToken.AMPERSAND) {
					token = scanAmpersandNumericToken();
				} else if (isDecimalDigit(c)) {
					token = scanNumericToken();
				} else if ("<=>+-*/^\\".indexOf(c) >= 0) {
					token = scanNumericOperator();
				} else if (isLetter(c)) {
					String symbol = scanSymbol();
					String usymbol = symbol.toUpperCase();
					if (usymbol.equals("AND") || usymbol.equals("MOD") || usymbol.equals("OR") || usymbol.equals("XOR")
							|| usymbol.equals("NOT")) {
						token = new OperatorToken(symbol);
						lineNumberCanFollow = false;
					} else if (getBasicKeywords().hasKeyword(usymbol)) {
						BasicKeyword keyword = getBasicKeywords().getKeyword(usymbol);
						token = new BasicKeywordToken(symbol, keyword);
						lineNumberCanFollow = keyword.canBeFollowedByLineNumber();
						insideRemark = keyword.isRemark();
						insideData = keyword.isData();
					} else {
						// Variable
						if (symbol.endsWith("%")) {
							token = new IntegerTypedVariableToken(symbol);
						} else if (symbol.endsWith("$")) {
							token = new StringTypedVariableToken(symbol);
						} else if (symbol.endsWith("!")) {
							token = new FloatingPointTypedVariableToken(symbol);
						} else {
							token = new UntypedVariableToken(symbol);
						}
						lineNumberCanFollow = false;
					}
				} else if (c == LiteralToken.QUOTE) {
					token = scanQuotedLiteralToken();
					lineNumberCanFollow = false;
				} else {
					token = new LiteralToken(String.valueOf(c));
					advancePosition();
				}
			}
		}
		return token;
	}

	private NumericToken scanAmpersandNumericToken() throws BasicCompilationException {
		int p0 = getPosition();
		advancePosition();
		char c = getCurrentChar();
		if (c == 'X') {
			// binary
			advancePosition();
			while (!atEndOfText() && isBinaryDigit(getCurrentChar()))
				advancePosition();
			return new Integer16BitBinaryToken(subText(p0, getPosition()));
		} else {
			// hexadecimal
			if (c == 'H')
				advancePosition();
			while (!atEndOfText() && isHexadecimalDigit(getCurrentChar()))
				advancePosition();
			return new Integer16BitHexadecimalToken(subText(p0, getPosition()));
		}
	}

	private NumericToken scanNumericToken() throws BasicCompilationException {
		int p0 = getPosition();
		advancePosition();
		boolean point = false;
		boolean exponent = false;
		boolean stop = false;
		while (!atEndOfText() && !stop) {
			char c = getCurrentChar();
			if (c == '.') {
				if (point || exponent) {
					stop = true;
				} else {
					point = true;
					advancePosition();
				}
			} else if (c == 'e' || c == 'E') {
				if (exponent) {
					stop = true;
				} else {
					exponent = true;
					advancePosition();
					if (!atEndOfText() && getCurrentChar() == '-')
						advancePosition();
				}
			} else if (isDecimalDigit(c)) {
				advancePosition();
			} else {
				stop = true;
			}
		}
		String sourceFragment = subText(p0, getPosition());
		if (point || exponent) {
			return new FloatingPointNumberToken(sourceFragment);
		} else {
			int value = Integer.parseInt(sourceFragment);
			if (lineNumberCanFollow) {
				return new LineNumberToken(sourceFragment);
			} else {
				if (value < 10) {
					return new SingleDigitDecimalToken(sourceFragment);
				} else if (value <= 0xff) {
					return new Integer8BitDecimalToken(sourceFragment);
				} else if (value <= 0x7fff) {
					return new Integer16BitDecimalToken(sourceFragment);
				} else {
					return new FloatingPointNumberToken(sourceFragment);
				}
			}
		}
	}

	private OperatorToken scanNumericOperator() throws BasicCompilationException {
		int p0 = getPosition();
		char c0 = getCurrentChar();
		advancePosition();
		if (!atEndOfText()) {
			char c1 = getCurrentChar();
			if (c0 == '<' && (c1 == '>' || c1 == '=')) {
				advancePosition();
			} else if (c0 == '>' && c1 == '=') {
				advancePosition();
			}
		}
		return new OperatorToken(subText(p0, getPosition()));
	}

	private LiteralToken scanQuotedLiteralToken() throws BasicCompilationException {
		int p0 = getPosition();
		advancePosition();
		while (!atEndOfText() && getCurrentChar() != LiteralToken.QUOTE)
			advancePosition();
		if (!atEndOfText())
			advancePosition();
		return new LiteralToken(subText(p0, getPosition()));
	}

	private String scanSymbol() throws BasicCompilationException {
		int p0 = getPosition();
		advancePosition();
		while (!atEndOfText() && isSymbolCharacter(getCurrentChar()))
			advancePosition();
		return subText(p0, getPosition());
	}

	private void advancePosition() {
		advancePosition(1);
	}

	private void advancePosition(int n) {
		setPosition(getPosition() + n);
	}

	private int charsRemaining() {
		return getText().length() - getPosition();
	}

	private char getCurrentChar() throws BasicCompilationException {
		checkEndOfText();
		return getText().charAt(getPosition());
	}

	private boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}

	private boolean isDecimalDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isBinaryDigit(char c) {
		return c >= '0' && c <= '1';
	}

	private boolean isHexadecimalDigit(char c) {
		return isDecimalDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isSymbolCharacter(char c) {
		return isDecimalDigit(c) || isLetter(c) || "%$!._".indexOf(c) >= 0;
	}

	private String subText(int fromPosition, int toPosition) {
		return getText().substring(fromPosition, toPosition);
	}

	private void checkEndOfText() throws BasicCompilationException {
		if (atEndOfText())
			throw new BasicCompilationException("Unfinished line", getText(), getPosition());
	}

	public boolean atEndOfText() {
		return getPosition() >= getText().length();
	}

	public String getText() {
		return text;
	}

	public int getPosition() {
		return position;
	}

	private void setPosition(int position) {
		this.position = position;
	}

	private LocomotiveBasicKeywords getBasicKeywords() {
		return basicKeywords;
	}

}