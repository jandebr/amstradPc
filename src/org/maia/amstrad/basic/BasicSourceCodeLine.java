package org.maia.amstrad.basic;

public abstract class BasicSourceCodeLine implements Comparable<BasicSourceCodeLine> {

	private String text;

	private int lineNumber;

	private BasicSourceCode parentSourceCode;

	protected BasicSourceCodeLine(String text) throws BasicSyntaxException {
		editTo(text);
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
		int lineNumber = new LineNumberParser(text).firstToken().getLineNumber();
		if (lineNumber < BasicRuntime.MINIMUM_BASIC_LINE_NUMBER
				|| lineNumber > BasicRuntime.MAXIMUM_BASIC_LINE_NUMBER) {
			throw new BasicSyntaxException("Line number out of range", text);
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

	public String toStringInParsedForm() throws BasicSyntaxException {
		return parse().toString();
	}

	public BasicSourceTokenSequence parse() throws BasicSyntaxException {
		BasicSourceTokenSequence sequence = new BasicSourceTokenSequence();
		BasicSourceCodeLineScanner scanner = createScanner();
		sequence.append(scanner.firstToken());
		while (!scanner.atEndOfText()) {
			sequence.append(scanner.nextToken());
		}
		return sequence;
	}

	public abstract BasicSourceCodeLineScanner createScanner();

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

	private static class LineNumberParser extends BasicSourceCodeLineScanner {

		public LineNumberParser(String text) {
			super(text);
		}

		@Override
		public BasicLineNumberToken nextToken() {
			return null; // only first token (line number) is required here
		}

	}

}