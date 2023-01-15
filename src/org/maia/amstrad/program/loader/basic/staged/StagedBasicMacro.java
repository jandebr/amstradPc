package org.maia.amstrad.program.loader.basic.staged;

public abstract class StagedBasicMacro {

	private int lineNumberStart;

	private int lineNumberEnd;

	protected StagedBasicMacro(int lineNumberStart, int lineNumberEnd) {
		this.lineNumberStart = lineNumberStart;
		this.lineNumberEnd = lineNumberEnd;
	}

	public boolean containsLine(int lineNumber) {
		return lineNumber >= getLineNumberStart() && lineNumber <= getLineNumberEnd();
	}

	public int getLineNumberStart() {
		return lineNumberStart;
	}

	public int getLineNumberEnd() {
		return lineNumberEnd;
	}

}