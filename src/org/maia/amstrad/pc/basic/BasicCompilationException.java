package org.maia.amstrad.pc.basic;

public class BasicCompilationException extends Exception {

	private String codeLine;

	private int lineIndex;

	private int positionInLine;

	public BasicCompilationException(String message, String codeLine, int lineIndex, int positionInLine) {
		super(message);
		this.codeLine = codeLine;
		this.lineIndex = lineIndex;
		this.positionInLine = positionInLine;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder(256);
		sb.append(super.getMessage());
		sb.append(" at ").append(getLineIndex() + 1).append(':').append(getPositionInLine() + 1);
		sb.append(" in ");
		if (getCodeLine().length() > 32) {
			sb.append(getCodeLine().substring(0, 32)).append("...");
		} else {
			sb.append(getCodeLine());
		}
		return sb.toString();
	}

	public String getCodeLine() {
		return codeLine;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public int getPositionInLine() {
		return positionInLine;
	}

}