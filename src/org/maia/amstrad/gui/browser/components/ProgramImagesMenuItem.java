package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramImagesMenuItem extends ProgramMenuItem {

	public ProgramImagesMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Images");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getBrowser().closeModalWindow();
			getBrowser().openProgramImageGalleryModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		return !getProgram().getImages().isEmpty();
	}

}