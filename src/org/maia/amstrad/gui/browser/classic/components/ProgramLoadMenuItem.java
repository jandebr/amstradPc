package org.maia.amstrad.gui.browser.classic.components;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class ProgramLoadMenuItem extends ProgramLaunchMenuItem {

	public ProgramLoadMenuItem(ProgramMenu menu) {
		super(menu, "Load");
	}

	@Override
	protected void launchProgram() throws AmstradProgramException {
		AmstradProgram program = getProgram();
		getProgramLoader().load(program);
		getBrowserDisplaySource().getProgramBrowser().fireProgramLoaded(program);
	}

}