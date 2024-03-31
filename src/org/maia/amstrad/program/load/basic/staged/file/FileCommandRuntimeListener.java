package org.maia.amstrad.program.load.basic.staged.file;

import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.program.load.basic.staged.StagedBasicProgramLoaderSession;
import org.maia.amstrad.program.load.basic.staged.StagedBasicProgramTrappedRuntimeListener;

public abstract class FileCommandRuntimeListener extends StagedBasicProgramTrappedRuntimeListener {

	private BasicSourceCode sourceCode;

	protected FileCommandRuntimeListener(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session,
			int memoryTrapAddress) {
		super(session, memoryTrapAddress);
		this.sourceCode = sourceCode;
	}

	protected BasicSourceCode getSourceCode() {
		return sourceCode;
	}

}