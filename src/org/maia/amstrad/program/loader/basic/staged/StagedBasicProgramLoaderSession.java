package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public class StagedBasicProgramLoaderSession extends AmstradProgramLoaderSession {

	public StagedBasicProgramLoaderSession(StagedBasicProgramLoader loader, AmstradProgramRuntime programRuntime) {
		super(loader, programRuntime);
	}

}