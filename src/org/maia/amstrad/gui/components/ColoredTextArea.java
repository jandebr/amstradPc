package org.maia.amstrad.gui.components;

import java.util.List;
import java.util.Vector;

public class ColoredTextArea extends ScrollableItemList<ColoredTextLine> {

	private List<ColoredTextLine> lineItems;

	public ColoredTextArea(int maxItemsShowing) {
		super(maxItemsShowing);
		this.lineItems = new Vector<ColoredTextLine>();
	}

	public void clear() {
		getLineItems().clear();
	}

	public void add(ColoredTextLine lineItem) {
		getLineItems().add(lineItem);
	}

	@Override
	public int size() {
		return getLineItems().size();
	}

	@Override
	public ColoredTextLine getItem(int index) {
		return getLineItems().get(index);
	}

	private List<ColoredTextLine> getLineItems() {
		return lineItems;
	}

}