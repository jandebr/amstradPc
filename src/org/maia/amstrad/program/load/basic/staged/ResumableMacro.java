package org.maia.amstrad.program.load.basic.staged;

import org.maia.amstrad.basic.BasicLineNumberRange;

public abstract class ResumableMacro extends StagedBasicMacro {

	private int resumeMemoryAddress;

	protected ResumableMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
		super(range);
		this.resumeMemoryAddress = resumeMemoryAddress;
	}

	public int getResumeMemoryAddress() {
		return resumeMemoryAddress;
	}

}