package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberRange;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicMemoryMap;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;

public class HimemBasicPreprocessor extends StagedBasicPreprocessor implements LocomotiveBasicMemoryMap {

	private int minimumReservedBytes;

	public HimemBasicPreprocessor(int minimumReservedBytes) {
		this.minimumReservedBytes = minimumReservedBytes;
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 1; // for himem macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return false;
	}

	@Override
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		return Collections.emptyList();
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (!session.hasMacrosAdded(HimemMacro.class)) {
			int n = Math.max(session.getReservedMemoryInBytes(), getMinimumReservedBytes());
			if (n > 0) {
				addHimemMacro(sourceCode, INITIAL_HIMEM - n, session);
			}
		}
	}

	private void addHimemMacro(BasicSourceCode sourceCode, int himemAddress, StagedBasicProgramLoaderSession session)
			throws BasicException {
		int ln = session.acquireSmallestAvailablePreambleLineNumber();
		addCodeLine(sourceCode, ln, "SYMBOL AFTER 256:MEMORY &" + Integer.toHexString(himemAddress)
				+ (session.produceRemarks() ? ":REM @himem" : ""));
		session.addMacro(new HimemMacro(new BasicLineNumberRange(ln)));
	}

	public int getMinimumReservedBytes() {
		return minimumReservedBytes;
	}

	public static class HimemMacro extends StagedBasicMacro {

		public HimemMacro(BasicLineNumberRange range) {
			super(range);
		}

	}

}