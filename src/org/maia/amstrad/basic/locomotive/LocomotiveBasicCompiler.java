package org.maia.amstrad.basic.locomotive;

import java.util.StringTokenizer;

import org.maia.amstrad.basic.BasicCompilationException;
import org.maia.amstrad.basic.BasicCompiler;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords.BasicKeyword;

public class LocomotiveBasicCompiler extends LocomotiveBasicProcessor implements BasicCompiler {

	public LocomotiveBasicCompiler() {
	}

	@Override
	public byte[] compile(CharSequence sourceCode) throws BasicCompilationException {
		int maxBytes = BasicRuntime.MEMORY_POINTER_END_OF_PROGRAM - BasicRuntime.MEMORY_ADDRESS_START_OF_PROGRAM;
		ByteBuffer byteBuffer = new ByteBuffer(maxBytes);
		StringTokenizer st = new StringTokenizer(sourceCode.toString(), "\n\r");
		int lineIndex = 0;
		while (st.hasMoreTokens()) {
			SourceLineScanner scanner = new SourceLineScanner(st.nextToken(), lineIndex++);
			if (!scanner.isEmpty()) {
				int i0 = byteBuffer.getSize();
				byteBuffer.appendWord(0); // placeholder for line length
				compileLine(scanner, byteBuffer);
				byteBuffer.replaceWordAt(i0, byteBuffer.getSize() - i0); // substitute actual line length
			}
		}
		byteBuffer.appendWord(0); // end of program
		return byteBuffer.getData();
	}

	private void compileLine(SourceLineScanner scanner, ByteBuffer byteBuffer) throws BasicCompilationException {
		int lineNumber = scanner.scanLineNumber();
		byteBuffer.appendWord(lineNumber);
		compileLineBody(scanner, byteBuffer);
		byteBuffer.appendByte((byte) 0); // end of line
	}

	private void compileLineBody(SourceLineScanner scanner, ByteBuffer byteBuffer) throws BasicCompilationException {
		while (!scanner.atEndOfText()) {
			SourceToken token = scanner.scanToken();
			if (token == null) {
				throw new LocomotiveBasicCompilationException("Syntax error", scanner);
			} else {
				token.appendByteCodeTo(byteBuffer);
			}
		}
	}

	private static class LocomotiveBasicCompilationException extends BasicCompilationException {

		public LocomotiveBasicCompilationException(String message, SourceLineScanner scanner) {
			super(message, scanner.getText(), scanner.getLineIndex(), scanner.getPosition());
		}

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

	private class SourceLineScanner {

		private String text;

		private int lineIndex;

		private int position;

		private boolean lineNumberCanFollow;

		private boolean insideRemark;

		private boolean insideData;

		public SourceLineScanner(String text, int lineIndex) {
			this.text = text;
			this.lineIndex = lineIndex;
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
				throw new LocomotiveBasicCompilationException("Could not parse line number", this);
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
						if (usymbol.equals("AND") || usymbol.equals("MOD") || usymbol.equals("OR")
								|| usymbol.equals("XOR") || usymbol.equals("NOT")) {
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
				throw new LocomotiveBasicCompilationException("Unfinished line", this);
		}

		public boolean atEndOfText() {
			return getPosition() >= getText().length();
		}

		public String getText() {
			return text;
		}

		public int getLineIndex() {
			return lineIndex;
		}

		public int getPosition() {
			return position;
		}

		private void setPosition(int position) {
			this.position = position;
		}

	}

	private static abstract class SourceToken {

		private String sourceFragment;

		protected SourceToken(String sourceFragment) {
			this.sourceFragment = sourceFragment;
		}

		public abstract void appendByteCodeTo(ByteBuffer byteBuffer);

		protected String getSourceFragment() {
			return sourceFragment;
		}

	}

	private static class InstructionSeparatorToken extends SourceToken {

		public static final char SEPARATOR = ':';

		public InstructionSeparatorToken() {
			super(String.valueOf(SEPARATOR));
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			byteBuffer.appendByte((byte) 0x01);
		}

	}

	private static abstract class NumericToken extends SourceToken {

		public static final char AMPERSAND = '&';

		protected NumericToken(String sourceFragment) {
			super(sourceFragment);
		}

		protected int parseAsInt() {
			return Integer.parseInt(getSourceFragment());
		}

		protected double parseAsDouble() {
			return Double.parseDouble(getSourceFragment());
		}

	}

	private static class SingleDigitDecimalToken extends NumericToken {

		public SingleDigitDecimalToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			int n = parseAsInt();
			byteBuffer.appendByte((byte) (0x0e + n));
		}

	}

	private static class Integer8BitDecimalToken extends NumericToken {

		public Integer8BitDecimalToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			int n = parseAsInt();
			byteBuffer.appendByte((byte) 0x19);
			byteBuffer.appendByte((byte) n);
		}

	}

	private static class Integer16BitDecimalToken extends NumericToken {

		public Integer16BitDecimalToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			int n = parseAsInt();
			byteBuffer.appendByte((byte) 0x1a);
			byteBuffer.appendWord(n);
		}

	}

	private static class Integer16BitBinaryToken extends NumericToken {

		public Integer16BitBinaryToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			int n = parseAsInt();
			byteBuffer.appendByte((byte) 0x1b);
			byteBuffer.appendWord(n);
		}

		@Override
		protected int parseAsInt() {
			return Integer.parseInt(getSourceFragment().substring(2), 2); // ex. &X11010
		}

	}

	private static class Integer16BitHexadecimalToken extends NumericToken {

		public Integer16BitHexadecimalToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			int n = parseAsInt();
			byteBuffer.appendByte((byte) 0x1c);
			byteBuffer.appendWord(n);
		}

		@Override
		protected int parseAsInt() {
			if (getSourceFragment().toUpperCase().startsWith("&H")) {
				return Integer.parseInt(getSourceFragment().substring(2), 16); // ex. &H7A1D
			} else {
				return Integer.parseInt(getSourceFragment().substring(1), 16); // ex. &7A1D
			}
		}

	}

	private static class LineNumberToken extends Integer16BitDecimalToken {

		public LineNumberToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			int n = parseAsInt();
			byteBuffer.appendByte((byte) 0x1e);
			byteBuffer.appendWord(n);
		}

	}

	private static class FloatingPointNumberToken extends NumericToken {

		public FloatingPointNumberToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			double value = parseAsDouble(); // assuming this is a positive number
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
			byteBuffer.appendByte((byte) 0x1f);
			byteBuffer.appendByte((byte) m1);
			byteBuffer.appendByte((byte) m2);
			byteBuffer.appendByte((byte) m3);
			byteBuffer.appendByte((byte) m4);
			byteBuffer.appendByte((byte) exponent);
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

	}

	private static abstract class VariableToken extends SourceToken {

		protected VariableToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			byteBuffer.appendByte(getVariableTypeCode());
			byteBuffer.appendWord(0);
			String name = getVariableNameWithoutTypeIndicator();
			int n = name.length();
			for (int i = 0; i < n - 1; i++) {
				byteBuffer.appendByte((byte) name.charAt(i));
			}
			byteBuffer.appendByte((byte) (128 + name.charAt(n - 1)));
		}

		protected abstract byte getVariableTypeCode();

		protected abstract String getVariableNameWithoutTypeIndicator();

	}

	private static abstract class TypedVariableToken extends VariableToken {

		protected TypedVariableToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		protected String getVariableNameWithoutTypeIndicator() {
			return getSourceFragment().substring(0, getSourceFragment().length() - 1);
		}

	}

	private static class IntegerTypedVariableToken extends TypedVariableToken {

		public IntegerTypedVariableToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		protected byte getVariableTypeCode() {
			return (byte) 0x02;
		}

	}

	private static class StringTypedVariableToken extends TypedVariableToken {

		public StringTypedVariableToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		protected byte getVariableTypeCode() {
			return (byte) 0x03;
		}

	}

	private static class FloatingPointTypedVariableToken extends TypedVariableToken {

		public FloatingPointTypedVariableToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		protected byte getVariableTypeCode() {
			return (byte) 0x04;
		}

	}

	private static class UntypedVariableToken extends VariableToken {

		public UntypedVariableToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		protected byte getVariableTypeCode() {
			return (byte) 0x0d;
		}

		@Override
		protected String getVariableNameWithoutTypeIndicator() {
			return getSourceFragment();
		}

	}

	private static class BasicKeywordToken extends SourceToken {

		private BasicKeyword keyword;

		public static final char REMARK_SHORTHAND = '\'';

		public BasicKeywordToken(String sourceFragment, BasicKeyword keyword) {
			super(sourceFragment);
			this.keyword = keyword;
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			BasicKeyword keyword = getKeyword();
			if (keyword.isPrecededByInstructionSeparator()) {
				byteBuffer.appendByte((byte) 0x01); // instruction separator
			}
			if (keyword.isExtendedKeyword()) {
				byteBuffer.appendByte(keyword.getPrefixByte());
			}
			byteBuffer.appendByte(keyword.getCodeByte());
		}

		public BasicKeyword getKeyword() {
			return keyword;
		}

	}

	private static class OperatorToken extends SourceToken {

		public OperatorToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			String symbol = getSourceFragment().toUpperCase();
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

	}

	private static class LiteralToken extends SourceToken {

		public static final char QUOTE = '"';

		public LiteralToken(String sourceFragment) {
			super(sourceFragment);
		}

		@Override
		public void appendByteCodeTo(ByteBuffer byteBuffer) {
			int n = getSourceFragment().length();
			for (int i = 0; i < n; i++) {
				byteBuffer.appendByte((byte) getSourceFragment().charAt(i));
			}
		}

	}

}