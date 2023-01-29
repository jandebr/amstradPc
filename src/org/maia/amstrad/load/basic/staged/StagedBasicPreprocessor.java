package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.load.AmstradProgramLoaderSession;
import org.maia.amstrad.load.basic.BasicPreprocessor;

public abstract class StagedBasicPreprocessor extends BasicPreprocessor {

	protected StagedBasicPreprocessor() {
	}

	@Override
	protected final void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException {
		stage(sourceCode, (StagedBasicProgramLoaderSession) session);
	}

	protected abstract int getDesiredPreambleLineCount();

	protected abstract void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	protected BasicLineNumberLinearMapping renum(BasicSourceCode sourceCode, int lineNumberStart, int lineNumberStep,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicLineNumberLinearMapping mapping = sourceCode.renum(lineNumberStart, lineNumberStep);
		session.renumMacros(mapping); // keep line numbers in sync
		return mapping;
	}

	protected int getNextAvailableLineNumber(BasicSourceCode sourceCode) {
		return sourceCode.getNextAvailableLineNumber(sourceCode.getDominantLineNumberStep());
	}

	protected boolean originalCodeContainsKeyword(BasicSourceCode sourceCode, String keyword,
			StagedBasicProgramLoaderSession session) throws BasicException {
		return codeContainsKeyword(sourceCode, session.getScopeExcludingMacros(), keyword);
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