package org.maia.amstrad.gui.covers.fabric;

import java.awt.Color;

public class FabricPatch {

	private Color baseColor;

	private int offsetX;

	private int offsetY;

	private int width;

	private int height;

	public FabricPatch(Color baseColor, int offsetX, int offsetY, int width, int height) {
		this.baseColor = baseColor;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
	}

	public Color getBaseColor() {
		return baseColor;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}