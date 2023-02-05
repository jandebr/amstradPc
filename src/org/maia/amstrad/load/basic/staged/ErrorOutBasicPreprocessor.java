package org.maia.amstrad.load.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;

public class ErrorOutBasicPreprocessor extends StagedBasicPreprocessor {

	public ErrorOutBasicPreprocessor() {
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 1; // for error out macro
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(ErrorOutMacro.class)) {
			addErrorOutMacro(sourceCode, session);
		}
	}

	private void addErrorOutMacro(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireLargestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln, "ERROR 255" + (session.produceRemarks() ? ":REM @error" : ""));
		session.addMacro(new ErrorOutMacro(new BasicLineNumberRange(ln)));
	}

	public static class ErrorOutMacro extends StagedBasicMacro {

		public ErrorOutMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

}