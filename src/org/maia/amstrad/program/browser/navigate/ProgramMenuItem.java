package org.maia.amstrad.program.browser.navigate;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public abstract class ProgramMenuItem {

	private final ProgramBrowserDisplaySource programBrowser;

	private AmstradProgram program;

	private String label;

	protected ProgramMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program, String label) {
		this.programBrowser = browser;
		this.program = program;
		this.label = label;
	}

	public abstract void execute();

	public boolean isEnabled() {
		return true;
	}

	protected ProgramBrowserDisplaySource getProgramBrowser() {
		return programBrowser;
	}

	public AmstradProgram getProgram() {
		return program;
	}

	public String getLabel() {
		return label;
	}

}