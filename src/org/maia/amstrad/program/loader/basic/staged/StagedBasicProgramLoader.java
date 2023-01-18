package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.basic.BasicPreprocessingProgramLoader;

public class StagedBasicProgramLoader extends BasicPreprocessingProgramLoader {

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	private boolean leaveRemarks;

	public StagedBasicProgramLoader(AmstradPc amstradPc, EndingBasicAction endingAction,
			EndingBasicCodeDisclosure codeDisclosure, boolean leaveRemarks) {
		super(amstradPc);
		this.endingAction = endingAction;
		this.codeDisclosure = codeDisclosure;
		this.leaveRemarks = leaveRemarks;
		setupPreprocessors();
	}

	protected void setupPreprocessors() {
		addPreprocessor(new PreambleBasicPreprocessor(2)); // must come first
		addPreprocessor(new EndingBasicPreprocessor());
		addPreprocessor(new HimemBasicPreprocessor(16)); // must come last
	}

	@Override
	protected StagedBasicProgramLoaderSession createLoaderSession(AmstradProgramRuntime programRuntime) {
		StagedBasicProgramLoaderSession session = new StagedBasicProgramLoaderSession(this, programRuntime);
		session.setEndingAction(getEndingAction());
		session.setCodeDisclosure(getCodeDisclosure());
		session.setLeaveRemarks(leaveRemarks());
		return session;
	}

	private EndingBasicAction getEndingAction() {
		return endingAction;
	}

	private EndingBasicCodeDisclosure getCodeDisclosure() {
		return codeDisclosure;
	}

	private boolean leaveRemarks() {
		return leaveRemarks;
	}

}