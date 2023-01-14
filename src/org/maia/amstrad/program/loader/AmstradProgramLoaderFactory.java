package org.maia.amstrad.program.loader;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramType;
import org.maia.amstrad.program.loader.basic.BasicProgramLoader;
import org.maia.amstrad.program.loader.basic.staged.EndingBasicAction;
import org.maia.amstrad.program.loader.basic.staged.EndingBasicCodeDisclosure;
import org.maia.amstrad.program.loader.basic.staged.StagedBasicProgramLoader;
import org.maia.amstrad.program.loader.snapshot.AmstradPcSnapshotLoader;

public class AmstradProgramLoaderFactory {

	private static AmstradProgramLoaderFactory instance;

	private AmstradProgramLoaderFactory() {
	}

	public AmstradProgramLoader createLoaderFor(AmstradProgram program, AmstradPc amstradPc) {
		AmstradProgramLoader loader = null;
		if (AmstradProgramType.CPC_SNAPSHOT.equals(program.getProgramType())) {
			loader = createAmstradPcSnapshotLoader(amstradPc);
		} else if (AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType())) {
			loader = createOriginalBasicProgramLoader(amstradPc);
		}
		return loader;
	}

	public AmstradPcSnapshotLoader createAmstradPcSnapshotLoader(AmstradPc amstradPc) {
		return new AmstradPcSnapshotLoader(amstradPc);
	}

	public BasicProgramLoader createOriginalBasicProgramLoader(AmstradPc amstradPc) {
		return new BasicProgramLoader(amstradPc);
	}

	public StagedBasicProgramLoader createStagedBasicProgramLoader(AmstradPc amstradPc) {
		return createStagedBasicProgramLoader(amstradPc, null);
	}

	public StagedBasicProgramLoader createStagedBasicProgramLoader(AmstradPc amstradPc,
			EndingBasicAction endingAction) {
		return createStagedBasicProgramLoader(amstradPc, endingAction, EndingBasicCodeDisclosure.HIDE_CODE);
	}

	public StagedBasicProgramLoader createStagedBasicProgramLoader(AmstradPc amstradPc, EndingBasicAction endingAction,
			EndingBasicCodeDisclosure codeDisclosure) {
		return new StagedBasicProgramLoader(amstradPc, endingAction, codeDisclosure);
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