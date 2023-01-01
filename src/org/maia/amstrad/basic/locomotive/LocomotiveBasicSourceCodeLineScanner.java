package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicSourceCodeLineScanner;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.FloatingPointNumberToken;
import org.maia.amstrad.basic.locomotive.token.FloatingPointTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.token.Integer16BitBinaryToken;
import org.maia.amstrad.basic.locomotive.token.Integer16BitDecimalToken;
import org.maia.amstrad.basic.locomotive.token.Integer16BitHexadecimalToken;
import org.maia.amstrad.basic.locomotive.token.Integer8BitDecimalToken;
import org.maia.amstrad.basic.locomotive.token.IntegerTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.basic.locomotive.token.LiteralDataToken;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.LiteralRemarkToken;
import org.maia.amstrad.basic.locomotive.token.LiteralToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;
import org.maia.amstrad.basic.locomotive.token.OperatorToken;
import org.maia.amstrad.basic.locomotive.token.SingleDigitDecimalToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.UntypedVariableToken;

public class LocomotiveBasicSourceCodeLineScanner extends BasicSourceCodeLineScanner {

	private LocomotiveBasicKeywords basicKeywords;

	private boolean lineNumberCanFollow;

	private boolean insideRemark;

	private boolean insideData;

	public LocomotiveBasicSourceCodeLineScanner(String text, LocomotiveBasicKeywords basicKeywords) {
		super(text);
		this.basicKeywords = basicKeywords;
	}

	@Override
	public LocomotiveBasicSourceToken nextToken() throws BasicSyntaxException {
		LocomotiveBasicSourceToken token = null;
		checkEndOfText();
		char c = getCurrentChar();
		if (c >= 0x20 && c <= 0x7e) {
			if (insideRemark) {
				token = scanLiteralRemarkToken();
			} else if (insideData) {
				token = scanLiteralDataToken();
				insideData = false;
			} else {
				if (c == InstructionSeparatorToken.SEPARATOR) {
					token = new InstructionSeparatorToken();
					advancePosition();
					lineNumberCanFollow = false;
					insideData = false;
				} else if (c == BasicKeywordToken.REMARK_SHORTHAND) {
					String symbol = String.valueOf(c);
					LocomotiveBasicKeyword keyword = getBasicKeywords().getKeyword(symbol);
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
						LocomotiveBasicKeyword keyword = getBasicKeywords().getKeyword(usymbol);
						token = new BasicKeywordToken(symbol, keyword);
						lineNumberCanFollow = keyword.canBeFollowedByLineNumber();
						insideRemark = keyword.isRemark();
						insideData = keyword.isData();
					} else {
						// Variable
						char type = symbol.charAt(symbol.length() - 1);
						if (type == IntegerTypedVariableToken.TYPE_INDICATOR) {
							token = new IntegerTypedVariableToken(symbol);
						} else if (type == StringTypedVariableToken.TYPE_INDICATOR) {
							token = new StringTypedVariableToken(symbol);
						} else if (type == FloatingPointTypedVariableToken.TYPE_INDICATOR) {
							token = new FloatingPointTypedVariableToken(symbol);
						} else {
							token = new UntypedVariableToken(symbol);
						}
						lineNumberCanFollow = false;
					}
				} else if (c == LiteralQuotedToken.QUOTE) {
					token = scanLiteralQuotedToken();
					lineNumberCanFollow = false;
				} else {
					int p0 = getPosition();
					advancePosition();
					while (!atEndOfText() && isWhitespace(getCurrentChar()))
						advancePosition();
					token = new LiteralToken(subText(p0, getPosition()));
				}
			}
		}
		return token;
	}

	private NumericToken scanAmpersandNumericToken() throws BasicSyntaxException {
		int p0 = getPosition();
		advancePosition();
		checkEndOfText();
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

	private NumericToken scanNumericToken() throws BasicSyntaxException {
		int p0 = getPosition();
		advancePosition();
		boolean point = false;
		boolean exponent = false;
		boolean stop = false;
		while (!atEndOfText() && !stop) {
			char c = getCurrentChar();
			if (c == '.' && !isAtLeadingLineNumber()) {
				if (point || exponent) {
					stop = true;
				} else {
					point = true;
					advancePosition();
				}
			} else if ((c == 'e' || c == 'E') && !isAtLeadingLineNumber()) {
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
			if (lineNumberCanFollow) {
				return new LineNumberReferenceToken(sourceFragment);
			} else {
				int value = Integer.parseInt(sourceFragment);
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

	private OperatorToken scanNumericOperator() throws BasicSyntaxException {
		int p0 = getPosition();
		checkEndOfText();
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

	private LiteralRemarkToken scanLiteralRemarkToken() {
		int p0 = getPosition();
		while (!atEndOfText())
			advancePosition();
		return new LiteralRemarkToken(subText(p0, getPosition()));
	}

	private LiteralDataToken scanLiteralDataToken() throws BasicSyntaxException {
		int p0 = getPosition();
		while (!atEndOfText() && getCurrentChar() != InstructionSeparatorToken.SEPARATOR)
			advancePosition();
		return new LiteralDataToken(subText(p0, getPosition()));
	}

	private LiteralQuotedToken scanLiteralQuotedToken() throws BasicSyntaxException {
		int p0 = getPosition();
		advancePosition();
		while (!atEndOfText() && getCurrentChar() != LiteralQuotedToken.QUOTE)
			advancePosition();
		if (!atEndOfText())
			advancePosition();
		return new LiteralQuotedToken(subText(p0, getPosition()));
	}

	private String scanSymbol() throws BasicSyntaxException {
		int p0 = getPosition();
		advancePosition();
		while (!atEndOfText() && isSymbolCharacter(getCurrentChar()))
			advancePosition();
		return subText(p0, getPosition());
	}

	private boolean isSymbolCharacter(char c) {
		return isDecimalDigit(c) || isLetter(c) || "%$!._".indexOf(c) >= 0;
	}

	private LocomotiveBasicKeywords getBasicKeywords() {
		return basicKeywords;
	}

}