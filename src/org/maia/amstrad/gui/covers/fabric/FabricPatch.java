package org.maia.amstrad.gui.covers.fabric;

import java.awt.Color;

public class FabricPatch {

	private Color baseColor;

	private int offsetX;

	private int offsetY;

	private int width;

	private int height;

	private Orientation orientation;

	public FabricPatch(Color baseColor, int offsetX, int offsetY, int width, int height) {
		this(baseColor, offsetX, offsetY, width, height,
				width > height ? Orientation.HORIZONTAL : Orientation.VERTICAL);
	}

	public FabricPatch(Color baseColor, int offsetX, int offsetY, int width, int height, Orientation orientation) {
		this.baseColor = baseColor;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
		this.orientation = orientation;
	}

	public boolean isHorizontal() {
		return Orientation.HORIZONTAL.equals(getOrientation());
	}

	public boolean isVertical() {
		return Orientation.VERTICAL.equals(getOrientation());
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

	public Orientation getOrientation() {
		return orientation;
	}

	public static enum Orientation {

		HORIZONTAL,

		VERTICAL;

	}

}