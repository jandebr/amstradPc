package org.maia.amstrad.program.loader;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class OriginalBasicProgramLoader extends AbstractBasicProgramLoader {

	public OriginalBasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected BasicSourceCode getSourceCodeToLoad(AmstradProgram program)
			throws AmstradProgramException, BasicException {
		return getOriginalSourceCode(program);
	}

	@Override
	protected BasicByteCode getByteCodeToLoad(AmstradProgram program) throws AmstradProgramException {
		return getOriginalByteCode(program);
	}

}