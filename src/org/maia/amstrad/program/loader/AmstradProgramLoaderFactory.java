package org.maia.amstrad.program.loader;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramType;

public class AmstradProgramLoaderFactory {

	private static AmstradProgramLoaderFactory instance;

	private AmstradProgramLoaderFactory() {
	}

	public AmstradProgramLoader createLoaderFor(AmstradProgram program, AmstradPc amstradPc) {
		AmstradProgramLoader loader = null;
		if (AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType())) {
			loader = new OriginalBasicProgramLoader(amstradPc.getBasicRuntime());
		} else if (AmstradProgramType.CPC_SNAPSHOT.equals(program.getProgramType())) {
			loader = new AmstradPcSnapshotLoader(amstradPc);
		}
		return loader;
	}

	public static AmstradProgramLoaderFactory getInstance() {
		if (instance == null) {
			setInstance(new AmstradProgramLoaderFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(AmstradProgramLoaderFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

}