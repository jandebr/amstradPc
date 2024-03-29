package org.maia.amstrad.program.load;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;

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

	public AmstradProgram getProgram() {
		return getProgramRuntime().getProgram();
	}

	public AmstradPc getAmstradPc() {
		return getProgramRuntime().getAmstradPc();
	}

}