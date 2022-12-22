package org.maia.amstrad.basic.locomotive.source;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords;

public class BasicSourceCodeLine implements Comparable<BasicSourceCodeLine> {

	private String text;

	private int lineNumber;

	private BasicSourceCode parentSourceCode;

	public BasicSourceCodeLine(String text) throws BasicSyntaxException {
		editTo(text);
	}

	public BasicSourceCodeLineScanner createScanner() {
		return new BasicSourceCodeLineScanner(getText(), LocomotiveBasicKeywords.getInstance());
	}

	public synchronized void editTo(String text) throws BasicSyntaxException {
		if (text == null)
			throw new NullPointerException("null line");
		int oldLineNumber = getLineNumber();
		int newLineNumber = parseLineNumber(text);
		setText(text);
		setLineNumber(newLineNumber);
		if (hasParentSourceCode() && newLineNumber != oldLineNumber) {
			if (oldLineNumber != 0) {
				getParentSourceCode().removeLineNumber(oldLineNumber);
			}
			getParentSourceCode().addLine(this);
		}
	}

	private int parseLineNumber(String text) throws BasicSyntaxException {
		int lineNumber = new BasicSourceCodeLineScanner(text, LocomotiveBasicKeywords.getInstance()).firstToken()
				.getValue();
		if (lineNumber < BasicRuntime.MINIMUM_BASIC_LINE_NUMBER
				|| lineNumber > BasicRuntime.MAXIMUM_BASIC_LINE_NUMBER) {
			throw new BasicSyntaxException("Line number out of range", text, 0);
		}
		return lineNumber;
	}

	@Override
	public int compareTo(BasicSourceCodeLine otherLine) {
		if (getLineNumber() < otherLine.getLineNumber())
			return -1;
		else if (getLineNumber() > otherLine.getLineNumber())
			return 1;
		else
			return 0;
	}

	@Override
	public String toString() {
		return getText();
	}

	public String toStringInParsedForm() {
		BasicSourceCodeLineScanner scanner = createScanner();
		StringBuilder sb = new StringBuilder(256);
		while (!scanner.atEndOfText()) {
			if (sb.length() > 0)
				sb.append(' ');
			SourceToken token = null;
			try {
				token = scanner.nextToken();
			} catch (BasicSyntaxException e) {
			}
			if (token != null) {
				sb.append(token);
			} else {
				sb.append("NULL");
			}
		}
		return sb.toString();
	}

	public String getText() {
		return text;
	}

	private void setText(String text) {
		this.text = text;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	private void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	private boolean hasParentSourceCode() {
		return getParentSourceCode() != null;
	}

	private BasicSourceCode getParentSourceCode() {
		return parentSourceCode;
	}

	void setParentSourceCode(BasicSourceCode parentSourceCode) {
		this.parentSourceCode = parentSourceCode;
	}

}