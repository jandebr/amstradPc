package org.maia.amstrad.gui.browser.classic.components;

import org.maia.amstrad.gui.components.ColoredTextArea;
import org.maia.amstrad.program.AmstradProgram;

public abstract class ProgramSheet extends ColoredTextArea {

	private AmstradProgram program;

	protected ProgramSheet(AmstradProgram program, int maxItemsShowing, int maxWidth, int backgroundColorIndex) {
		super(maxItemsShowing);
		this.program = program;
		populateSheet(maxWidth, backgroundColorIndex);
	}

	protected abstract void populateSheet(int maxWidth, int backgroundColorIndex);

	public AmstradProgram getProgram() {
		return program;
	}

}