package org.maia.amstrad.gui.browser.classic.components;

import org.maia.amstrad.gui.browser.classic.ClassicProgramBrowserDisplaySource;
import org.maia.amstrad.gui.components.ScrollableItem;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;

public abstract class ProgramMenuItem implements ScrollableItem {

	private ProgramMenu menu;

	private String label;

	protected ProgramMenuItem(ProgramMenu menu, String label) {
		this.menu = menu;
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

	protected AmstradPc getAmstradPc() {
		return getBrowserDisplaySource().getAmstradPc();
	}

	protected ClassicProgramBrowserDisplaySource getBrowserDisplaySource() {
		return getMenu().getBrowserDisplaySource();
	}

	public AmstradProgram getProgram() {
		return getMenu().getProgram();
	}

	public ProgramMenu getMenu() {
		return menu;
	}

	public String getLabel() {
		return label;
	}

}