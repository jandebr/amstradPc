package org.maia.amstrad.load.basic.staged;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;

public class ErrorOutBasicPreprocessor extends StagedBasicPreprocessor {

	public ErrorOutBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 1; // for error out macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return false;
	}

	@Override
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		return Collections.emptyList();
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(ErrorOutMacro.class)) {
			addErrorOutMacro(sourceCode, session);
		}
	}

	private void addErrorOutMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln, "ERROR 255" + (session.produceRemarks() ? ":REM @error" : ""));
		session.addMacro(new ErrorOutMacro(new BasicLineNumberRange(ln)));
	}

	public static class ErrorOutMacro extends StagedBasicMacro {

		public ErrorOutMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

}