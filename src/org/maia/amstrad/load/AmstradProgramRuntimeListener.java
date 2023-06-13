package org.maia.amstrad.load;

import org.maia.amstrad.util.AmstradListener;

public interface AmstradProgramRuntimeListener extends AmstradListener {

	void amstradProgramIsAboutToRun(AmstradProgramRuntime programRuntime);

	void amstradProgramIsRun(AmstradProgramRuntime programRuntime);

	void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded);

}