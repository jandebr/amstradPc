package org.maia.amstrad.basic.locomotive.source;

public interface SourceTokenVisitor {

	void visitInstructionSeparator(InstructionSeparatorToken token);

	void visitSingleDigitDecimal(SingleDigitDecimalToken token);

	void visitInteger8BitDecimal(Integer8BitDecimalToken token);

	void visitInteger16BitDecimal(Integer16BitDecimalToken token);

	void visitInteger16BitBinary(Integer16BitBinaryToken token);

	void visitInteger16BitHexadecimal(Integer16BitHexadecimalToken token);

	void visitLineNumber(LineNumberToken token);

	void visitFloatingPointNumber(FloatingPointNumberToken token);

	void visitIntegerTypedVariable(IntegerTypedVariableToken token);

	void visitStringTypedVariable(StringTypedVariableToken token);

	void visitFloatingPointTypedVariable(FloatingPointTypedVariableToken token);

	void visitUntypedVariable(UntypedVariableToken token);

	void visitBasicKeyword(BasicKeywordToken token);

	void visitOperator(OperatorToken token);

	void visitLiteral(LiteralToken token);

}