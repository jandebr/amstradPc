package org.maia.amstrad.gui.components;

import java.util.List;
import java.util.Vector;

public class ColoredLine implements Item {

	private List<ColoredTextSpan> textSpans;

	public ColoredLine() {
		this.textSpans = new Vector<ColoredTextSpan>();
	}

	public ColoredLine(ColoredTextSpan... textSpans) {
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