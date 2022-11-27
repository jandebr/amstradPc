package org.maia.amstrad.program.loader;

import org.maia.amstrad.basic.BasicCompilationException;
import org.maia.amstrad.basic.BasicProgramRuntime;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramPayload;
import org.maia.amstrad.program.AmstradProgramType;

public class BasicProgramLoader implements AmstradProgramLoader {

	private BasicRuntime basicRuntime;

	public BasicProgramLoader(BasicRuntime basicRuntime) {
		this.basicRuntime = basicRuntime;
	}

	@Override
	public BasicProgramRuntime load(AmstradProgram program) throws AmstradProgramException {
		if (!AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType()))
			throw new IllegalArgumentException("This loader only supports Basic programs");
		BasicProgramRuntime programRuntime = null;
		AmstradProgramPayload payload = program.getPayload();
		if (payload.isText()) {
			try {
				programRuntime = getBasicRuntime().loadSourceCode(payload.asTextPayload().getPayload());
			} catch (BasicCompilationException e) {
				throw new AmstradProgramException(program,
						"Failed to compile source code of " + program.getProgramName(), e);
			}
		} else if (payload.isBinary()) {
			programRuntime = getBasicRuntime().loadByteCode(payload.asBinaryPayload().getPayload());
		}
		return programRuntime;
	}

	public BasicRuntime getBasicRuntime() {
		return basicRuntime;
	}

}