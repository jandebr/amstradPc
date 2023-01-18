package org.maia.amstrad.program.loader.basic;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramType;
import org.maia.amstrad.program.loader.AmstradProgramLoader;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public class BasicProgramLoader extends AmstradProgramLoader {

	public BasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected AmstradProgramRuntime createProgramRuntime(AmstradProgram program) throws AmstradProgramException {
		if (!AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType()))
			throw new AmstradProgramException(program,
					"Program " + program.getProgramName() + " is not a Basic program");
		return new BasicProgramRuntime(program, getAmstradPc());
	}

	@Override
	protected void loadProgramIntoAmstradPc(AmstradProgram program, AmstradProgramLoaderSession session)
			throws AmstradProgramException {
		BasicCode code = retrieveCode(program, session);
		try {
			getAmstradPc().getBasicRuntime().load(code);
		} catch (BasicException e) {
			throw new AmstradProgramException(program, "Failed to load Basic program", e);
		}
	}

	protected BasicCode retrieveCode(AmstradProgram program, AmstradProgramLoaderSession session)
			throws AmstradProgramException {
		if (program.getPayload().isText()) {
			return retrieveOriginalSourceCode(program);
		} else {
			return retrieveOriginalByteCode(program);
		}
	}

	protected BasicSourceCode retrieveOriginalSourceCode(AmstradProgram program) throws AmstradProgramException {
		try {
			CharSequence code = program.getPayload().asTextPayload().getText();
			return BasicLanguageKit.forLanguage(BasicLanguageKit.guessLanguageOfSourceCode(code)).parseSourceCode(code);
		} catch (BasicException e) {
			throw new AmstradProgramException(program, "Failed to retrieve Basic source code", e);
		}
	}

	protected BasicByteCode retrieveOriginalByteCode(AmstradProgram program) throws AmstradProgramException {
		try {
			byte[] code = program.getPayload().asBinaryPayload().getBytes();
			return BasicLanguageKit.forLanguage(BasicLanguageKit.guessLanguageOfByteCode(code)).parseByteCode(code);
		} catch (BasicException e) {
			throw new AmstradProgramException(program, "Failed to retrieve Basic byte code", e);
		}
	}

	private static class BasicProgramRuntime extends AmstradProgramRuntime {

		public BasicProgramRuntime(AmstradProgram program, AmstradPc amstradPc) {
			super(program, amstradPc);
		}

		@Override
		protected void doRun() {
			getAmstradPc().getBasicRuntime().run();
		}

	}

}