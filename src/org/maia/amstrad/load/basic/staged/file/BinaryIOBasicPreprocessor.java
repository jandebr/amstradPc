package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicPreprocessor;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public abstract class BinaryIOBasicPreprocessor extends StagedBasicPreprocessor {

	protected BinaryIOBasicPreprocessor() {
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected final void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (!session.hasMacrosAdded(BinaryIOMacro.class)) {
			addBinaryIOMacro(sourceCode, session);
		}
		invokeBinaryIOMacro(sourceCode, session);
	}

	private void addBinaryIOMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln + " ELSE RETURN"
				+ (session.produceRemarks() ? ":REM @binaryio" : ""));
		session.addMacro(new BinaryIOMacro(new BasicLineNumberRange(ln), addrResume));
	}

	protected abstract void invokeBinaryIOMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	public static class BinaryIOMacro extends FileCommandMacro {

		public BinaryIOMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

}