package org.maia.amstrad.gui.browser.classic.components;

public class ProgramImagesMenuItem extends ProgramMenuItem {

	public ProgramImagesMenuItem(ProgramMenu menu) {
		super(menu, "Images");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getBrowserDisplaySource().openProgramImageGalleryModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		return !getProgram().getImages().isEmpty();
	}

}