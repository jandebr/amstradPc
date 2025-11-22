package org.maia.amstrad.gui.covers.stock.fabric;

import java.awt.Color;

public class FabricHints {

	private Color backgroundColor;

	private Color baseColor;

	public FabricHints() {
	}

	public FabricHints withBackgroundColor(Color backgroundColor) {
		setBackgroundColor(backgroundColor);
		return this;
	}

	public FabricHints withBaseColor(Color baseColor) {
		setBaseColor(baseColor);
		return this;
	}

	public Color getBackgroundColor(Color defaultColor) {
		Color color = getBackgroundColor();
		if (color == null)
			color = defaultColor;
		return color;
	}

	public Color getBaseColor(Color defaultColor) {
		Color color = getBaseColor();
		if (color == null)
			color = defaultColor;
		return color;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public Color getBaseColor() {
		return baseColor;
	}

	public void setBaseColor(Color baseColor) {
		this.baseColor = baseColor;
	}

}