package org.maia.amstrad.program.loader;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class OriginalBasicProgramLoader extends AbstractBasicProgramLoader {

	public OriginalBasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
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