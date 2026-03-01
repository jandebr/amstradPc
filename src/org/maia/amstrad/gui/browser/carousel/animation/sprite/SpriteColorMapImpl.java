package org.maia.amstrad.gui.browser.carousel.animation.sprite;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class SpriteColorMapImpl implements SpriteColorMap {

	private Color defaultColor;

	private Map<Integer, Color> map;

	public SpriteColorMapImpl() {
		this(Color.BLACK);
	}

	public SpriteColorMapImpl(Color defaultColor) {
		this.defaultColor = defaultColor;
		this.map = new HashMap<Integer, Color>();
	}

	@Override
	public Color getColor(int colorIndex) {
		Color color = getMap().get(colorIndex);
		if (color == null) {
			color = getDefaultColor();
		}
		return color;
	}

	public SpriteColorMapImpl setColor(int colorIndex, Color color) {
		getMap().put(colorIndex, color);
		return this;
	}

	public Color getDefaultColor() {
		return defaultColor;
	}

	public void setDefaultColor(Color color) {
		this.defaultColor = color;
	}

	private Map<Integer, Color> getMap() {
		return map;
	}

}