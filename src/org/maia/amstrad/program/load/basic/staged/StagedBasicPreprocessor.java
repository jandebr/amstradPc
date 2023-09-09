package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.Integer8BitDecimalToken;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.program.load.AmstradProgramLoaderSession;
import org.maia.amstrad.program.load.basic.BasicPreprocessor;

public abstract class StagedBasicPreprocessor extends BasicPreprocessor {

	protected StagedBasicPreprocessor() {
	}

	public abstract int getDesiredPreambleLineCount();

	public abstract boolean isApplicableToMergedCode();

	public abstract Collection<BasicKeywordToken> getKeywordsActedOn();

	@Override
	public final void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session)
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
		return codeContainsKeyword(sourceCode, session.getSnapshotScopeOfCodeExcludingMacros(sourceCode), keyword);
	}

	protected void substituteErrorCode(int errorCode, BasicSourceCode sourceCode,
			StagedBasicProgramLoaderSession session) throws BasicException {
		BasicSourceTokenSequence sequence = sourceCode.getLineByLineNumber(session.getErrorOutMacroLineNumber())
				.parse();
		BasicSourceToken ERROR = createKeywordToken(sourceCode.getLanguage(), "ERROR");
		int i = sequence.getFirstIndexOf(ERROR);
		if (i >= 0) {
			sequence.replace(i + 2, new Integer8BitDecimalToken(errorCode));
			addCodeLine(sourceCode, sequence);
		}
	}

	protected void substituteLineNumberReference(int lineNumber, int lineNumberReference, BasicSourceCode sourceCode)
			throws BasicException {
		BasicSourceTokenSequence sequence = sourceCode.getLineByLineNumber(lineNumber).parse();
		int i = sequence.getFirstIndexOf(LineNumberReferenceToken.class);
		if (i >= 0) {
			sequence.replace(i, new LineNumberReferenceToken(lineNumberReference));
			addCodeLine(sourceCode, sequence);
		}
	}

}