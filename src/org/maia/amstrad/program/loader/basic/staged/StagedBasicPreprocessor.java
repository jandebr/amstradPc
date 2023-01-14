package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;
import org.maia.amstrad.program.loader.basic.BasicPreprocessor;

public abstract class StagedBasicPreprocessor extends BasicPreprocessor {

	protected StagedBasicPreprocessor() {
	}

	@Override
	protected void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session) throws BasicException {
		stage(sourceCode, (StagedBasicProgramLoaderSession) session);
	}

	protected abstract void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	protected void runInSeparateThread(Runnable task) {
		new Thread(task).start();
	}

}