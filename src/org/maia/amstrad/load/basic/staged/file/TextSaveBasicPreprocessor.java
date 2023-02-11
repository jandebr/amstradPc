package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class TextSaveBasicPreprocessor extends FileCommandBasicPreprocessor {

	public TextSaveBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 2; // for textsave macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected final void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		if (!session.hasMacrosAdded(TextSaveMacro.class)) {
			addTextSaveMacro(sourceCode, session);
		}
		invokeTextSaveMacro(sourceCode, session);
	}

	private void addTextSaveMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln1
				+ (session.produceRemarks() ? ":REM @textsave" : ""));
		addCodeLine(sourceCode, ln2, "RETURN" + (session.produceRemarks() ? ":REM @textsave" : ""));
		session.addMacro(new TextSaveMacro(new BasicLineNumberRange(ln1, ln2), addrResume));
	}

	private void invokeTextSaveMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		// TODO
	}

	public static class TextSaveMacro extends FileCommandMacro {

		public TextSaveMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

}