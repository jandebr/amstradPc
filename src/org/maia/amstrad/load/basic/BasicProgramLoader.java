package org.maia.amstrad.load.basic;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.load.AmstradProgramLoader;
import org.maia.amstrad.load.AmstradProgramLoaderSession;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class BasicProgramLoader extends AmstradProgramLoader {

	public BasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected AmstradProgramRuntime createProgramRuntime(AmstradProgram program) throws AmstradProgramException {
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

	public BasicSourceCode retrieveSourceCode(AmstradProgram program) throws AmstradProgramException {
		BasicSourceCode sourceCode = null;
		try {
			if (program.getPayload().isText()) {
				sourceCode = retrieveOriginalSourceCode(program);
			} else {
				sourceCode = getAmstradPc().getBasicRuntime().getDecompiler()
						.decompile(retrieveOriginalByteCode(program));
			}
		} catch (BasicException e) {
			throw new AmstradProgramException(program, "Failed to retrieve Basic source code", e);
		}
		return sourceCode;
	}

}