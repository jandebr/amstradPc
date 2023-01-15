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
		if (!session.isMacroAdded(HimemMacro.class)) {
			int n = Math.max(session.getReservedMemoryInBytes(), getMinimumReservedBytes());
			if (n > 0) {
				addMacro(sourceCode, ADDRESS_HIMEM - n, session);
			}
		}
	}

	protected void addMacro(BasicSourceCode sourceCode, int himemAddress, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.getAmstradPc().getBasicRuntime().getMinimumLineNumber();
		if (sourceCode.getSmallestLineNumber() == ln) {
			// line number already in use => renum
			int lnStep = sourceCode.getDominantLineNumberStep();
			int lnStart = lnStep * (ln / lnStep + 1);
			sourceCode.renum(lnStart, lnStep);
		}
		addCodeLine(sourceCode, ln, "MEMORY &" + Integer.toHexString(himemAddress) + ": REM @Himem");
		session.addMacro(new HimemMacro(ln));
	}

	protected int getMinimumReservedBytes() {
		return minimumReservedBytes;
	}

	public static class HimemMacro extends StagedBasicMacro {

		public HimemMacro(int lineNumber) {
			super(lineNumber, lineNumber);
		}

	}

}