package org.maia.amstrad.basic;

public class BasicByteCodeFormatter {

	public BasicByteCodeFormatter() {
	}

	public CharSequence format(byte[] byteCode) {
		return format(byteCode, new boolean[byteCode.length]);
	}

	public CharSequence format(byte[] byteCode, boolean[] underline) {
		return format(byteCode, underline, false);
	}

	public CharSequence format(byte[] byteCode, boolean[] underline, boolean underlinedLinesOnly) {
		StringBuilder sb = new StringBuilder(2048);
		StringBuilder line = new StringBuilder(128);
		StringBuilder undr = new StringBuilder(128);
		int bi = 0;
		int lineLength, lineNumber;
		do {
			line.setLength(0);
			undr.setLength(0);
			lineLength = wordAt(byteCode, bi);
			for (int i = 0; i < 2; i++) {
				line.append(rightPad(formatByte(byteCode[bi]), 3, ' '));
				undr.append(rightPad(formatUnderline(underline[bi]), 3, ' '));
				bi++;
			}
			if (lineLength > 0) {
				lineNumber = wordAt(byteCode, bi);
				line.insert(0, rightPad(Integer.toString(lineNumber), 5, ' ') + " | ");
				undr.insert(0, repeat(8, ' '));
				for (int i = 0; i < 2; i++) {
					line.append(rightPad(formatByte(byteCode[bi]), 3, ' '));
					undr.append(rightPad(formatUnderline(underline[bi]), 3, ' '));
					bi++;
				}
				line.append("| ");
				undr.append("  ");
				for (int i = 0; i < lineLength - 4; i++) {
					line.append(rightPad(formatByte(byteCode[bi]), 3, ' '));
					undr.append(rightPad(formatUnderline(underline[bi]), 3, ' '));
					bi++;
				}
			} else {
				line.insert(0, repeat(8, ' '));
				undr.insert(0, repeat(8, ' '));
			}
			boolean underlined = !undr.toString().trim().isEmpty();
			if (!underlinedLinesOnly || underlined) {
				sb.append(line).append('\n');
				if (underlined) {
					sb.append(undr).append('\n');
				}
			}
		} while (lineLength > 0);
		return sb;
	}

	private String formatByte(byte b) {
		String str = Integer.toString(b & 0xff, 16);
		if (str.length() == 1) {
			return '0' + str;
		} else {
			return str;
		}
	}

	private String formatUnderline(boolean u) {
		if (u) {
			return repeat(2, '^');
		} else {
			return repeat(2, ' ');
		}
	}

	private String repeat(int width, char c) {
		return rightPad("", width, c);
	}

	private String rightPad(String str, int width, char pad) {
		int n = str.length();
		if (n >= width) {
			return str;
		} else {
			StringBuilder sb = new StringBuilder(width);
			sb.append(str);
			for (int i = 0; i < width - n; i++)
				sb.append(pad);
			return sb.toString();
		}
	}

	private int wordAt(byte[] byteCode, int index) {
		return (byteCode[index] & 0xff) | ((byteCode[index + 1] << 8) & 0xff00);
	}

}