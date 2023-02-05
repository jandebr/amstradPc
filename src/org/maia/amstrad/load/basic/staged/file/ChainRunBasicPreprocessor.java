package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public abstract class ChainRunBasicPreprocessor extends StagedBasicPreprocessor {

	protected ChainRunBasicPreprocessor() {
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected final void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (!session.hasMacrosAdded(ChainRunMacro.class)) {
			addChainRunMacro(sourceCode, session);
		}
		invokeChainRunMacro(sourceCode, session);
	}

	private void addChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln + " ELSE END"
				+ (session.produceRemarks() ? ":REM @chainrun" : ""));
		session.addMacro(new ChainRunMacro(new BasicLineNumberRange(ln), addrResume));
	}

	protected abstract void invokeChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	public static class ChainRunMacro extends FileCommandMacro {

		public ChainRunMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

}