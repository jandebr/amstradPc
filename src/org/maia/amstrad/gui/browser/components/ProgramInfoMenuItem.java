package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramInfoMenuItem extends ProgramMenuItem {

	public ProgramInfoMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Info");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getBrowser().closeModalWindow();
			getBrowser().openProgramInfoModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		return getProgram().hasDescriptiveInfo();
	}

}