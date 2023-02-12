package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class WaitResumeBasicPreprocessor extends StagedBasicPreprocessor {

	public WaitResumeBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 2; // for waitresume macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return false; // only adds global macro
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(WaitResumeMacro.class)) {
			addWaitResumeMacro(sourceCode, session);
		}
	}

	private void addWaitResumeMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln1
				+ (session.produceRemarks() ? ":REM @waitresume" : ""));
		addCodeLine(sourceCode, ln2, "RETURN" + (session.produceRemarks() ? ":REM @waitresume" : ""));
		session.addMacro(new WaitResumeMacro(new BasicLineNumberRange(ln1, ln2), addrResume));
	}

	public static class WaitResumeMacro extends FileCommandMacro {

		public WaitResumeMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

}