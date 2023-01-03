package org.maia.amstrad.program.loader;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicProgramRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramType;

public abstract class AbstractBasicProgramLoader extends AmstradProgramLoader {

	protected AbstractBasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected AmstradProgramRuntime doLoad(AmstradProgram program) throws AmstradProgramException {
		if (!AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType()))
			throw new AmstradProgramException(program,
					"Program " + program.getProgramName() + " is not a Basic program");
		try {
			if (program.getPayload().isText()) {
				getBasicRuntime().load(getSourceCodeToLoad(program));
			} else {
				getBasicRuntime().load(getByteCodeToLoad(program));
			}
		} catch (BasicException e) {
			throw new AmstradProgramException(program, e);
		}
		return new LocomotiveBasicProgramRuntime(program, getAmstradPc());
	}

	protected abstract BasicSourceCode getSourceCodeToLoad(AmstradProgram program)
			throws AmstradProgramException, BasicException;

	protected abstract BasicByteCode getByteCodeToLoad(AmstradProgram program)
			throws AmstradProgramException, BasicException;

	protected final BasicSourceCode getOriginalSourceCode(AmstradProgram program)
			throws AmstradProgramException, BasicException {
		return new LocomotiveBasicSourceCode(program.getPayload().asTextPayload().getText());
	}

	protected final BasicByteCode getOriginalByteCode(AmstradProgram program) throws AmstradProgramException {
		return new LocomotiveBasicByteCode(program.getPayload().asBinaryPayload().getBytes());
	}

	protected BasicRuntime getBasicRuntime() {
		return getAmstradPc().getBasicRuntime();
	}

}