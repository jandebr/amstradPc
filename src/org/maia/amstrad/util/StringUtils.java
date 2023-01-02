package org.maia.amstrad.util;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;

public class StringUtils {

	private StringUtils() {
	}

	public static boolean isBlank(String str) {
		if (str == null)
			return true;
		for (int i = 0; i < str.length(); i++) {
			if (!Character.isWhitespace(str.charAt(i)))
				return false;
		}
		return true;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.isEmpty();
	}

	public static String emptyForNull(String str) {
		if (str == null)
			return "";
		else
			return str;
	}

	public static String spaces(int width) {
		return repeat(' ', width);
	}

	public static String repeat(char c, int times) {
		if (times == 0) {
			return "";
		} else if (times == 1) {
			return String.valueOf(c);
		} else {
			StringBuilder sb = new StringBuilder(times);
			for (int i = 0; i < times; i++)
				sb.append(c);
			return sb.toString();
		}
	}

	public static String fitWidth(String str, int width) {
		return fitWidthLeftAlign(str, width);
	}

	public static String fitWidthLeftAlign(String str, int width) {
		int n = str.length();
		if (n == width) {
			return str;
		} else if (n > width) {
			return str.substring(0, width - 2) + "..";
		} else {
			return str + spaces(width - n);
		}
	}

	public static String fitWidthRightAlign(String str, int width) {
		int n = str.length();
		if (n == width) {
			return str;
		} else if (n > width) {
			return ".." + str.substring(n - width, n);
		} else {
			return spaces(width - n) + str;
		}
	}

	public static String fitWidthCenterAlign(String str, int width) {
		int n = str.length();
		if (n == width) {
			return str;
		} else if (n > width) {
			return str.substring(0, width - 2) + "..";
		} else {
			int spacesBefore = (width - n) / 2;
			int spacesAfter = width - n - spacesBefore;
			return spaces(spacesBefore) + str + spaces(spacesAfter);
		}
	}

	public static String rightPad(String str, int width, char padding) {
		int n = str.length();
		if (n >= width) {
			return str;
		} else {
			return str + repeat(padding, width - n);
		}
	}

	public static List<String> splitOnNewlinesAndWrap(String str, int width) {
		List<String> wrappedLines = new Vector<String>();
		List<String> lines = splitOnNewlines(str);
		for (String line : lines) {
			for (String wrappedLine : wrap(line, width)) {
				wrappedLines.add(wrappedLine);
			}
		}
		return wrappedLines;
	}

	public static List<String> splitOnNewlines(String str) {
		List<String> lines = new Vector<String>();
		if (!isEmpty(str)) {
			StringTokenizer st = new StringTokenizer(str, "\r\n");
			while (st.hasMoreTokens()) {
				lines.add(st.nextToken());
			}
		}
		return lines;
	}

	public static List<String> wrap(String str, int width) {
		List<String> lines = new Vector<String>();
		if (!isEmpty(str)) {
			StringBuilder line = new StringBuilder(width);
			int lastWsIndex = -1;
			int i = 0;
			while (i < str.length()) {
				char c = str.charAt(i++);
				boolean ws = Character.isWhitespace(c);
				if (line.length() == width) {
					int wrapFromIndex = width;
					if (lastWsIndex > 0 && !ws) {
						wrapFromIndex = lastWsIndex + 1;
					}
					lines.add(line.substring(0, wrapFromIndex).trim());
					line.delete(0, wrapFromIndex);
					lastWsIndex = -1;
				}
				if (line.length() > 0 || !ws) {
					if (ws) {
						lastWsIndex = line.length();
					}
					line.append(c);
				}
			}
			if (line.length() > 0)
				lines.add(line.toString().trim());
		}
		return lines;
	}

	public static int toInt(String str, int defaultValue) {
		int result = defaultValue;
		if (!isEmpty(str)) {
			try {
				result = Integer.parseInt(str);
			} catch (NumberFormatException e) {
			}
		}
		return result;
	}

	public static AmstradMonitorMode toMonitorMode(String str, AmstradMonitorMode defaultMode) {
		AmstradMonitorMode result = defaultMode;
		if (!isEmpty(str)) {
			try {
				result = AmstradMonitorMode.valueOf(str);
			} catch (IllegalArgumentException e) {
			}
		}
		return result;
	}

	public static boolean containsIgnoringCase(String str, String substring) {
		if (str == null || substring == null)
			return false;
		if (substring.isEmpty())
			return true;
		if (substring.length() > str.length())
			return false;
		return str.toLowerCase().contains(substring.toLowerCase());
	}

}