package org.maia.amstrad.pc.basic.locomotive;

import java.util.InputMismatchException;
import java.util.Set;
import java.util.StringTokenizer;

import org.maia.amstrad.pc.basic.BasicCompiler;
import org.maia.amstrad.pc.basic.BasicRuntime;
import org.maia.amstrad.pc.basic.locomotive.LocomotiveBasicKeywords.BasicKeyword;

public class LocomotiveBasicCompiler extends LocomotiveBasicProcessor implements BasicCompiler {

	private byte[] byteCode;

	private int byteCodeIndex;

	public LocomotiveBasicCompiler() {
	}

	@Override
	public byte[] compile(CharSequence sourceCode) {
		init(sourceCode);
		StringTokenizer st = new StringTokenizer(sourceCode.toString(), "\n\r");
		while (st.hasMoreTokens()) {
			String line = st.nextToken().trim();
			if (!line.isEmpty()) {
				int i0 = byteCodeIndex;
				appendWord(0); // placeholder line length
				compileLine(line);
				replaceWordAt(i0, byteCodeIndex - i0); // actual line length
			}
		}
		appendWord(0); // end of program
		return getTrimmedByteCode();
	}

	protected void init(CharSequence sourceCode) {
		int maxBytes = BasicRuntime.MEMORY_POINTER_END_OF_PROGRAM - BasicRuntime.MEMORY_ADDRESS_START_OF_PROGRAM;
		this.byteCode = new byte[maxBytes];
		this.byteCodeIndex = 0;
	}

	private void compileLine(String line) {
		TextScanner scanner = new TextScanner(line);
		scanner.skipWhitespace();
		try {
			int lineNumber = scanner.scanInt();
			appendWord(lineNumber);
			scanner.skipWhitespace();
			compileLineBody(scanner);
			appendByte(0); // end of line
		} catch (InputMismatchException e) {
			throw new MalformedSyntaxException("Could not parse line number", line);
		}
	}

	private void compileLineBody(TextScanner scanner) {
		while (!scanner.atEndOfText()) {
			if (scanner.hasBasicKeywordAhead()) {
				BasicKeyword keyword = scanner.scanBasicKeyword();
				if (keyword.isExtendedKeyword()) {
					appendByte(keyword.getPrefixByte());
				}
				appendByte(keyword.getCodeByte());
			} else {
				char c = scanner.scanChar();
				if (c == ':') {
					appendByte(0x01); // instruction separator
				} else {
					appendByte(c); // literal character
				}
			}
		}
	}

	private void appendByte(int value) {
		byteCode[byteCodeIndex++] = (byte) (value & 0xff);
	}

	private void appendWord(int value) {
		// little-endian
		appendByte(value % 256);
		appendByte(value / 256);
	}

	private void replaceByteAt(int index, int value) {
		byteCode[index] = (byte) (value & 0xff);
	}

	private void replaceWordAt(int index, int value) {
		// little-endian
		replaceByteAt(index, value % 256);
		replaceByteAt(index + 1, value / 256);
	}

	private byte[] getTrimmedByteCode() {
		int n = byteCodeIndex;
		byte[] bytes = new byte[n];
		System.arraycopy(byteCode, 0, bytes, 0, n);
		return bytes;
	}

	@SuppressWarnings("serial")
	public static class MalformedSyntaxException extends RuntimeException {

		private String codeLine;

		public MalformedSyntaxException(String message, String codeLine) {
			super(message);
			setCodeLine(codeLine);
		}

		public String getCodeLine() {
			return codeLine;
		}

		private void setCodeLine(String codeLine) {
			this.codeLine = codeLine;
		}

	}

	private class TextScanner {

		private String text;

		private int position;

		public TextScanner(String text) {
			this.text = text;
		}

		public boolean atEndOfText() {
			return getPosition() >= getText().length();
		}

		public void skipWhitespace() {
			while (!atEndOfText() && isWhitespace(getCurrentChar()))
				advancePosition();
		}

		public boolean hasIntAhead() {
			boolean ahead = false;
			int p0 = getPosition();
			try {
				scanInt();
				ahead = true;
			} catch (InputMismatchException e) {
			} finally {
				setPosition(p0);
			}
			return ahead;
		}

		public int scanInt() throws InputMismatchException {
			int p0 = getPosition();
			while (!atEndOfText() && isDigit(getCurrentChar()))
				advancePosition();
			try {
				return Integer.parseInt(subText(p0, getPosition()));
			} catch (NumberFormatException e) {
				setPosition(p0);
				throw new InputMismatchException(e.getMessage());
			}
		}

		public boolean hasCharAhead() {
			return !atEndOfText();
		}

		public char scanChar() throws InputMismatchException {
			if (!atEndOfText()) {
				char c = getCurrentChar();
				advancePosition();
				return c;
			} else {
				throw new InputMismatchException("No character ahead");
			}
		}

		public boolean hasBasicKeywordAhead() {
			boolean ahead = false;
			int p0 = getPosition();
			try {
				scanBasicKeyword();
				ahead = true;
			} catch (InputMismatchException e) {
			} finally {
				setPosition(p0);
			}
			return ahead;
		}

		public BasicKeyword scanBasicKeyword() throws InputMismatchException {
			if (!atEndOfText()) {
				int maxSymbolLength = charsRemaining();
				Set<BasicKeyword> keywords = getBasicKeywords().getKeywordsStartingWith(
						Character.toUpperCase(getCurrentChar()));
				for (BasicKeyword keyword : keywords) {
					String symbol = keyword.getSourceForm();
					if (symbol.length() <= maxSymbolLength
							&& subText(getPosition(), getPosition() + symbol.length()).toUpperCase().equals(symbol)) {
						advancePosition(symbol.length());
						return keyword;
					}
				}
			}
			throw new InputMismatchException("No Basic keyword ahead");
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

		private char getCurrentChar() {
			return getText().charAt(getPosition());
		}

		private boolean isWhitespace(char c) {
			return Character.isWhitespace(c);
		}

		private boolean isDigit(char c) {
			return Character.isDigit(c);
		}

		private String subText(int fromPosition, int toPosition) {
			return getText().substring(fromPosition, toPosition);
		}

		public String getText() {
			return text;
		}

		private int getPosition() {
			return position;
		}

		private void setPosition(int position) {
			this.position = position;
		}

	}

}