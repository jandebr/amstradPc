package org.maia.amstrad.program.browser.components;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramLoadMenuItem extends ProgramLaunchMenuItem {

	public ProgramLoadMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Load");
	}

	@Override
	protected void launchProgram(AmstradProgram program) throws AmstradProgramException {
		getProgramLoader(program).load(program);
		getBrowser().notifyProgramLoaded(program);
	}

}