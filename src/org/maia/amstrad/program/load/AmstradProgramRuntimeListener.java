package org.maia.amstrad.program.load;

import org.maia.util.GenericListener;

public interface AmstradProgramRuntimeListener extends GenericListener {

	void amstradProgramIsAboutToRun(AmstradProgramRuntime programRuntime);

	void amstradProgramIsRun(AmstradProgramRuntime programRuntime);

	void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded);

}