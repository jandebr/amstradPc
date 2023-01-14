package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.basic.BasicPreprocessedProgramLoader;

public class StagedBasicProgramLoader extends BasicPreprocessedProgramLoader {

	public StagedBasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
		setupPreprocessors();
	}

	protected void setupPreprocessors() {
		addPreprocessor(new EndingBasicPreprocessor());
	}

	@Override
	protected StagedBasicProgramLoaderSession createLoaderSession(AmstradProgramRuntime programRuntime) {
		return new StagedBasicProgramLoaderSession(this, programRuntime);
	}

}