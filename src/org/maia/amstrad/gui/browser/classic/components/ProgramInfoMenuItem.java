package org.maia.amstrad.gui.browser.classic.components;

public class ProgramInfoMenuItem extends ProgramMenuItem {

	public ProgramInfoMenuItem(ProgramMenu menu) {
		super(menu, "Info");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getBrowserDisplaySource().openProgramInfoModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		return getProgram().hasDescriptiveInfo();
	}

}