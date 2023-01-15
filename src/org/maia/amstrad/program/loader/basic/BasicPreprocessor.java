package org.maia.amstrad.program.loader.basic;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCodeLine;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public abstract class BasicPreprocessor {

	protected BasicPreprocessor() {
	}

	protected abstract void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException;

	protected void addCodeLine(BasicSourceCode sourceCode, int lineNumber, String lineCode) throws BasicException {
		sourceCode.addLine(new LocomotiveBasicSourceCodeLine(lineNumber + " " + lineCode));
	}

	protected void addCodeLine(BasicSourceCode sourceCode, BasicSourceTokenSequence sequence) throws BasicException {
		sourceCode.addLine(new LocomotiveBasicSourceCodeLine(sequence.getSourceCode()));
	}

	protected BasicSourceToken createKeywordToken(String keyword) throws BasicSyntaxException {
		return LocomotiveBasicSourceTokenFactory.getInstance().createBasicKeyword(keyword);
	}

}