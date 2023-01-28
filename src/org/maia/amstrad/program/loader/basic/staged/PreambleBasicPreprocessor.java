package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;

public class PreambleBasicPreprocessor extends StagedBasicPreprocessor {

	private int preambleLineCount;

	public PreambleBasicPreprocessor() {
		this(0);
	}

	public PreambleBasicPreprocessor(int preambleLineCount) {
		setPreambleLineCount(preambleLineCount);
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 0; // no need itself
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(PreambleBlockMacro.class)) {
			BasicLineNumberLinearMapping mapping = null;
			if (getPreambleLineCount() > 0) {
				int lnLow = sourceCode.getSmallestLineNumber();
				int lnStep = sourceCode.getDominantLineNumberStep();
				int lnOffset = (getPreambleLineCount() + 1) * lnStep;
				if (lnLow < lnOffset) {
					mapping = renum(sourceCode, lnOffset, lnStep, session);
				}
				addPreambleLineMacros(sourceCode, lnStep, session);
				addPreambleBlockMacro(sourceCode, lnStep, session);
			}
			initializeOriginalToStagedLineNumberMapping(sourceCode, mapping, session);
		}
	}

	private void addPreambleLineMacros(BasicSourceCode sourceCode, int lineNumberStep,
			StagedBasicProgramLoaderSession session) throws BasicException {
		for (int i = 0; i < getPreambleLineCount(); i++) {
			int ln = (i + 1) * lineNumberStep;
			if (session.produceRemarks()) {
				addCodeLine(sourceCode, ln, "REM @preamble");
			}
			session.addMacro(new PreambleLineMacro(ln));
		}
	}

	private void addPreambleBlockMacro(BasicSourceCode sourceCode, int lineNumberStep,
			StagedBasicProgramLoaderSession session) {
		int lnStart = lineNumberStep;
		int lnEnd = getPreambleLineCount() * lineNumberStep;
		session.addMacro(new PreambleBlockMacro(lnStart, lnEnd));
	}

	private void initializeOriginalToStagedLineNumberMapping(BasicSourceCode sourceCode,
			BasicLineNumberLinearMapping renumMapping, StagedBasicProgramLoaderSession session) {
		StagedLineNumberMapping mapping = null;
		BasicLineNumberScope scope = session.getScopeExcludingMacros();
		if (renumMapping != null) {
			mapping = StagedLineNumberMapping.renumMapping(renumMapping, scope);
		} else {
			mapping = StagedLineNumberMapping.identityMapping(sourceCode, scope);
		}
		session.setOriginalToStagedLineNumberMapping(mapping);
	}

	public int getPreambleLineCount() {
		return preambleLineCount;
	}

	public void setPreambleLineCount(int preambleLineCount) {
		this.preambleLineCount = preambleLineCount;
	}

	public static class PreambleLineMacro extends StagedBasicMacro {

		public PreambleLineMacro(int lineNumber) {
			super(lineNumber);
		}

	}

	public static class PreambleBlockMacro extends StagedBasicMacro {

		public PreambleBlockMacro(int lineNumberStart, int lineNumberEnd) {
			super(lineNumberStart, lineNumberEnd);
		}

	}

}