package org.maia.amstrad.program.loader.basic.staged;

public abstract class StagedBasicMacroHandler {

	private StagedBasicMacro macro;

	private StagedBasicProgramLoaderSession session;

	protected StagedBasicMacroHandler(StagedBasicMacro macro, StagedBasicProgramLoaderSession session) {
		this.macro = macro;
		this.session = session;
	}

	protected StagedBasicMacro getMacro() {
		return macro;
	}

	protected StagedBasicProgramLoaderSession getSession() {
		return session;
	}

}