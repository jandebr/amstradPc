package org.maia.amstrad.program.load.basic.staged.file;

import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.program.load.basic.staged.WaitResumeBasicPreprocessor.WaitResumeMacro;

public abstract class FileCommandMacro extends WaitResumeMacro {

	protected FileCommandMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
		super(range, resumeMemoryAddress);
	}

}