package org.maia.amstrad.load.basic;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.load.AmstradProgramLoaderSession;

public abstract class BasicPreprocessor {

	protected BasicPreprocessor() {
	}

	public abstract void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException;

	protected int getNextAvailableLineNumber(BasicSourceCode sourceCode) {
		return sourceCode.getNextAvailableLineNumber(sourceCode.getDominantLineNumberStep());
	}

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

	protected boolean codeContainsKeyword(BasicSourceCode sourceCode, BasicLineNumberScope scope, String keyword)
			throws BasicException {
		BasicSourceToken token = createKeywordToken(sourceCode.getLanguage(), keyword);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				if (sequence.contains(token))
					return true;
			}
		}
		return false;
	}

}