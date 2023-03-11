package org.maia.amstrad.load.basic.staged;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberLinearMapping;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;

public class PreambleBasicPreprocessor extends StagedBasicPreprocessor {

	private int preambleLineCount;

	public PreambleBasicPreprocessor() {
		this(0);
	}

	public PreambleBasicPreprocessor(int preambleLineCount) {
		setPreambleLineCount(preambleLineCount);
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // no need itself
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
		if (!session.hasMacrosAdded(PreambleBlockMacro.class)) {
			BasicLineNumberScope originalCodeScope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode); // before
																												// renum
			BasicLineNumberLinearMapping renumMapping = null;
			if (getPreambleLineCount() > 0) {
				int lnLow = sourceCode.getSmallestLineNumber();
				int lnStep = 1;
				int lnOffset = (getPreambleLineCount() + 1) * lnStep;
				if (lnLow < lnOffset) {
					renumMapping = renum(sourceCode, lnOffset, lnStep, session);
				}
				addPreambleLineMacros(sourceCode, lnStep, session);
				addPreambleBlockMacro(sourceCode, lnStep, session);
			}
			initializeOriginalToStagedLineNumberMapping(sourceCode, originalCodeScope, renumMapping, session);
		}
	}

	private void addPreambleLineMacros(BasicSourceCode sourceCode, int lineNumberStep,
			StagedBasicProgramLoaderSession session) throws BasicException {
		for (int i = 0; i < getPreambleLineCount(); i++) {
			int ln = (i + 1) * lineNumberStep;
			if (session.produceRemarks()) {
				addCodeLine(sourceCode, ln, "REM @preamble");
			}
			session.addMacro(new PreambleLineMacro(new BasicLineNumberRange(ln)));
		}
	}

	private void addPreambleBlockMacro(BasicSourceCode sourceCode, int lineNumberStep,
			StagedBasicProgramLoaderSession session) {
		int lnFrom = lineNumberStep;
		int lnTo = getPreambleLineCount() * lineNumberStep;
		session.addMacro(new PreambleBlockMacro(new BasicLineNumberRange(lnFrom, lnTo)));
	}

	private void initializeOriginalToStagedLineNumberMapping(BasicSourceCode sourceCode,
			BasicLineNumberScope originalCodeScope, BasicLineNumberLinearMapping renumMapping,
			StagedBasicProgramLoaderSession session) {
		StagedLineNumberMapping stagedMapping = null;
		if (renumMapping != null) {
			stagedMapping = StagedLineNumberMapping.renumMapping(renumMapping, originalCodeScope);
		} else {
			stagedMapping = StagedLineNumberMapping.identityMapping(sourceCode, originalCodeScope);
		}
		session.setOriginalToStagedLineNumberMapping(stagedMapping);
	}

	public int getPreambleLineCount() {
		return preambleLineCount;
	}

	public void setPreambleLineCount(int preambleLineCount) {
		this.preambleLineCount = preambleLineCount;
	}

	public static class PreambleLineMacro extends StagedBasicMacro {

		public PreambleLineMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

	public static class PreambleBlockMacro extends StagedBasicMacro {

		public PreambleBlockMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

}