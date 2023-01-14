package org.maia.amstrad.program.loader.basic.staged;

import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.AmstradProgramLoaderSession;

public class StagedBasicProgramLoaderSession extends AmstradProgramLoaderSession {

	private EndingBasicAction endingAction;

	private EndingBasicCodeDisclosure codeDisclosure;

	public StagedBasicProgramLoaderSession(StagedBasicProgramLoader loader, AmstradProgramRuntime programRuntime) {
		super(loader, programRuntime);
	}

	public EndingBasicAction getEndingAction() {
		return endingAction;
	}

	public void setEndingAction(EndingBasicAction endingAction) {
		this.endingAction = endingAction;
	}

	public EndingBasicCodeDisclosure getCodeDisclosure() {
		return codeDisclosure;
	}

	public void setCodeDisclosure(EndingBasicCodeDisclosure codeDisclosure) {
		this.codeDisclosure = codeDisclosure;
	}

}