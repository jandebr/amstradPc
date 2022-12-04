package org.maia.amstrad.program;

import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradProgramRuntime {

	private AmstradProgram program;

	private AmstradPc amstradPc;

	protected AmstradProgramRuntime(AmstradProgram program, AmstradPc amstradPc) {
		this.program = program;
		this.amstradPc = amstradPc;
	}

	public abstract void run() throws AmstradProgramException;

	public AmstradProgram getProgram() {
		return program;
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}