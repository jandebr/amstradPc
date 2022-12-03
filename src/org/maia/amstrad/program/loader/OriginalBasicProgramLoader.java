package org.maia.amstrad.program.loader;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class OriginalBasicProgramLoader extends BasicProgramLoader {

	public OriginalBasicProgramLoader(BasicRuntime basicRuntime) {
		super(basicRuntime);
	}

	@Override
	protected CharSequence getSourceCodeToLoad(AmstradProgram program) throws AmstradProgramException {
		return getOriginalSourceCode(program);
	}

	@Override
	protected byte[] getByteCodeToLoad(AmstradProgram program) throws AmstradProgramException {
		return getOriginalByteCode(program);
	}

}