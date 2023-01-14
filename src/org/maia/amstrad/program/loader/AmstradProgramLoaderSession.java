package org.maia.amstrad.program.loader;

import org.maia.amstrad.program.AmstradProgramRuntime;

public class AmstradProgramLoaderSession {

	private AmstradProgramLoader loader;

	private AmstradProgramRuntime programRuntime;

	public AmstradProgramLoaderSession(AmstradProgramLoader loader, AmstradProgramRuntime programRuntime) {
		this.loader = loader;
		this.programRuntime = programRuntime;
	}

	public AmstradProgramLoader getLoader() {
		return loader;
	}

	public AmstradProgramRuntime getProgramRuntime() {
		return programRuntime;
	}

}