package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class ChainBasicPreprocessor extends ChainRunBasicPreprocessor {

	public ChainBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 2; // for chainrun macro
	}

	@Override
	protected void invokeChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (!originalCodeContainsKeyword(sourceCode, "CHAIN", session))
			return;
		ChainRunMacro macro = session.getMacroAdded(ChainRunMacro.class);
		// TODO
	}

}