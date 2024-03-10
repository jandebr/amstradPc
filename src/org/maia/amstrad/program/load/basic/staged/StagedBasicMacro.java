package org.maia.amstrad.program.load.basic.staged;

import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberRange;

public abstract class StagedBasicMacro {

	private BasicLineNumberRange lineNumberRange;

	protected StagedBasicMacro() {
		this(null);
	}

	protected StagedBasicMacro(BasicLineNumberRange range) {
		setLineNumberRange(range);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName());
		if (isDeclaredInSourceCode()) {
			builder.append("[");
			builder.append(getLineNumberFrom());
			builder.append(",");
			builder.append(getLineNumberTo());
			builder.append("]");
		}
		return builder.toString();
	}

	public void renum(BasicLineNumberLinearMapping mapping) {
		if (isDeclaredInSourceCode()) {
			if (mapping.isMapped(getLineNumberFrom()) && mapping.isMapped(getLineNumberTo())) {
				int lnFrom = mapping.getNewLineNumber(getLineNumberFrom());
				int lnTo = mapping.getNewLineNumber(getLineNumberTo());
				setLineNumberRange(new BasicLineNumberRange(lnFrom, lnTo));
			}
		}
	}

	public boolean isDeclaredInSourceCode() {
		return getLineNumberRange() != null;
	}

	public int getLineNumberFrom() {
		return isDeclaredInSourceCode() ? getLineNumberRange().getLineNumberFrom() : -1;
	}

	public int getLineNumberTo() {
		return isDeclaredInSourceCode() ? getLineNumberRange().getLineNumberTo() : -1;
	}

	public BasicLineNumberRange getLineNumberRange() {
		return lineNumberRange;
	}

	private void setLineNumberRange(BasicLineNumberRange range) {
		this.lineNumberRange = range;
	}

}