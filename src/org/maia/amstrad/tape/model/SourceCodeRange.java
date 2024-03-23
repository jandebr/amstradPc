package org.maia.amstrad.tape.model;

public class SourceCodeRange {

	private SourceCodePosition startPosition;

	private SourceCodePosition endPosition;

	public SourceCodeRange(SourceCodePosition startPosition, SourceCodePosition endPosition) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	public String toString() {
		return "SourceCode range [" + getStartPosition() + " , " + getEndPosition() + "]";
	}

	public SourceCodePosition getStartPosition() {
		return startPosition;
	}

	public SourceCodePosition getEndPosition() {
		return endPosition;
	}

}
