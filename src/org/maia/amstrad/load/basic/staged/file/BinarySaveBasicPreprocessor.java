package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class BinarySaveBasicPreprocessor extends BinaryIOBasicPreprocessor {

	public BinarySaveBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // shares the binaryio macro
	}

	@Override
	protected void invokeBinaryIOMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (!originalCodeContainsKeyword(sourceCode, "SAVE", session))
			return;
		BinaryIOMacro macro = session.getMacroAdded(BinaryIOMacro.class);
		// TODO
	}

}