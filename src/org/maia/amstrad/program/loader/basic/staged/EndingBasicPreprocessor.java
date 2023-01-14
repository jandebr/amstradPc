package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;

public class EndingBasicPreprocessor extends StagedBasicPreprocessor {

	public EndingBasicPreprocessor() {
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		// TODO Auto-generated method stub
		System.out.println("STAGE ENDING");
	}

}