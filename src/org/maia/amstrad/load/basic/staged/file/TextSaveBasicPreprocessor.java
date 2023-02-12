package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class TextSaveBasicPreprocessor extends FileCommandBasicPreprocessor {

	public TextSaveBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // reusing waitresume macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		invokeTextSave(sourceCode, session);
	}

	private void invokeTextSave(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		// TODO
	}

}