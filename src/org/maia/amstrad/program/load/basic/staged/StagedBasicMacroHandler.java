package org.maia.amstrad.program.load.basic.staged;

import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;

public abstract class StagedBasicMacroHandler implements AmstradMemoryTrapHandler {

	private StagedBasicMacro macro;

	private StagedBasicProgramLoaderSession session;

	private StagedCommandResolver resolver;

	protected StagedBasicMacroHandler(StagedBasicMacro macro, StagedBasicProgramLoaderSession session,
			StagedCommandResolver resolver) {
		this.macro = macro;
		this.session = session;
		this.resolver = resolver;
	}

	public StagedBasicMacro getMacro() {
		return macro;
	}

	public StagedBasicProgramLoaderSession getSession() {
		return session;
	}

	public StagedCommandResolver getResolver() {
		return resolver;
	}

}