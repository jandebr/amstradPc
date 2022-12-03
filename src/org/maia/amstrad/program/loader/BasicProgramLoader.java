package org.maia.amstrad.program.loader;

import org.maia.amstrad.basic.BasicCompilationException;
import org.maia.amstrad.basic.BasicProgramRuntime;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramType;

public abstract class BasicProgramLoader implements AmstradProgramLoader {

	private BasicRuntime basicRuntime;

	protected BasicProgramLoader(BasicRuntime basicRuntime) {
		this.basicRuntime = basicRuntime;
	}

	@Override
	public BasicProgramRuntime load(AmstradProgram program) throws AmstradProgramException {
		if (!AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType()))
			throw new AmstradProgramException(program,
					"Program " + program.getProgramName() + " is not a Basic program");
		BasicProgramRuntime programRuntime = null;
		if (program.getPayload().isText()) {
			try {
				programRuntime = getBasicRuntime().loadSourceCode(getSourceCodeToLoad(program));
			} catch (BasicCompilationException e) {
				throw new AmstradProgramException(program,
						"Failed to compile source code of " + program.getProgramName(), e);
			}
		} else if (program.getPayload().isBinary()) {
			programRuntime = getBasicRuntime().loadByteCode(getByteCodeToLoad(program));
		}
		return programRuntime;
	}

	protected abstract CharSequence getSourceCodeToLoad(AmstradProgram program) throws AmstradProgramException;

	protected abstract byte[] getByteCodeToLoad(AmstradProgram program) throws AmstradProgramException;

	protected CharSequence getOriginalSourceCode(AmstradProgram program) throws AmstradProgramException {
		return program.getPayload().asTextPayload().getPayload();
	}

	protected byte[] getOriginalByteCode(AmstradProgram program) throws AmstradProgramException {
		return program.getPayload().asBinaryPayload().getPayload();
	}

	protected BasicRuntime getBasicRuntime() {
		return basicRuntime;
	}

}