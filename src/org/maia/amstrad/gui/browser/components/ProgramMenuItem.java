package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;

public abstract class ProgramMenuItem {

	private final ProgramBrowserDisplaySource browser;

	private AmstradProgram program;

	private String label;

	protected ProgramMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program, String label) {
		this.browser = browser;
		this.program = program;
		this.label = label;
	}

	public abstract void execute();

	public boolean isEnabled() {
		return true;
	}
	
	public int getLabelColor() {
		return 22;
	}
	
	public int getFocusBackgroundColor() {
		return 9;
	}

	protected ProgramBrowserDisplaySource getBrowser() {
		return browser;
	}

	protected AmstradPc getAmstradPc() {
		return getBrowser().getAmstradPc();
	}

	public AmstradProgram getProgram() {
		return program;
	}

	public String getLabel() {
		return label;
	}

}