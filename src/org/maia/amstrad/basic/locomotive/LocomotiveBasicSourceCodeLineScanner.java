package org.maia.amstrad.basic.locomotive;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicSourceCodeLineScanner;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.FloatingPointNumberToken;
import org.maia.amstrad.basic.locomotive.token.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.token.Integer16BitBinaryToken;
import org.maia.amstrad.basic.locomotive.token.Integer16BitHexadecimalToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.basic.locomotive.token.LiteralDataToken;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.LiteralRemarkToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;
import org.maia.amstrad.basic.locomotive.token.OperatorToken;

public class LocomotiveBasicSourceCodeLineScanner extends BasicSourceCodeLineScanner {

	private List<LocomotiveBasicKeyword> keywordsAtHand;

	private boolean lineNumberCanFollow;

	private boolean insideRemark;

	private boolean insideData;

	public LocomotiveBasicSourceCodeLineScanner(String text) {
		super(text);
		this.keywordsAtHand = new Vector<LocomotiveBasicKeyword>();
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
					token = getSourceTokenFactory().createInstructionSeparator();
					advancePosition();
					lineNumberCanFollow = false;
					insideData = false;
				} else if (c == BasicKeywordToken.REMARK_SHORTHAND) {
					token = getSourceTokenFactory().createBasicKeyword(String.valueOf(c));
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
					if (getSourceTokenFactory().isOperator(symbol)) {
						token = getSourceTokenFactory().createOperator(symbol);
						lineNumberCanFollow = false;
					} else {
						getSourceTokenFactory().collectKeywordsStartingWithSymbol(symbol, keywordsAtHand);
						if (!keywordsAtHand.isEmpty()) {
							// Keyword
							LocomotiveBasicKeyword keyword = scanFullKeyword(symbol, keywordsAtHand);
							token = new BasicKeywordToken(keyword);
							lineNumberCanFollow = keyword.canBeFollowedByLineNumber();
							insideRemark = keyword.isRemark();
							insideData = keyword.isData();
						} else {
							// Variable
							token = getSourceTokenFactory().createVariable(symbol);
							lineNumberCanFollow = false;
						}
					}
				} else if (c == LiteralQuotedToken.QUOTE) {
					token = scanLiteralQuotedToken();
				} else {
					int p0 = getPosition();
					advancePosition();
					while (!atEndOfText() && isWhitespace(c) && isWhitespace(getCurrentChar()))
						advancePosition(); // stretch of whitespace
					token = getSourceTokenFactory().createLiteral(subText(p0, getPosition()));
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
				return LocomotiveBasicSourceTokenFactory.getInstance().createPositiveIntegerNumber(value);
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
		return getSourceTokenFactory().createOperator(subText(p0, getPosition()));
	}

	private LiteralRemarkToken scanLiteralRemarkToken() {
		int p0 = getPosition();
		while (!atEndOfText())
			advancePosition();
		return getSourceTokenFactory().createLiteralRemark(subText(p0, getPosition()));
	}

	private LiteralDataToken scanLiteralDataToken() throws BasicSyntaxException {
		int p0 = getPosition();
		while (!atEndOfText() && getCurrentChar() != InstructionSeparatorToken.SEPARATOR)
			advancePosition();
		return getSourceTokenFactory().createLiteralData(subText(p0, getPosition()));
	}

	private LiteralQuotedToken scanLiteralQuotedToken() throws BasicSyntaxException {
		advancePosition();
		int p0 = getPosition();
		while (!atEndOfText() && getCurrentChar() != LiteralQuotedToken.QUOTE)
			advancePosition();
		int p1 = getPosition();
		if (!atEndOfText())
			advancePosition();
		return getSourceTokenFactory().createLiteralQuoted(subText(p0, p1));
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

	private LocomotiveBasicKeyword scanFullKeyword(String symbolScannedSoFar, List<LocomotiveBasicKeyword> candidates) {
		LocomotiveBasicKeyword winner = candidates.get(0);
		if (candidates.size() > 1) {
			// Find longest matching keyword
			String utext = getText().toUpperCase();
			int p0 = getPosition() - symbolScannedSoFar.length();
			for (int i = 1; i < candidates.size(); i++) {
				LocomotiveBasicKeyword keyword = candidates.get(i);
				if (utext.startsWith(keyword.getSourceForm(), p0)) {
					if (keyword.getSourceForm().length() > winner.getSourceForm().length()) {
						winner = keyword;
					}
				}
			}
		}
		advancePosition(winner.getSourceForm().length() - symbolScannedSoFar.length());
		return winner;
	}

	private LocomotiveBasicSourceTokenFactory getSourceTokenFactory() {
		return LocomotiveBasicSourceTokenFactory.getInstance();
	}

}