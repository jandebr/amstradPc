package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramCloseMenuItem extends ProgramMenuItem {

	public ProgramCloseMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Close");
	}

	@Override
	public void execute() {
		getBrowser().closeModalWindow();
	}

}