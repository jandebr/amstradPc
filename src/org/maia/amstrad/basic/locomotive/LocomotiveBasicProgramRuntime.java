package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class LocomotiveBasicProgramRuntime extends AmstradProgramRuntime {

	public LocomotiveBasicProgramRuntime(AmstradProgram program, AmstradPc amstradPc) {
		super(program, amstradPc);
	}

	@Override
	protected void doRun() {
		LocomotiveBasicRuntime rt = (LocomotiveBasicRuntime) getAmstradPc().getBasicRuntime();
		rt.run();
	}

}