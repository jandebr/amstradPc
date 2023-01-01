package org.maia.amstrad.basic;

public abstract class BasicSourceCodeLineScanner {

	private String text;

	private int position;

	private boolean atLeadingLineNumber;

	protected BasicSourceCodeLineScanner(String text) {
		this.text = text;
		positionAtLeadingLineNumber();
	}

	private void positionAtLeadingLineNumber() {
		while (!atEndOfText() && !isDecimalDigit(getCurrentChar()))
			advancePosition();
		setAtLeadingLineNumber(!atEndOfText());
	}

	public BasicLineNumberToken firstToken() throws BasicSyntaxException {
		BasicLineNumberToken token = null;
		int p0 = getPosition();
		if (isAtLeadingLineNumber()) {
			while (!atEndOfText() && isDecimalDigit(getCurrentChar()))
				advancePosition();
			if (getPosition() > p0) {
				token = new BasicLineNumberToken(subText(p0, getPosition()));
				if (!atEndOfText() && isWhitespace(getCurrentChar()))
					advancePosition();
				setAtLeadingLineNumber(false);
			}
		}
		if (token != null)
			return token;
		else
			throw new BasicSyntaxException("No line number ahead", getText(), p0);
	}

	public abstract BasicSourceToken nextToken() throws BasicSyntaxException;

	protected void advancePosition() {
		advancePosition(1);
	}

	protected void advancePosition(int n) {
		setPosition(getPosition() + n);
	}

	protected int charsRemaining() {
		return getText().length() - getPosition();
	}

	protected char getCurrentChar() {
		return getText().charAt(getPosition());
	}

	protected boolean isWhitespace(char c) {
		return Character.isWhitespace(c);
	}

	protected boolean isDecimalDigit(char c) {
		return c >= '0' && c <= '9';
	}

	protected boolean isBinaryDigit(char c) {
		return c >= '0' && c <= '1';
	}

	protected boolean isHexadecimalDigit(char c) {
		return isDecimalDigit(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
	}

	protected boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	protected String subText(int fromPosition, int toPosition) {
		return getText().substring(fromPosition, toPosition);
	}

	protected void checkEndOfText() throws BasicSyntaxException {
		if (atEndOfText())
			throw new BasicSyntaxException("Unfinished line", getText(), getPosition());
	}

	public boolean atEndOfText() {
		return getPosition() >= getText().length();
	}

	public String getText() {
		return text;
	}

	public int getPosition() {
		return position;
	}

	private void setPosition(int position) {
		this.position = position;
	}

	protected boolean isAtLeadingLineNumber() {
		return atLeadingLineNumber;
	}

	protected void setAtLeadingLineNumber(boolean atLeadingLineNumber) {
		this.atLeadingLineNumber = atLeadingLineNumber;
	}

}