package org.maia.amstrad.basic.locomotive;

import java.util.Collection;

import org.maia.amstrad.basic.BasicLineNumberToken;
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
import org.maia.amstrad.basic.locomotive.token.VariableToken;

public class LocomotiveBasicSourceTokenFactory {

	private LocomotiveBasicKeywords basicKeywords;

	private LocomotiveBasicOperators basicOperators;

	private static LocomotiveBasicSourceTokenFactory instance;

	public static LocomotiveBasicSourceTokenFactory getInstance() {
		if (instance == null) {
			setInstance(new LocomotiveBasicSourceTokenFactory(LocomotiveBasicKeywords.getInstance(),
					LocomotiveBasicOperators.getInstance()));
		}
		return instance;
	}

	private static synchronized void setInstance(LocomotiveBasicSourceTokenFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

	private LocomotiveBasicSourceTokenFactory(LocomotiveBasicKeywords basicKeywords,
			LocomotiveBasicOperators basicOperators) {
		this.basicKeywords = basicKeywords;
		this.basicOperators = basicOperators;
	}

	public boolean isKeyword(String sourceFragment) {
		return getBasicKeywords().hasKeyword(sourceFragment);
	}

	void collectKeywordsStartingWithSymbol(String symbol, Collection<LocomotiveBasicKeyword> result) {
		getBasicKeywords().collectKeywordsStartingWithSymbol(symbol, result);
	}

	public BasicKeywordToken createBasicKeyword(String sourceFragment) throws BasicSyntaxException {
		LocomotiveBasicKeyword keyword = getBasicKeywords().getKeyword(sourceFragment);
		if (keyword != null) {
			return new BasicKeywordToken(keyword);
		} else {
			throw new BasicSyntaxException("Unrecognized keyword", sourceFragment);
		}
	}

	public InstructionSeparatorToken createInstructionSeparator() {
		return new InstructionSeparatorToken();
	}

	public LiteralToken createLiteral(String sourceFragment) {
		return new LiteralToken(sourceFragment);
	}

	public LiteralQuotedToken createLiteralQuoted(String literalBetweenQuotes) {
		return new LiteralQuotedToken(LiteralQuotedToken.QUOTE + literalBetweenQuotes + LiteralQuotedToken.QUOTE);
	}

	public LiteralRemarkToken createLiteralRemark(String sourceFragment) {
		return new LiteralRemarkToken(sourceFragment);
	}

	public LiteralDataToken createLiteralData(String sourceFragment) {
		return new LiteralDataToken(sourceFragment);
	}

	public FloatingPointNumberToken createFloatingPointNumber(double value) {
		return new FloatingPointNumberToken(value);
	}

	public NumericToken createPositiveIntegerNumber(int value) throws BasicSyntaxException {
		if (value < 0) {
			throw new BasicSyntaxException("Value " + value + " is negative", String.valueOf(value));
		} else if (value < 10) {
			return createPositiveIntegerSingleDigitDecimal(value);
		} else if (value <= 0xff) {
			return createPositiveInteger8BitDecimal(value);
		} else if (value <= 0x7fff) {
			return createPositiveInteger16BitDecimal(value);
		} else {
			return createFloatingPointNumber(value);
		}
	}

	public SingleDigitDecimalToken createPositiveIntegerSingleDigitDecimal(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 9);
		return new SingleDigitDecimalToken(value);
	}

	public Integer8BitDecimalToken createPositiveInteger8BitDecimal(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0xff);
		return new Integer8BitDecimalToken(value);
	}

	public Integer16BitDecimalToken createPositiveInteger16BitDecimal(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0x7fff);
		return new Integer16BitDecimalToken(value);
	}

	public Integer16BitBinaryToken createPositiveInteger16BitBinary(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0xffff);
		return new Integer16BitBinaryToken("&X" + Integer.toBinaryString(value));
	}

	public Integer16BitHexadecimalToken createPositiveInteger16BitHexadecimal(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0xffff);
		return new Integer16BitHexadecimalToken("&" + Integer.toHexString(value));
	}

	public BasicLineNumberToken createLineNumber(int lineNumber) throws BasicSyntaxException {
		checkValueInRange(lineNumber, LocomotiveBasicRuntime.MINIMUM_LINE_NUMBER,
				LocomotiveBasicRuntime.MAXIMUM_LINE_NUMBER);
		return new BasicLineNumberToken(String.valueOf(lineNumber));
	}

	public LineNumberReferenceToken createLineNumberReference(int lineNumber) throws BasicSyntaxException {
		checkValueInRange(lineNumber, LocomotiveBasicRuntime.MINIMUM_LINE_NUMBER,
				LocomotiveBasicRuntime.MAXIMUM_LINE_NUMBER);
		return new LineNumberReferenceToken(lineNumber);
	}

	public boolean isOperator(String sourceFragment) {
		return getBasicOperators().hasOperator(sourceFragment);
	}

	public OperatorToken createOperator(String sourceFragment) throws BasicSyntaxException {
		LocomotiveBasicOperator operator = getBasicOperators().getOperator(sourceFragment);
		if (operator != null) {
			return new OperatorToken(operator);
		} else {
			throw new BasicSyntaxException("Unrecognized operator", sourceFragment);
		}
	}

	public VariableToken createVariable(String sourceFragment) {
		char type = sourceFragment.charAt(sourceFragment.length() - 1);
		if (type == IntegerTypedVariableToken.TYPE_INDICATOR || type == FloatingPointTypedVariableToken.TYPE_INDICATOR
				|| type == StringTypedVariableToken.TYPE_INDICATOR) {
			String variableNameWithoutTypeIndicator = sourceFragment.substring(0, sourceFragment.length() - 1);
			if (type == IntegerTypedVariableToken.TYPE_INDICATOR) {
				return createVariableOfIntegerType(variableNameWithoutTypeIndicator);
			} else if (type == FloatingPointTypedVariableToken.TYPE_INDICATOR) {
				return createVariableOfFloatingPointType(variableNameWithoutTypeIndicator);
			} else {
				return createVariableOfStringType(variableNameWithoutTypeIndicator);
			}
		} else {
			return createVariableUntyped(sourceFragment);
		}
	}

	public IntegerTypedVariableToken createVariableOfIntegerType(String variableNameWithoutTypeIndicator) {
		return IntegerTypedVariableToken.forName(variableNameWithoutTypeIndicator);
	}

	public FloatingPointTypedVariableToken createVariableOfFloatingPointType(String variableNameWithoutTypeIndicator) {
		return FloatingPointTypedVariableToken.forName(variableNameWithoutTypeIndicator);
	}

	public StringTypedVariableToken createVariableOfStringType(String variableNameWithoutTypeIndicator) {
		return StringTypedVariableToken.forName(variableNameWithoutTypeIndicator);
	}

	public UntypedVariableToken createVariableUntyped(String variableName) {
		return new UntypedVariableToken(variableName);
	}

	private void checkValueInRange(int value, int minValue, int maxValue) throws BasicSyntaxException {
		if (value < minValue || value > maxValue)
			throw new BasicSyntaxException("Value " + value + " is out of range [" + minValue + "," + maxValue + "]",
					String.valueOf(value));
	}

	private LocomotiveBasicKeywords getBasicKeywords() {
		return basicKeywords;
	}

	private LocomotiveBasicOperators getBasicOperators() {
		return basicOperators;
	}

}