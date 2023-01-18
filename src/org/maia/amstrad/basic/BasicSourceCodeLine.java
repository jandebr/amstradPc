package org.maia.amstrad.basic;

public abstract class BasicSourceCodeLine implements Cloneable, Comparable<BasicSourceCodeLine> {

	private String text;

	private int lineNumber;

	private BasicLanguage language;

	private BasicSourceCode parentSourceCode;

	private BasicSourceTokenSequence nativeTokenSequence;

	protected BasicSourceCodeLine(BasicLanguage language, String text) throws BasicSyntaxException {
		this.language = language;
		setText(text);
		setLineNumber(parseLineNumber(text));
	}

	@Override
	public BasicSourceCodeLine clone() {
		BasicSourceCodeLine clone = null;
		try {
			clone = (BasicSourceCodeLine) super.clone();
		} catch (CloneNotSupportedException e) {
			// never the case
		}
		return clone;
	}

	public synchronized void editTo(String text) throws BasicException {
		int oldLineNumber = getLineNumber();
		int newLineNumber = parseLineNumber(text);
		setText(text);
		setLineNumber(newLineNumber);
		setNativeTokenSequence(null); // recreate on demand
		if (hasParentSourceCode() && newLineNumber != oldLineNumber) {
			if (oldLineNumber != 0) {
				getParentSourceCode().removeLineNumber(oldLineNumber);
			}
			getParentSourceCode().addLine(this);
		}
	}

	private int parseLineNumber(String text) throws BasicSyntaxException {
		return new LineNumberParser(text).firstToken().getLineNumber();
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
		if (getNativeTokenSequence() != null) {
			return getNativeTokenSequence().clone(); // can be edited independently
		} else {
			BasicSourceTokenSequence sequence = new BasicSourceTokenSequence();
			BasicSourceCodeLineScanner scanner = createScanner();
			sequence.append(scanner.firstToken());
			while (!scanner.atEndOfText()) {
				sequence.append(scanner.nextToken());
			}
			setNativeTokenSequence(sequence.clone());
			return sequence; // can be edited independently
		}
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

	public BasicLanguage getLanguage() {
		return language;
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

	private BasicSourceTokenSequence getNativeTokenSequence() {
		return nativeTokenSequence;
	}

	private void setNativeTokenSequence(BasicSourceTokenSequence nativeTokenSequence) {
		this.nativeTokenSequence = nativeTokenSequence;
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