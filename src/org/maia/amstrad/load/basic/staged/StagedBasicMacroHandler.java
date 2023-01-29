package org.maia.amstrad.load.basic.staged;

public abstract class StagedBasicMacroHandler {

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