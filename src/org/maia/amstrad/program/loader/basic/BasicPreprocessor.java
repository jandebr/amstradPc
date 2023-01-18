package org.maia.amstrad.program.loader.basic;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public abstract class BasicPreprocessor {

	protected BasicPreprocessor() {
	}

	protected abstract void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException;

	protected void addCodeLine(BasicSourceCode sourceCode, int lineNumber, String lineCode) throws BasicException {
		sourceCode.addLine(
				BasicLanguageKit.forLanguage(sourceCode.getLanguage()).createSourceCodeLine(lineNumber, lineCode));
	}

	protected void addCodeLine(BasicSourceCode sourceCode, BasicSourceTokenSequence sequence) throws BasicException {
		sourceCode.addLine(BasicLanguageKit.forLanguage(sourceCode.getLanguage()).createSourceCodeLine(sequence));
	}

	protected BasicSourceToken createKeywordToken(BasicLanguage language, String keyword) throws BasicSyntaxException {
		return BasicLanguageKit.forLanguage(language).createKeywordToken(keyword);
	}

}