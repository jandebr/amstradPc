package org.maia.amstrad.program.browser.navigate;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramLoadMenuItem extends ProgramLaunchMenuItem {

	public ProgramLoadMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Load");
	}

	@Override
	protected void launchProgram() throws AmstradProgramException {
		AmstradProgram program = getProgram();
		getProgramLoader().load(program);
		getProgramBrowser().notifyProgramLoaded(program);
	}

}