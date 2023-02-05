package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class BinaryLoadBasicPreprocessor extends BinaryIOBasicPreprocessor {

	public BinaryLoadBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 1; // for binaryio macro
	}

	@Override
	protected void invokeBinaryIOMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (!originalCodeContainsKeyword(sourceCode, "LOAD", session))
			return;
		BinaryIOMacro macro = session.getMacroAdded(BinaryIOMacro.class);
		// TODO
	}

}