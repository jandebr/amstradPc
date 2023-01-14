package org.maia.amstrad.program.loader.basic;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public abstract class BasicPreprocessor {

	protected BasicPreprocessor() {
	}

	protected abstract void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException;

}