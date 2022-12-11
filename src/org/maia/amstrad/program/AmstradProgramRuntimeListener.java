package org.maia.amstrad.program;

public interface AmstradProgramRuntimeListener {

	void amstradProgramIsRun(AmstradProgramRuntime programRuntime);

	void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded);

}