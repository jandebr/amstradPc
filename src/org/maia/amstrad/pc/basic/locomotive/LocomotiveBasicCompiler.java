package org.maia.amstrad.pc.basic.locomotive;

import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import org.maia.amstrad.pc.basic.BasicCompiler;
import org.maia.amstrad.pc.basic.BasicRuntime;
import org.maia.amstrad.pc.basic.locomotive.LocomotiveTokenMap.Token;

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
		int i = line.indexOf(' ');
		if (i < 0)
			throw new MalformedSyntaxException("Could not split on line number: " + line);
		try {
			int lineNumber = Integer.parseInt(line.substring(0, i));
			appendWord(lineNumber);
			compileCode(line.substring(i + 1));
			appendByte(0); // end of line
		} catch (NumberFormatException e) {
			throw new MalformedSyntaxException("Could not parse line number: " + line);
		}
	}

	private void compileCode(String code) {
		String upperCode = code.toUpperCase(Locale.US);
		int ci = 0;
		while (ci < code.length()) {
			Token token = tokenAhead(upperCode, ci);
			if (token != null) {
				if (token.isExtendedToken()) {
					appendByte(0xff);
				}
				appendByte(token.getCodeByte());
				ci += token.getSourceForm().length();
			} else {
				char c = code.charAt(ci++);
				if (c == ':') {
					appendByte(0x01); // instruction separator
				} else {
					appendByte(c); // literal character
				}
			}
		}
	}

	private Token tokenAhead(String upperCode, int fromIndex) {
		int maxSymbolLength = upperCode.length() - fromIndex;
		Set<Token> tokens = getTokenMap().getTokensStartingWith(upperCode.charAt(fromIndex));
		for (Token token : tokens) {
			String symbol = token.getSourceForm();
			if (symbol.length() <= maxSymbolLength
					&& upperCode.substring(fromIndex, fromIndex + symbol.length()).equals(symbol)) {
				return token;
			}
		}
		return null;
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

		public MalformedSyntaxException(String message) {
			super(message);
		}

	}

}