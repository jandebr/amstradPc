package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;

public class PreambleLandingBasicPreprocessor extends StagedBasicPreprocessor {

	public PreambleLandingBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 1; // for landing macro
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