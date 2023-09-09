package org.maia.amstrad.program.load.basic.staged;

import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;

public abstract class StagedBasicMacroHandler implements AmstradMemoryTrapHandler {

	private StagedBasicMacro macro;

	private StagedBasicProgramLoaderSession session;

	protected StagedBasicMacroHandler(StagedBasicMacro macro, StagedBasicProgramLoaderSession session) {
		this.macro = macro;
		this.session = session;
	}

	public StagedBasicMacro getMacro() {
		return macro;
	}

	public StagedBasicProgramLoaderSession getSession() {
		return session;
	}

}