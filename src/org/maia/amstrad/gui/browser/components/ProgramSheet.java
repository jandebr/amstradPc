package org.maia.amstrad.gui.browser.components;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.program.AmstradProgram;

public abstract class ProgramSheet extends ItemList {

	private AmstradProgram program;

	private List<ProgramInfoLine> lineItems;

	protected ProgramSheet(AmstradProgram program, int maxItemsShowing, int maxWidth, int backgroundColorIndex) {
		super(maxItemsShowing);
		this.program = program;
		this.lineItems = new Vector<ProgramInfoLine>();
		populateSheet(maxWidth, backgroundColorIndex);
	}

	protected abstract void populateSheet(int maxWidth, int backgroundColorIndex);

	protected void add(ProgramInfoLine lineItem) {
		getLineItems().add(lineItem);
	}

	@Override
	public int size() {
		return getLineItems().size();
	}

	public ProgramInfoLine getLineItem(int index) {
		return getLineItems().get(index);
	}

	public AmstradProgram getProgram() {
		return program;
	}

	private List<ProgramInfoLine> getLineItems() {
		return lineItems;
	}

}