package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.AmstradProgramLoaderSession;
import org.maia.amstrad.load.basic.BasicPreprocessor;

public abstract class StagedBasicPreprocessor extends BasicPreprocessor {

	protected StagedBasicPreprocessor() {
	}

	protected abstract int getDesiredPreambleLineCount();

	@Override
	protected final void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException {
		stage(sourceCode, (StagedBasicProgramLoaderSession) session);
	}

	protected abstract void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	protected BasicLineNumberLinearMapping renum(BasicSourceCode sourceCode, int lineNumberStart, int lineNumberStep,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicLineNumberLinearMapping mapping = sourceCode.renum(lineNumberStart, lineNumberStep);
		session.renumMacros(mapping); // keep line numbers in sync
		return mapping;
	}

	protected boolean originalCodeContainsKeyword(BasicSourceCode sourceCode, String keyword,
			StagedBasicProgramLoaderSession session) throws BasicException {
		return codeContainsKeyword(sourceCode, session.getScopeExcludingMacros(), keyword);
	}

}