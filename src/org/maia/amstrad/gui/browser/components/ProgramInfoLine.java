package org.maia.amstrad.gui.browser.components;

import java.util.List;
import java.util.Vector;

public class ProgramInfoLine {

	private List<ProgramInfoTextSpan> textSpans;

	public ProgramInfoLine() {
		this.textSpans = new Vector<ProgramInfoTextSpan>();
	}

	public ProgramInfoLine(ProgramInfoTextSpan... textSpans) {
		this();
		for (int i = 0; i < textSpans.length; i++) {
			add(textSpans[i]);
		}
	}

	public void add(ProgramInfoTextSpan textSpan) {
		getTextSpans().add(textSpan);
	}

	public List<ProgramInfoTextSpan> getTextSpans() {
		return textSpans;
	}

}