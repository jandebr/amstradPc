package org.maia.amstrad.program.loader.basic;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public class BasicPreprocessingProgramLoader extends BasicProgramLoader {

	private List<BasicPreprocessor> preprocessors;

	public BasicPreprocessingProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
		this.preprocessors = new Vector<BasicPreprocessor>();
	}

	public synchronized void addPreprocessor(BasicPreprocessor preprocessor) {
		getPreprocessors().add(preprocessor);
	}

	public synchronized void removePreprocessor(BasicPreprocessor preprocessor) {
		getPreprocessors().remove(preprocessor);
	}

	@Override
	protected synchronized BasicCode retrieveCode(AmstradProgram program, AmstradProgramLoaderSession session)
			throws AmstradProgramException {
		BasicSourceCode sourceCode = null;
		try {
			if (program.getPayload().isText()) {
				sourceCode = retrieveOriginalSourceCode(program);
			} else {
				sourceCode = getAmstradPc().getBasicRuntime().getDecompiler()
						.decompile(retrieveOriginalByteCode(program));
			}
			preprocess(sourceCode, session);
		} catch (BasicException e) {
			throw new AmstradProgramException(program, "Failed to extend Basic code", e);
		}
		return sourceCode;
	}

	private void preprocess(BasicSourceCode sourceCode, AmstradProgramLoaderSession session) throws BasicException {
		for (BasicPreprocessor preprocessor : getPreprocessors()) {
			preprocessor.preprocess(sourceCode, session);
		}
	}

	private List<BasicPreprocessor> getPreprocessors() {
		return preprocessors;
	}

}