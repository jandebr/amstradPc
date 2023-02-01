package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.Integer8BitDecimalToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.load.AmstradProgramLoaderSession;
import org.maia.amstrad.load.basic.BasicPreprocessor;
import org.maia.amstrad.load.basic.staged.ErrorOutBasicPreprocessor.ErrorOutMacro;

public abstract class StagedBasicPreprocessor extends BasicPreprocessor {

	protected StagedBasicPreprocessor() {
	}

	protected abstract int getDesiredPreambleLineCount();

	@Override
	protected final void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
			throws BasicException {
		stage(sourceCode, (StagedBasicProgramLoaderSession) session);
	}

	protected abstract void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	protected BasicLineNumberLinearMapping renum(BasicSourceCode sourceCode, int lineNumberStart, int lineNumberStep,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicLineNumberLinearMapping mapping = sourceCode.renum(lineNumberStart, lineNumberStep);
		session.renumMacros(mapping); // keep line numbers in sync
		return mapping;
	}

	protected boolean originalCodeContainsKeyword(BasicSourceCode sourceCode, String keyword,
			StagedBasicProgramLoaderSession session) throws BasicException {
		return codeContainsKeyword(sourceCode, session.getScopeExcludingMacros(), keyword);
	}

	protected void substituteErrorCode(int errorCode, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int ln = session.getMacroAdded(ErrorOutMacro.class).getLineNumberStart();
		BasicSourceTokenSequence sequence = sourceCode.getLineByLineNumber(ln).parse();
		BasicSourceToken ERROR = createKeywordToken(sourceCode.getLanguage(), "ERROR");
		int i = sequence.getFirstIndexOf(ERROR);
		if (i >= 0) {
			sequence.replace(i + 2, new Integer8BitDecimalToken(errorCode));
			addCodeLine(sourceCode, sequence);
		}
	}

	protected void substituteGotoLineNumber(int lineNumber, int gotoLineNumber, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicSourceTokenSequence sequence = sourceCode.getLineByLineNumber(lineNumber).parse();
		BasicSourceToken GOTO = createKeywordToken(sourceCode.getLanguage(), "GOTO");
		int i = sequence.getFirstIndexOf(GOTO);
		if (i >= 0) {
			sequence.replace(i + 2, new LineNumberReferenceToken(gotoLineNumber));
			addCodeLine(sourceCode, sequence);
		}
	}

}