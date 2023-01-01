package org.maia.amstrad.basic.locomotive;

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
import org.maia.amstrad.basic.locomotive.token.OperatorToken;
import org.maia.amstrad.basic.locomotive.token.SingleDigitDecimalToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.UntypedVariableToken;

public interface LocomotiveBasicSourceTokenVisitor {

	void visitInstructionSeparator(InstructionSeparatorToken token);

	void visitSingleDigitDecimal(SingleDigitDecimalToken token);

	void visitInteger8BitDecimal(Integer8BitDecimalToken token);

	void visitInteger16BitDecimal(Integer16BitDecimalToken token);

	void visitInteger16BitBinary(Integer16BitBinaryToken token);

	void visitInteger16BitHexadecimal(Integer16BitHexadecimalToken token);

	void visitFloatingPointNumber(FloatingPointNumberToken token);

	void visitLineNumberReference(LineNumberReferenceToken token);

	void visitIntegerTypedVariable(IntegerTypedVariableToken token);

	void visitStringTypedVariable(StringTypedVariableToken token);

	void visitFloatingPointTypedVariable(FloatingPointTypedVariableToken token);

	void visitUntypedVariable(UntypedVariableToken token);

	void visitBasicKeyword(BasicKeywordToken token);

	void visitOperator(OperatorToken token);

	void visitLiteral(LiteralToken token);

	void visitLiteralQuoted(LiteralQuotedToken token);

	void visitLiteralRemark(LiteralRemarkToken token);

	void visitLiteralData(LiteralDataToken token);

}