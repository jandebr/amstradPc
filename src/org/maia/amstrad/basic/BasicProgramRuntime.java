package org.maia.amstrad.basic;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class BasicProgramRuntime extends AmstradProgramRuntime {

	public BasicProgramRuntime(AmstradProgram program, AmstradPc amstradPc) {
		super(program, amstradPc);
	}

	@Override
	public void run() {
		getBasicRuntime().keyboardEnter("RUN", true);
	}

	public BasicRuntime getBasicRuntime() {
		return getAmstradPc().getBasicRuntime();
	}

}