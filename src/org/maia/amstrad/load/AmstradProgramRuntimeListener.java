package org.maia.amstrad.load;

public interface AmstradProgramRuntimeListener {

	void amstradProgramIsAboutToRun(AmstradProgramRuntime programRuntime);

	void amstradProgramIsRun(AmstradProgramRuntime programRuntime);

	void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded);

}