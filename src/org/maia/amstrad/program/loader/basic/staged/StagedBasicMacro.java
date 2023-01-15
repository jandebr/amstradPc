package org.maia.amstrad.program.loader.basic.staged;

public abstract class StagedBasicMacro {

	private int lineNumberStart;

	protected StagedBasicMacro(int lineNumberStart) {
		this.lineNumberStart = lineNumberStart;
	}

	public int getLineNumberStart() {
		return lineNumberStart;
	}

}