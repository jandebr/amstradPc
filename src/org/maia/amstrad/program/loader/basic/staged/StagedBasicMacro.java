package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicLineNumberLinearMapping;

public abstract class StagedBasicMacro {

	private int lineNumberStart;

	private int lineNumberEnd;

	protected StagedBasicMacro(int lineNumber) {
		this(lineNumber, lineNumber);
	}

	protected StagedBasicMacro(int lineNumberStart, int lineNumberEnd) {
		setLineNumberStart(lineNumberStart);
		setLineNumberEnd(lineNumberEnd);
	}

	public void renum(BasicLineNumberLinearMapping mapping) {
		if (mapping.isMapped(getLineNumberStart())) {
			setLineNumberStart(mapping.getNewLineNumber(getLineNumberStart()));
		}
		if (mapping.isMapped(getLineNumberEnd())) {
			setLineNumberEnd(mapping.getNewLineNumber(getLineNumberEnd()));
		}
	}

	public boolean containsLine(int lineNumber) {
		return lineNumber >= getLineNumberStart() && lineNumber <= getLineNumberEnd();
	}

	public int getLineNumberStart() {
		return lineNumberStart;
	}

	private void setLineNumberStart(int lineNumberStart) {
		this.lineNumberStart = lineNumberStart;
	}

	public int getLineNumberEnd() {
		return lineNumberEnd;
	}

	private void setLineNumberEnd(int lineNumberEnd) {
		this.lineNumberEnd = lineNumberEnd;
	}

}