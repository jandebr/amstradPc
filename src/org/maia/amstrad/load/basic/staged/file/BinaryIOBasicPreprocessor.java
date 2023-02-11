package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public abstract class BinaryIOBasicPreprocessor extends FileCommandBasicPreprocessor {

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
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln1
				+ (session.produceRemarks() ? ":REM @binaryio" : ""));
		addCodeLine(sourceCode, ln2, "RETURN" + (session.produceRemarks() ? ":REM @binaryio" : ""));
		session.addMacro(new BinaryIOMacro(new BasicLineNumberRange(ln1, ln2), addrResume));
	}

	protected abstract void invokeBinaryIOMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException;

	protected void endWithError(int errorCode, BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) {
		System.err.println("BinaryIO ended with ERROR " + errorCode);
		try {
			substituteErrorCode(errorCode, sourceCode, session);
			BinaryIOMacro macro = session.getMacroAdded(BinaryIOMacro.class);
			addCodeLine(sourceCode, macro.getLineNumberTo(), "GOTO " + session.getErrorOutMacroLineNumber());
			resumeWithNewSourceCode(sourceCode, macro, session);
		} catch (BasicException e) {
			e.printStackTrace();
		}
	}

	public static class BinaryIOMacro extends FileCommandMacro {

		public BinaryIOMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

}