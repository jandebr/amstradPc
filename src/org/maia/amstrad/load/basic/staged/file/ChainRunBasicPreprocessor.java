package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class ChainRunBasicPreprocessor extends FileCommandBasicPreprocessor {

	public ChainRunBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 2; // for chainrun macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(ChainRunMacro.class)) {
			addChainRunMacro(sourceCode, session);
		}
		invokeChainRunMacro(sourceCode, session);
	}

	private void addChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln1
				+ (session.produceRemarks() ? ":REM @chainrun" : ""));
		addCodeLine(sourceCode, ln2, "END" + (session.produceRemarks() ? ":REM @chainrun" : ""));
		session.addMacro(new ChainRunMacro(new BasicLineNumberRange(ln1, ln2), addrResume));
	}

	private void invokeChainRunMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		// TODO
	}

	public static class ChainRunMacro extends FileCommandMacro {

		public ChainRunMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

}