package org.maia.amstrad.gui.components;

import java.util.List;
import java.util.Vector;

public class ColoredTextLine implements ScrollableItem {

	private List<ColoredTextSpan> textSpans;

	public ColoredTextLine() {
		this.textSpans = new Vector<ColoredTextSpan>();
	}

	public ColoredTextLine(ColoredTextSpan... textSpans) {
		this();
		for (int i = 0; i < textSpans.length; i++) {
			add(textSpans[i]);
		}
	}

	public void add(ColoredTextSpan textSpan) {
		getTextSpans().add(textSpan);
	}

	public List<ColoredTextSpan> getTextSpans() {
		return textSpans;
	}

}