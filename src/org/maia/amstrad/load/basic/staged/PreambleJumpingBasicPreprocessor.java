package org.maia.amstrad.load.basic.staged;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.load.basic.staged.PreambleLandingBasicPreprocessor.PreambleLandingMacro;

public class PreambleJumpingBasicPreprocessor extends StagedBasicPreprocessor {

	public PreambleJumpingBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 1; // for jumping macro
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
		if (!session.hasMacrosAdded(PreambleJumpingMacro.class)) {
			addPreambleJumpingMacro(sourceCode, session);
		}
	}

	private void addPreambleJumpingMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireLargestAvailablePreambleLineNumber();
		int lnGoto = session.getMacroAdded(PreambleLandingMacro.class).getLineNumberFrom();
		addCodeLine(sourceCode, ln, "GOTO " + lnGoto + (session.produceRemarks() ? ":REM @jump" : ""));
		session.addMacro(new PreambleJumpingMacro(new BasicLineNumberRange(ln)));
	}

	public static class PreambleJumpingMacro extends StagedBasicMacro {

		public PreambleJumpingMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

}