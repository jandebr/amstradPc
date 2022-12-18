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

	public BasicSourceCodeLineScanner createScanner(LocomotiveBasicKeywords basicKeywords) {
		return new BasicSourceCodeLineScanner(getText(), basicKeywords);
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
		int i = 0;
		while (i < text.length() && Character.isDigit(text.charAt(i)))
			i++;
		try {
			int lineNumber = Integer.parseInt(text.substring(0, i));
			if (lineNumber < BasicRuntime.MINIMUM_BASIC_LINE_NUMBER
					|| lineNumber > BasicRuntime.MAXIMUM_BASIC_LINE_NUMBER) {
				throw new BasicSyntaxException("Line number out of range", text, 0);
			}
			return lineNumber;
		} catch (NumberFormatException e) {
			throw new BasicSyntaxException("Expecting a line number", text, 0);
		}
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