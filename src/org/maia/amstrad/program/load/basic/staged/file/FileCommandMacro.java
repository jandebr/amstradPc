package org.maia.amstrad.program.load.basic.staged.file;

import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.program.load.basic.staged.StagedBasicMacro;

public abstract class FileCommandMacro extends StagedBasicMacro {

	private int resumeMemoryAddress;

	protected FileCommandMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
		super(range);
		this.resumeMemoryAddress = resumeMemoryAddress;
	}

	public int getResumeMemoryAddress() {
		return resumeMemoryAddress;
	}

}