package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;

public class PreambleBasicPreprocessor extends StagedBasicPreprocessor {

	private int preambleLineCount;

	public PreambleBasicPreprocessor(int preambleLineCount) {
		this.preambleLineCount = preambleLineCount;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(PreambleBlockMacro.class) && getPreambleLineCount() > 0) {
			int lnLow = sourceCode.getSmallestLineNumber();
			int lnStep = sourceCode.getDominantLineNumberStep();
			int lnOffset = (getPreambleLineCount() + 1) * lnStep;
			if (lnLow < lnOffset) {
				sourceCode.renum(lnOffset, lnStep);
			}
			addMacros(sourceCode, lnStep, session);
		}
	}

	protected void addMacros(BasicSourceCode sourceCode, int lineNumberStep, StagedBasicProgramLoaderSession session)
			throws BasicException {
		addLineMacros(sourceCode, lineNumberStep, session);
		addBlockMacro(sourceCode, lineNumberStep, session);
	}

	protected void addLineMacros(BasicSourceCode sourceCode, int lineNumberStep,
			StagedBasicProgramLoaderSession session) throws BasicException {
		for (int i = 0; i < getPreambleLineCount(); i++) {
			int ln = (i + 1) * lineNumberStep;
			if (session.leaveRemarks()) {
				addCodeLine(sourceCode, ln, "REM @preamble");
			}
			session.addMacro(new PreambleLineMacro(ln));
		}
	}

	protected void addBlockMacro(BasicSourceCode sourceCode, int lineNumberStep,
			StagedBasicProgramLoaderSession session) {
		int lnStart = lineNumberStep;
		int lnEnd = getPreambleLineCount() * lineNumberStep;
		session.addMacro(new PreambleBlockMacro(lnStart, lnEnd));
	}

	protected int getPreambleLineCount() {
		return preambleLineCount;
	}

	public static class PreambleBlockMacro extends StagedBasicMacro {

		public PreambleBlockMacro(int lineNumberStart, int lineNumberEnd) {
			super(lineNumberStart, lineNumberEnd);
		}

	}

	public static class PreambleLineMacro extends StagedBasicMacro {

		public PreambleLineMacro(int lineNumber) {
			super(lineNumber, lineNumber);
		}

	}

}