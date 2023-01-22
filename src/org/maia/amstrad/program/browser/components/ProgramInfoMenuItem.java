package org.maia.amstrad.program.browser.components;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramInfoMenuItem extends ProgramMenuItem {

	public ProgramInfoMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Info");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getProgramBrowser().closeModalWindow();
			getProgramBrowser().openProgramInfoModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		return getProgram().hasDescriptiveInfo();
	}

}