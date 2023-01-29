package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramReturnMenuItem extends ProgramMenuItem {

	public ProgramReturnMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Return");
	}

	@Override
	public void execute() {
		getBrowser().close();
	}

}