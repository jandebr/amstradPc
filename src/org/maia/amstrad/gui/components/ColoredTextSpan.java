package org.maia.amstrad.gui.components;

public class ColoredTextSpan {

	private String text;

	private int paperColorIndex;

	private int penColorIndex;

	public ColoredTextSpan(String text, int paperColorIndex, int penColorIndex) {
		this.text = text;
		this.paperColorIndex = paperColorIndex;
		this.penColorIndex = penColorIndex;
	}

	public String getText() {
		return text;
	}

	public int getPaperColorIndex() {
		return paperColorIndex;
	}

	public int getPenColorIndex() {
		return penColorIndex;
	}

}