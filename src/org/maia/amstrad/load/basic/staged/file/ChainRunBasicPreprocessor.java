package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class ChainRunBasicPreprocessor extends StagedBasicPreprocessor {

	public ChainRunBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 0;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		// TODO Auto-generated method stub
	}

}