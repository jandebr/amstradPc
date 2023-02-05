package org.maia.amstrad.basic;

public class BasicLineNumberRange {

	private int lineNumberFrom;

	private int lineNumberTo;

	public BasicLineNumberRange(int lineNumber) {
		this(lineNumber, lineNumber);
	}

	public BasicLineNumberRange(int lineNumberFrom, int lineNumberTo) {
		if (lineNumberFrom > lineNumberTo)
			throw new IllegalArgumentException("Invalid range: " + lineNumberFrom + " to " + lineNumberTo);
		this.lineNumberFrom = lineNumberFrom;
		this.lineNumberTo = lineNumberTo;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BasicLineNumberRange[");
		builder.append(getLineNumberFrom());
		builder.append(",");
		builder.append(getLineNumberTo());
		builder.append("]");
		return builder.toString();
	}

	public boolean containsLineNumber(int lineNumber) {
		return lineNumber >= getLineNumberFrom() && lineNumber <= getLineNumberTo();
	}

	public int getLineNumberFrom() {
		return lineNumberFrom;
	}

	public int getLineNumberTo() {
		return lineNumberTo;
	}

}