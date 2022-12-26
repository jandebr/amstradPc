package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicCompiler;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords.BasicKeyword;
import org.maia.amstrad.basic.locomotive.source.AbstractLiteralToken;
import org.maia.amstrad.basic.locomotive.source.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.source.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.source.BasicSourceCodeLine;
import org.maia.amstrad.basic.locomotive.source.BasicSourceCodeLineScanner;
import org.maia.amstrad.basic.locomotive.source.FloatingPointNumberToken;
import org.maia.amstrad.basic.locomotive.source.FloatingPointTypedVariableToken;
import org.maia.amstrad.basic.locomotive.source.InstructionSeparatorToken;
import org.maia.amstrad.basic.locomotive.source.Integer16BitBinaryToken;
import org.maia.amstrad.basic.locomotive.source.Integer16BitDecimalToken;
import org.maia.amstrad.basic.locomotive.source.Integer16BitHexadecimalToken;
import org.maia.amstrad.basic.locomotive.source.Integer8BitDecimalToken;
import org.maia.amstrad.basic.locomotive.source.IntegerTypedVariableToken;
import org.maia.amstrad.basic.locomotive.source.LineNumberToken;
import org.maia.amstrad.basic.locomotive.source.LiteralDataToken;
import org.maia.amstrad.basic.locomotive.source.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.source.LiteralRemarkToken;
import org.maia.amstrad.basic.locomotive.source.LiteralToken;
import org.maia.amstrad.basic.locomotive.source.OperatorToken;
import org.maia.amstrad.basic.locomotive.source.SingleDigitDecimalToken;
import org.maia.amstrad.basic.locomotive.source.SourceToken;
import org.maia.amstrad.basic.locomotive.source.SourceTokenVisitor;
import org.maia.amstrad.basic.locomotive.source.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.source.UntypedVariableToken;

public class LocomotiveBasicCompiler extends LocomotiveBasicProcessor implements BasicCompiler {

	public LocomotiveBasicCompiler() {
	}

	@Override
	public byte[] compile(CharSequence sourceCode) throws BasicSyntaxException {
		int maxBytes = BasicRuntime.MEMORY_POINTER_END_OF_PROGRAM - BasicRuntime.MEMORY_ADDRESS_START_OF_PROGRAM;
		ByteBuffer byteBuffer = new ByteBuffer(maxBytes);
		ByteCodeGenerator byteCodeGenerator = new ByteCodeGenerator(byteBuffer);
		BasicSourceCode code = new BasicSourceCode(sourceCode);
		for (BasicSourceCodeLine line : code) {
			BasicSourceCodeLineScanner scanner = line.createScanner();
			int i0 = byteBuffer.getSize();
			byteBuffer.appendWord(0); // placeholder for line length
			compileLine(scanner, byteCodeGenerator);
			byteBuffer.replaceWordAt(i0, byteBuffer.getSize() - i0); // substitute actual line length
		}
		byteBuffer.appendWord(0); // end of program
		return byteBuffer.getData();
	}

	private void compileLine(BasicSourceCodeLineScanner scanner, ByteCodeGenerator byteCodeGenerator)
			throws BasicSyntaxException {
		int lineNumber = scanner.firstToken().getValue();
		byteCodeGenerator.getByteBuffer().appendWord(lineNumber);
		while (!scanner.atEndOfText()) {
			SourceToken token = scanner.nextToken();
			if (token == null) {
				throw new BasicSyntaxException("Syntax error", scanner.getText(), scanner.getPosition());
			} else {
				token.invite(byteCodeGenerator); // appends the token's byte code to the buffer
			}
		}
		byteCodeGenerator.getByteBuffer().appendByte((byte) 0); // end of line
	}

	private static class ByteBuffer {

		private byte[] buffer;

		private int size;

		public ByteBuffer(int fixedCapacity) {
			this.buffer = new byte[fixedCapacity];
		}

		public void appendByte(byte value) {
			int i = getSize();
			if (i == getCapacity())
				throw new IndexOutOfBoundsException("Buffer capacity is reached");
			getBuffer()[i] = value;
			setSize(i + 1);
		}

		public void appendWord(int value) {
			// little-endian
			appendByte((byte) (value % 256));
			appendByte((byte) ((value / 256) & 0xff));
		}

		public void replaceByteAt(int index, byte value) {
			if (index < 0 || index >= getSize())
				throw new IndexOutOfBoundsException("Cannot replace outside the buffer's data range");
			getBuffer()[index] = value;
		}

		public void replaceWordAt(int index, int value) {
			// little-endian
			replaceByteAt(index, (byte) (value % 256));
			replaceByteAt(index + 1, (byte) ((value / 256) & 0xff));
		}

		public byte[] getData() {
			int n = getSize();
			byte[] bytes = new byte[n];
			System.arraycopy(getBuffer(), 0, bytes, 0, n);
			return bytes;
		}

		public int getCapacity() {
			return getBuffer().length;
		}

		private byte[] getBuffer() {
			return buffer;
		}

		public int getSize() {
			return size;
		}

		private void setSize(int size) {
			this.size = size;
		}

	}

	private static class ByteCodeGenerator implements SourceTokenVisitor {

		private ByteBuffer byteBuffer;

		public ByteCodeGenerator(ByteBuffer byteBuffer) {
			this.byteBuffer = byteBuffer;
		}

		@Override
		public void visitInstructionSeparator(InstructionSeparatorToken token) {
			getByteBuffer().appendByte((byte) 0x01);
		}

		@Override
		public void visitSingleDigitDecimal(SingleDigitDecimalToken token) {
			int n = token.getValue();
			getByteBuffer().appendByte((byte) (0x0e + n));
		}

		@Override
		public void visitInteger8BitDecimal(Integer8BitDecimalToken token) {
			int n = token.getValue();
			getByteBuffer().appendByte((byte) 0x19);
			getByteBuffer().appendByte((byte) n);
		}

		@Override
		public void visitInteger16BitDecimal(Integer16BitDecimalToken token) {
			int n = token.getValue();
			getByteBuffer().appendByte((byte) 0x1a);
			getByteBuffer().appendWord(n);
		}

		@Override
		public void visitInteger16BitBinary(Integer16BitBinaryToken token) {
			int n = token.getValue();
			getByteBuffer().appendByte((byte) 0x1b);
			getByteBuffer().appendWord(n);
		}

		@Override
		public void visitInteger16BitHexadecimal(Integer16BitHexadecimalToken token) {
			int n = token.getValue();
			getByteBuffer().appendByte((byte) 0x1c);
			getByteBuffer().appendWord(n);
		}

		@Override
		public void visitLineNumber(LineNumberToken token) {
			int n = token.getValue();
			getByteBuffer().appendByte((byte) 0x1e);
			getByteBuffer().appendWord(n);
		}

		@Override
		public void visitFloatingPointNumber(FloatingPointNumberToken token) {
			double value = token.getValue(); // assuming this is a positive number
			double fractionalPart = value % 1;
			long integralPart = (long) (value - fractionalPart);
			int[] mantissaBits = new int[32];
			int exponent = 128;
			int mi = 0;
			boolean zeros = true;
			// Integral part
			if (integralPart > 0L) {
				String binaryStr = Long.toString(integralPart, 2);
				for (int i = 0; i < binaryStr.length(); i++)
					mantissaBits[mi++] = binaryStr.charAt(i) - '0';
				exponent += binaryStr.length();
				zeros = false;
			}
			// Fractional part
			while (fractionalPart != 0 && mi < 32) {
				fractionalPart *= 2.0;
				if (fractionalPart >= 1.0) {
					mantissaBits[mi++] = 1;
					fractionalPart = fractionalPart % 1;
					zeros = false;
				} else {
					if (zeros) {
						exponent--;
					} else {
						mantissaBits[mi++] = 0;
					}
				}
			}
			if (fractionalPart >= 0.5) {
				// Round up one binary digit
				mi = 31;
				while (mi > 0 && mantissaBits[mi] == 1)
					mantissaBits[mi--] = 0;
				mantissaBits[mi] = 1;
				if (mi == 0)
					exponent++;
			}
			// Assemble in bytes
			int m4 = bitsToInteger(mantissaBits, 1, 8); // make first bit=0 to represent +sign
			int m3 = bitsToInteger(mantissaBits, 8, 16);
			int m2 = bitsToInteger(mantissaBits, 16, 24);
			int m1 = bitsToInteger(mantissaBits, 24, 32);
			// Special case 0.0
			if (zeros) {
				m4 = 0x28; // convention?
				exponent = 0;
			}
			// Output
			ByteBuffer byteBuffer = getByteBuffer();
			byteBuffer.appendByte((byte) 0x1f);
			byteBuffer.appendByte((byte) m1);
			byteBuffer.appendByte((byte) m2);
			byteBuffer.appendByte((byte) m3);
			byteBuffer.appendByte((byte) m4);
			byteBuffer.appendByte((byte) exponent);
		}

		@Override
		public void visitIntegerTypedVariable(IntegerTypedVariableToken token) {
			appendByteCodeForTypedVariable((byte) 0x02, token.getVariableNameWithoutTypeIndicator());
		}

		@Override
		public void visitStringTypedVariable(StringTypedVariableToken token) {
			appendByteCodeForTypedVariable((byte) 0x03, token.getVariableNameWithoutTypeIndicator());
		}

		@Override
		public void visitFloatingPointTypedVariable(FloatingPointTypedVariableToken token) {
			appendByteCodeForTypedVariable((byte) 0x04, token.getVariableNameWithoutTypeIndicator());
		}

		@Override
		public void visitUntypedVariable(UntypedVariableToken token) {
			appendByteCodeForTypedVariable((byte) 0x0d, token.getVariableNameWithoutTypeIndicator());
		}

		@Override
		public void visitBasicKeyword(BasicKeywordToken token) {
			BasicKeyword keyword = token.getKeyword();
			if (keyword.isPrecededByInstructionSeparator()) {
				getByteBuffer().appendByte((byte) 0x01); // instruction separator
			}
			if (keyword.isExtendedKeyword()) {
				getByteBuffer().appendByte(keyword.getPrefixByte());
			}
			getByteBuffer().appendByte(keyword.getCodeByte());
		}

		@Override
		public void visitOperator(OperatorToken token) {
			ByteBuffer byteBuffer = getByteBuffer();
			String symbol = token.getSourceFragment().toUpperCase();
			if (symbol.equals(">")) {
				byteBuffer.appendByte((byte) 0xee);
			} else if (symbol.equals("=")) {
				byteBuffer.appendByte((byte) 0xef);
			} else if (symbol.equals(">=")) {
				byteBuffer.appendByte((byte) 0xf0);
			} else if (symbol.equals("<")) {
				byteBuffer.appendByte((byte) 0xf1);
			} else if (symbol.equals("<>")) {
				byteBuffer.appendByte((byte) 0xf2);
			} else if (symbol.equals("<=")) {
				byteBuffer.appendByte((byte) 0xf3);
			} else if (symbol.equals("+")) {
				byteBuffer.appendByte((byte) 0xf4);
			} else if (symbol.equals("-")) {
				byteBuffer.appendByte((byte) 0xf5);
			} else if (symbol.equals("*")) {
				byteBuffer.appendByte((byte) 0xf6);
			} else if (symbol.equals("/")) {
				byteBuffer.appendByte((byte) 0xf7);
			} else if (symbol.equals("^")) {
				byteBuffer.appendByte((byte) 0xf8);
			} else if (symbol.equals("\\")) {
				byteBuffer.appendByte((byte) 0xf9);
			} else if (symbol.equals("AND")) {
				byteBuffer.appendByte((byte) 0xfa);
			} else if (symbol.equals("MOD")) {
				byteBuffer.appendByte((byte) 0xfb);
			} else if (symbol.equals("OR")) {
				byteBuffer.appendByte((byte) 0xfc);
			} else if (symbol.equals("XOR")) {
				byteBuffer.appendByte((byte) 0xfd);
			} else if (symbol.equals("NOT")) {
				byteBuffer.appendByte((byte) 0xfe);
			}
		}

		@Override
		public void visitLiteral(LiteralToken token) {
			appendByteCodeForLiteral(token);
		}

		@Override
		public void visitLiteralQuoted(LiteralQuotedToken token) {
			appendByteCodeForLiteral(token);
		}

		@Override
		public void visitLiteralRemark(LiteralRemarkToken token) {
			appendByteCodeForLiteral(token);
		}

		@Override
		public void visitLiteralData(LiteralDataToken token) {
			appendByteCodeForLiteral(token);
		}

		private void appendByteCodeForLiteral(AbstractLiteralToken token) {
			ByteBuffer byteBuffer = getByteBuffer();
			int n = token.getSourceFragment().length();
			for (int i = 0; i < n; i++) {
				byteBuffer.appendByte((byte) token.getSourceFragment().charAt(i));
			}
		}

		private void appendByteCodeForTypedVariable(byte variableTypeCode, String variableNameWithoutTypeIndicator) {
			ByteBuffer byteBuffer = getByteBuffer();
			byteBuffer.appendByte(variableTypeCode);
			byteBuffer.appendWord(0);
			int n = variableNameWithoutTypeIndicator.length();
			for (int i = 0; i < n - 1; i++) {
				byteBuffer.appendByte((byte) variableNameWithoutTypeIndicator.charAt(i));
			}
			byteBuffer.appendByte((byte) (128 + variableNameWithoutTypeIndicator.charAt(n - 1)));
		}

		private int bitsToInteger(int[] bits, int fromIndex, int toIndex) {
			int value = 0;
			int f = 1;
			for (int i = 0; i < toIndex - fromIndex; i++) {
				value += f * bits[toIndex - 1 - i];
				f *= 2;
			}
			return value;
		}

		public ByteBuffer getByteBuffer() {
			return byteBuffer;
		}

	}

}