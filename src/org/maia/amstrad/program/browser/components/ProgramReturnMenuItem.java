package org.maia.amstrad.program.browser.components;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramReturnMenuItem extends ProgramMenuItem {

	public ProgramReturnMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Return");
	}

	@Override
	public void execute() {
		getBrowser().close();
	}

}