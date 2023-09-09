package org.maia.amstrad.program.load.basic.staged;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.load.basic.BasicProgramRuntime;

public class StagedBasicProgramRuntime extends BasicProgramRuntime {

	public static final String RUN_ARG_CHAINRUN = "CHAINRUN";

	public StagedBasicProgramRuntime(AmstradProgram program, AmstradPc amstradPc) throws AmstradProgramException {
		super(program, amstradPc);
	}

	@Override
	protected void doRun(String... args) {
		if (args.length > 0 && args[0].equals(RUN_ARG_CHAINRUN)) {
			// no run actions for CHAIN "" or RUN "" as they resume an already running program
			// see ChainRunBasicPreprocessor
		} else {
			super.doRun(args);
		}
	}

}