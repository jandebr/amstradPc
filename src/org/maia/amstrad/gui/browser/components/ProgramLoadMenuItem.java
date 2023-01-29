package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

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