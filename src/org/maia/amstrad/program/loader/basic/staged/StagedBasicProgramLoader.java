package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.basic.BasicPreprocessedProgramLoader;

public class StagedBasicProgramLoader extends BasicPreprocessedProgramLoader {

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	public StagedBasicProgramLoader(AmstradPc amstradPc, EndingBasicAction endingAction,
			EndingBasicCodeDisclosure codeDisclosure) {
		super(amstradPc);
		this.endingAction = endingAction;
		this.codeDisclosure = codeDisclosure;
		setupPreprocessors();
	}

	protected void setupPreprocessors() {
		addPreprocessor(new EndingBasicPreprocessor());
	}

	@Override
	protected StagedBasicProgramLoaderSession createLoaderSession(AmstradProgramRuntime programRuntime) {
		StagedBasicProgramLoaderSession session = new StagedBasicProgramLoaderSession(this, programRuntime);
		session.setEndingAction(getEndingAction());
		session.setCodeDisclosure(getCodeDisclosure());
		return session;
	}

	private EndingBasicAction getEndingAction() {
		return endingAction;
	}

	private EndingBasicCodeDisclosure getCodeDisclosure() {
		return codeDisclosure;
	}

}