package org.maia.amstrad.basic.locomotive.source;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords.BasicKeyword;

public class SourceTokenFactory {

	private LocomotiveBasicKeywords basicKeywords;

	private static SourceTokenFactory instance;

	public static SourceTokenFactory getInstance() {
		if (instance == null) {
			setInstance(new SourceTokenFactory(LocomotiveBasicKeywords.getInstance()));
		}
		return instance;
	}

	private static synchronized void setInstance(SourceTokenFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

	private SourceTokenFactory(LocomotiveBasicKeywords basicKeywords) {
		this.basicKeywords = basicKeywords;
	}

	public BasicKeywordToken createBasicKeyword(String sourceFragment) throws BasicSyntaxException {
		BasicKeyword keyword = getBasicKeywords().getKeyword(sourceFragment.toUpperCase());
		if (keyword != null) {
			return new BasicKeywordToken(sourceFragment, keyword);
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

	public LiteralQuotedToken createLiteralQuoted(String sourceFragment) {
		return new LiteralQuotedToken(sourceFragment);
	}

	public LiteralRemarkToken createLiteralRemark(String sourceFragment) {
		return new LiteralRemarkToken(sourceFragment);
	}

	public LiteralDataToken createLiteralData(String sourceFragment) {
		return new LiteralDataToken(sourceFragment);
	}

	public FloatingPointNumberToken createFloatingPointNumber(double value) {
		return new FloatingPointNumberToken(FloatingPointNumberToken.format(value));
	}

	public NumericToken createIntegerNumber(int value) throws BasicSyntaxException {
		if (value < 10) {
			return createIntegerSingleDecimalDigit(value);
		} else if (value <= 0xff) {
			return createInteger8BitDecimal(value);
		} else if (value <= 0x7fff) {
			return createInteger16BitDecimal(value);
		} else {
			return createFloatingPointNumber(value);
		}
	}

	public SingleDigitDecimalToken createIntegerSingleDecimalDigit(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 9);
		return new SingleDigitDecimalToken(String.valueOf(value));
	}

	public Integer8BitDecimalToken createInteger8BitDecimal(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0xff);
		return new Integer8BitDecimalToken(String.valueOf(value));
	}

	public Integer16BitDecimalToken createInteger16BitDecimal(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0x7fff);
		return new Integer16BitDecimalToken(String.valueOf(value));
	}

	public Integer16BitBinaryToken createInteger16BitBinary(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0x7fff);
		return new Integer16BitBinaryToken("&X" + Integer.toBinaryString(value));
	}

	public Integer16BitHexadecimalToken createInteger16BitHexadecimal(int value) throws BasicSyntaxException {
		checkValueInRange(value, 0, 0x7fff);
		return new Integer16BitHexadecimalToken("&" + Integer.toHexString(value));
	}

	public LineNumberToken createLineNumber(int value) throws BasicSyntaxException {
		checkValueInRange(value, BasicRuntime.MINIMUM_BASIC_LINE_NUMBER, BasicRuntime.MAXIMUM_BASIC_LINE_NUMBER);
		return new LineNumberToken(String.valueOf(value));
	}

	public OperatorToken createOperator(String sourceFragment) throws BasicSyntaxException {
		if (OperatorToken.isOperator(sourceFragment)) {
			return new OperatorToken(sourceFragment);
		} else {
			throw new BasicSyntaxException("Unrecognized operator", sourceFragment);
		}
	}

	public VariableToken createVariable(String sourceFragment) throws BasicSyntaxException {
		if (sourceFragment.endsWith("%")) {
			return createVariableOfIntegerType(sourceFragment);
		} else if (sourceFragment.endsWith("$")) {
			return createVariableOfStringType(sourceFragment);
		} else if (sourceFragment.endsWith("!")) {
			return createVariableOfFloatingPointType(sourceFragment);
		} else {
			return createVariableUntyped(sourceFragment);
		}
	}

	public IntegerTypedVariableToken createVariableOfIntegerType(String sourceFragment) throws BasicSyntaxException {
		if (sourceFragment.endsWith("%")) {
			return new IntegerTypedVariableToken(sourceFragment);
		} else {
			throw new BasicSyntaxException("Type mismatch", sourceFragment);
		}
	}

	public StringTypedVariableToken createVariableOfStringType(String sourceFragment) throws BasicSyntaxException {
		if (sourceFragment.endsWith("$")) {
			return new StringTypedVariableToken(sourceFragment);
		} else {
			throw new BasicSyntaxException("Type mismatch", sourceFragment);
		}
	}

	public FloatingPointTypedVariableToken createVariableOfFloatingPointType(String sourceFragment)
			throws BasicSyntaxException {
		if (sourceFragment.endsWith("!")) {
			return new FloatingPointTypedVariableToken(sourceFragment);
		} else {
			throw new BasicSyntaxException("Type mismatch", sourceFragment);
		}
	}

	public UntypedVariableToken createVariableUntyped(String sourceFragment) {
		return new UntypedVariableToken(sourceFragment);
	}

	private void checkValueInRange(int value, int minValue, int maxValue) throws BasicSyntaxException {
		if (value < minValue || value > maxValue)
			throw new BasicSyntaxException("Value " + value + " is out of range [" + minValue + "," + maxValue + "]",
					String.valueOf(value));
	}

	private LocomotiveBasicKeywords getBasicKeywords() {
		return basicKeywords;
	}

}