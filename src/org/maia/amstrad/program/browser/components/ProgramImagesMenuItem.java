package org.maia.amstrad.program.browser.components;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramImagesMenuItem extends ProgramMenuItem {

	public ProgramImagesMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Images");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getProgramBrowser().closeModalWindow();
			getProgramBrowser().openProgramImageGalleryModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		return !getProgram().getImages().isEmpty();
	}

}