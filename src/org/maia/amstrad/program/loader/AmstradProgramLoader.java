package org.maia.amstrad.program.loader;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;

public abstract class AmstradProgramLoader {

	private AmstradPc amstradPc;

	protected AmstradProgramLoader(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	public abstract AmstradProgramRuntime load(AmstradProgram program) throws AmstradProgramException;

	protected AmstradPc getAmstradPc() {
		return amstradPc;
	}

}