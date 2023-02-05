package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class RunBasicPreprocessor extends ChainRunBasicPreprocessor {

	public RunBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // shares the chainrun macro
	}

	@Override
	protected void invokeChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (!originalCodeContainsKeyword(sourceCode, "RUN", session))
			return;
		ChainRunMacro macro = session.getMacroAdded(ChainRunMacro.class);
		// TODO
	}

}