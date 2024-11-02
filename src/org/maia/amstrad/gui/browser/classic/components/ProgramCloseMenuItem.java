package org.maia.amstrad.gui.browser.classic.components;

public class ProgramCloseMenuItem extends ProgramMenuItem {

	public ProgramCloseMenuItem(ProgramMenu menu) {
		super(menu, "Close");
	}

	@Override
	public void execute() {
		getBrowserDisplaySource().closeModalWindow();
	}

}