package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public class TextLoadBasicPreprocessor extends FileCommandBasicPreprocessor {

	public TextLoadBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 2; // for textload macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(TextLoadMacro.class)) {
			addTextLoadMacro(sourceCode, session);
		}
		invokeTextLoadMacro(sourceCode, session);
	}

	private void addTextLoadMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int addrResume = session.reserveMemory(1);
		int ln2 = session.acquireLargestAvailablePreambleLineNumber();
		int ln1 = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln1, "IF PEEK(&" + Integer.toHexString(addrResume) + ")=0 GOTO " + ln1
				+ (session.produceRemarks() ? ":REM @textload" : ""));
		addCodeLine(sourceCode, ln2, "RETURN" + (session.produceRemarks() ? ":REM @textload" : ""));
		session.addMacro(new TextLoadMacro(new BasicLineNumberRange(ln1, ln2), addrResume));
	}

	private void invokeTextLoadMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		// TODO
	}

	public static class TextLoadMacro extends FileCommandMacro {

		public TextLoadMacro(BasicLineNumberRange range, int resumeMemoryAddress) {
			super(range, resumeMemoryAddress);
		}

	}

}