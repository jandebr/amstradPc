package org.maia.amstrad.program.browser.components;

public class ProgramInfoTextSpan {

	private String text;

	private int paperColorIndex;

	private int penColorIndex;

	public ProgramInfoTextSpan(String text, int paperColorIndex, int penColorIndex) {
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