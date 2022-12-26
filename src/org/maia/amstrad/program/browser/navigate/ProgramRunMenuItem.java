package org.maia.amstrad.program.browser.navigate;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramRunMenuItem extends ProgramLaunchMenuItem {

	public ProgramRunMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Run");
	}

	@Override
	protected void launchProgram() throws AmstradProgramException {
		AmstradProgram program = getProgram();
		getProgramLoader().load(program).run();
		getProgramBrowser().notifyProgramRun(program);
	}

}