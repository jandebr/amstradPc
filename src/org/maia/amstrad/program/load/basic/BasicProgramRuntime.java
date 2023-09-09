package org.maia.amstrad.program.load.basic;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramType;
import org.maia.amstrad.program.load.AmstradProgramRuntime;

public class BasicProgramRuntime extends AmstradProgramRuntime {

	public BasicProgramRuntime(AmstradProgram program, AmstradPc amstradPc) throws AmstradProgramException {
		super(program, amstradPc);
		checkBasicProgram(program);
	}

	private void checkBasicProgram(AmstradProgram program) throws AmstradProgramException {
		if (!AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType()))
			throw new AmstradProgramException(program,
					"Program " + program.getProgramName() + " is not a Basic program");
	}

	@Override
	protected void doRun(String... args) {
		int lineNumber = extractLineNumber(args);
		if (lineNumber < 0) {
			getAmstradPc().getBasicRuntime().run();
		} else {
			getAmstradPc().getBasicRuntime().run(lineNumber);
		}
	}

	private int extractLineNumber(String... args) {
		int lineNumber = -1;
		if (args.length > 0) {
			try {
				lineNumber = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
			}
		}
		return lineNumber;
	}

}