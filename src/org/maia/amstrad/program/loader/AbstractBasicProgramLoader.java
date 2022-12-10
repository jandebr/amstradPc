package org.maia.amstrad.program.loader;

import org.maia.amstrad.basic.BasicCompilationException;
import org.maia.amstrad.basic.BasicProgramRuntime;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramType;

public abstract class AbstractBasicProgramLoader extends AmstradProgramLoader {

	protected AbstractBasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public BasicProgramRuntime load(AmstradProgram program) throws AmstradProgramException {
		if (!AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType()))
			throw new AmstradProgramException(program,
					"Program " + program.getProgramName() + " is not a Basic program");
		if (program.getPayload().isText()) {
			try {
				getBasicRuntime().loadSourceCode(getSourceCodeToLoad(program));
			} catch (BasicCompilationException e) {
				throw new AmstradProgramException(program,
						"Failed to compile source code of " + program.getProgramName(), e);
			}
		} else if (program.getPayload().isBinary()) {
			getBasicRuntime().loadByteCode(getByteCodeToLoad(program));
		}
		return new BasicProgramRuntime(program, getAmstradPc());
	}

	protected abstract CharSequence getSourceCodeToLoad(AmstradProgram program) throws AmstradProgramException;

	protected abstract byte[] getByteCodeToLoad(AmstradProgram program) throws AmstradProgramException;

	protected CharSequence getOriginalSourceCode(AmstradProgram program) throws AmstradProgramException {
		return program.getPayload().asTextPayload().getText();
	}

	protected byte[] getOriginalByteCode(AmstradProgram program) throws AmstradProgramException {
		return program.getPayload().asBinaryPayload().getBytes();
	}

	protected BasicRuntime getBasicRuntime() {
		return getAmstradPc().getBasicRuntime();
	}

}