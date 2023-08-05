package org.maia.amstrad.gui.browser.components;

public class ProgramInfoMenuItem extends ProgramMenuItem {

	public ProgramInfoMenuItem(ProgramMenu menu) {
		super(menu, "Info");
	}

	@Override
	public void execute() {
		if (isEnabled()) {
			getBrowser().openProgramInfoModalWindow(getProgram());
		}
	}

	@Override
	public boolean isEnabled() {
		return getProgram().hasDescriptiveInfo();
	}

}