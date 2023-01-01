package org.maia.amstrad.basic;

public class BasicSyntaxException extends BasicException {

	private String text;

	private int positionInText;

	public BasicSyntaxException(String message, String text) {
		this(message, text, 0);
	}

	public BasicSyntaxException(String message, String text, int positionInText) {
		super(message);
		this.text = text;
		this.positionInText = positionInText;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder(256);
		sb.append(super.getMessage());
		sb.append(" at ").append(getPositionInText() + 1);
		sb.append(" in ");
		if (getText().length() > 32) {
			sb.append(getText().substring(0, 32)).append("...");
		} else {
			sb.append(getText());
		}
		return sb.toString();
	}

	public String getText() {
		return text;
	}

	public int getPositionInText() {
		return positionInText;
	}

}