package org.maia.amstrad.gui.browser.components;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.components.ColoredLine;
import org.maia.amstrad.gui.components.ItemList;
import org.maia.amstrad.program.AmstradProgram;

public abstract class ProgramSheet extends ItemList<ColoredLine> {

	private AmstradProgram program;

	private List<ColoredLine> lineItems;

	protected ProgramSheet(AmstradProgram program, int maxItemsShowing, int maxWidth, int backgroundColorIndex) {
		super(maxItemsShowing);
		this.program = program;
		this.lineItems = new Vector<ColoredLine>();
		populateSheet(maxWidth, backgroundColorIndex);
	}

	protected abstract void populateSheet(int maxWidth, int backgroundColorIndex);

	protected void add(ColoredLine lineItem) {
		getLineItems().add(lineItem);
	}

	@Override
	public int size() {
		return getLineItems().size();
	}

	@Override
	public ColoredLine getItem(int index) {
		return getLineItems().get(index);
	}

	public AmstradProgram getProgram() {
		return program;
	}

	private List<ColoredLine> getLineItems() {
		return lineItems;
	}

}