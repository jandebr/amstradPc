package org.maia.amstrad.gui.sprite;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.maia.util.ColorUtils;

public class SpriteColorMapAlphaComposite implements SpriteColorMap {

	private SpriteColorMap baseColorMap;

	private Map<Integer, Color> compositedColorMap;

	private float transparencyFactor; // between 0 (base color) and 1 (fully transparent base color)

	public SpriteColorMapAlphaComposite(SpriteColorMap baseColorMap) {
		this.baseColorMap = baseColorMap;
		this.compositedColorMap = new HashMap<Integer, Color>();
	}

	@Override
	public synchronized Color getColor(int colorIndex) {
		Color color = getCompositedColorMap().get(colorIndex);
		if (color == null) {
			color = applyTransparencyFactor(getBaseColorMap().getColor(colorIndex), getTransparencyFactor());
			getCompositedColorMap().put(colorIndex, color);
		}
		return color;
	}

	@Override
	public int getMaxColorIndex() {
		return getBaseColorMap().getMaxColorIndex();
	}

	private Color applyTransparencyFactor(Color color, float factor) {
		if (factor == 0f) {
			return color;
		} else {
			float t = ColorUtils.getTransparency(color);
			float t1 = 1f - (1f - t) * (1f - factor);
			return ColorUtils.setTransparency(color, t1);
		}
	}

	public synchronized void changeTransparencyFactor(float newFactor) {
		float oldFactor = getTransparencyFactor();
		if (newFactor != oldFactor) {
			setTransparencyFactor(newFactor);
			getCompositedColorMap().clear(); // clear cache
		}
	}

	public float getTransparencyFactor() {
		return transparencyFactor;
	}

	private void setTransparencyFactor(float factor) {
		this.transparencyFactor = factor;
	}

	private SpriteColorMap getBaseColorMap() {
		return baseColorMap;
	}

	private Map<Integer, Color> getCompositedColorMap() {
		return compositedColorMap;
	}

}