package org.maia.amstrad.basic;

public abstract class BasicLineNumberScope {

	protected BasicLineNumberScope() {
	}

	public boolean isInScope(BasicSourceCodeLine line) {
		return isInScope(line.getLineNumber());
	}

	public abstract boolean isInScope(int lineNumber);

}