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
		int n = Math.max(session.getClaimedMemoryTrapAddresses(), getMinimumReservedBytes());
		if (n > 0) {
			specifyHimem(sourceCode, ADDRESS_HIMEM - n, session);
		}
	}

	protected void specifyHimem(BasicSourceCode sourceCode, int himemAddress, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.getAmstradPc().getBasicRuntime().getMinimumLineNumber();
		if (sourceCode.getSmallestLineNumber() == ln) {
			// line number already in use => renum
			int lnStep = sourceCode.getDominantLineNumberStep();
			int lnStart = lnStep * (ln / lnStep + 1);
			sourceCode.renum(lnStart, lnStep);
		}
		addCodeLine(sourceCode, ln, "MEMORY &" + Integer.toHexString(himemAddress));
	}

	protected int getMinimumReservedBytes() {
		return minimumReservedBytes;
	}

}