package org.maia.amstrad.gui.browser.components;

public class ProgramImagesMenuItem extends ProgramMenuItem {

	public ProgramImagesMenuItem(ProgramMenu menu) {
		super(menu, "Images");
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