package org.maia.amstrad.program.browser.components;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramCloseMenuItem extends ProgramMenuItem {

	public ProgramCloseMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Close");
	}

	@Override
	public void execute() {
		getProgramBrowser().closeModalWindow();
	}

}