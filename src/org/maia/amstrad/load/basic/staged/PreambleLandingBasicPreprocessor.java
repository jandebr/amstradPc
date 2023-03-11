package org.maia.amstrad.load.basic.staged;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;

public class PreambleLandingBasicPreprocessor extends StagedBasicPreprocessor {

	public PreambleLandingBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 1; // for landing macro
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
		if (!session.hasMacrosAdded(PreambleLandingMacro.class)) {
			addPreambleLandingMacro(sourceCode, session);
		}
	}

	private void addPreambleLandingMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln, session.produceRemarks() ? "REM @land" : "'");
		session.addMacro(new PreambleLandingMacro(new BasicLineNumberRange(ln)));
	}

	public static class PreambleLandingMacro extends StagedBasicMacro {

		public PreambleLandingMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

}