package org.maia.amstrad.program.load.basic.staged;

import org.maia.amstrad.program.load.AmstradProgramRuntime;
import org.maia.amstrad.program.load.AmstradProgramRuntimeListener;

public abstract class StagedBasicProgramRuntimeListener implements AmstradProgramRuntimeListener {

	private StagedBasicProgramLoaderSession session;

	protected StagedBasicProgramRuntimeListener(StagedBasicProgramLoaderSession session) {
		this.session = session;
	}

	public void install() {
		AmstradProgramRuntime rt = getSession().getProgramRuntime();
		rt.addListener(this);
		if (rt.isRun()) {
			// catching up, already running
			amstradProgramIsAboutToRun(rt);
			amstradProgramIsRun(rt);
		}
	}

	@Override
	public void amstradProgramIsRun(AmstradProgramRuntime programRuntime) {
		// no action
	}

	protected StagedBasicProgramLoaderSession getSession() {
		return session;
	}

}