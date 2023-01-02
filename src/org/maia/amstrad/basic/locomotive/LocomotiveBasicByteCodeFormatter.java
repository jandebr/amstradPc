package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.util.StringUtils;

public class LocomotiveBasicByteCodeFormatter {

	public LocomotiveBasicByteCodeFormatter() {
	}

	public CharSequence format(LocomotiveBasicByteCode byteCode) {
		return format(byteCode, new boolean[byteCode.getByteCount()]);
	}

	public CharSequence format(LocomotiveBasicByteCode byteCode, boolean[] underline) {
		return format(byteCode, underline, false);
	}

	public CharSequence format(LocomotiveBasicByteCode byteCode, boolean[] underline, boolean underlinedLinesOnly) {
		StringBuilder sb = new StringBuilder(2048);
		StringBuilder line = new StringBuilder(128);
		StringBuilder undr = new StringBuilder(128);
		int bi = 0;
		int lineLength, lineNumber;
		do {
			line.setLength(0);
			undr.setLength(0);
			lineLength = byteCode.getWord(bi);
			for (int i = 0; i < 2; i++) {
				line.append(StringUtils.rightPad(formatByte(byteCode.getByte(bi)), 3, ' '));
				undr.append(StringUtils.rightPad(formatUnderline(underline[bi]), 3, ' '));
				bi++;
			}
			if (lineLength > 0) {
				lineNumber = byteCode.getWord(bi);
				line.insert(0, StringUtils.rightPad(Integer.toString(lineNumber), 5, ' ') + " | ");
				undr.insert(0, StringUtils.repeat(' ', 8));
				for (int i = 0; i < 2; i++) {
					line.append(StringUtils.rightPad(formatByte(byteCode.getByte(bi)), 3, ' '));
					undr.append(StringUtils.rightPad(formatUnderline(underline[bi]), 3, ' '));
					bi++;
				}
				line.append("| ");
				undr.append("  ");
				for (int i = 0; i < lineLength - 4; i++) {
					line.append(StringUtils.rightPad(formatByte(byteCode.getByte(bi)), 3, ' '));
					undr.append(StringUtils.rightPad(formatUnderline(underline[bi]), 3, ' '));
					bi++;
				}
			} else {
				line.insert(0, StringUtils.repeat(' ', 8));
				undr.insert(0, StringUtils.repeat(' ', 8));
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
			return StringUtils.repeat('^', 2);
		} else {
			return StringUtils.repeat(' ', 2);
		}
	}

}