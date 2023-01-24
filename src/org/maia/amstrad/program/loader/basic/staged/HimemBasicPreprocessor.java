package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;

public class HimemBasicPreprocessor extends StagedBasicPreprocessor implements LocomotiveBasicMemoryMap {

	private int minimumReservedBytes;

	public HimemBasicPreprocessor(int minimumReservedBytes) {
		this.minimumReservedBytes = minimumReservedBytes;
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(HimemMacro.class)) {
			int n = Math.max(session.getReservedMemoryInBytes(), getMinimumReservedBytes());
			if (n > 0) {
				addMacro(sourceCode, ADDRESS_HIMEM - n, session);
			}
		}
	}

	@Override
	protected int getDesiredPreambleLineCount() {
		return 1; // for himem macro
	}

	protected void addMacro(BasicSourceCode sourceCode, int himemAddress, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireFirstAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln, "SYMBOL AFTER 256:MEMORY &" + Integer.toHexString(himemAddress)
				+ (session.produceRemarks() ? ":REM @himem" : ""));
		session.addMacro(new HimemMacro(ln));
	}

	public int getMinimumReservedBytes() {
		return minimumReservedBytes;
	}

	public static class HimemMacro extends StagedBasicMacro {

		public HimemMacro(int lineNumber) {
			super(lineNumber, lineNumber);
		}

	}

}