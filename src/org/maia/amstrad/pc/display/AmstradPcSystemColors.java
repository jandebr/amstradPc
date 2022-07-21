package org.maia.amstrad.pc.display;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

public class AmstradPcSystemColors {

	private List<Color> colors;

	private static final int DEFAULT_BORDER_INDEX = 1;

	private static final int DEFAULT_PAPER_INDEX = 1;

	private static final int DEFAULT_PEN_INDEX = 24;

	public AmstradPcSystemColors() {
		this.colors = new Vector<Color>(27);
		loadColors();
	}

	private void loadColors() {
		List<Color> colors = getColors();
		colors.clear();
		colors.add(new Color(0, 0, 0)); // 0 (zwart)
		colors.add(new Color(0, 0, 96)); // 1 (blauw)
		colors.add(new Color(0, 0, 255)); // 2 (lichtblauw)
		colors.add(new Color(96, 0, 0)); // 3 (rood)
		colors.add(new Color(96, 0, 96)); // 4 (magenta)
		colors.add(new Color(96, 0, 255)); // 5 (lichtpaars)
		colors.add(new Color(255, 0, 0)); // 6 (lichtrood)
		colors.add(new Color(255, 0, 96)); // 7 (purper)
		colors.add(new Color(255, 0, 255)); // 8 (lichtmagenta)
		colors.add(new Color(0, 96, 0)); // 9 (groen)
		colors.add(new Color(0, 96, 96)); // 10 (cyaan)
		colors.add(new Color(0, 96, 255)); // 11 (hemelsblauw)
		colors.add(new Color(96, 96, 0)); // 12 (geel)
		colors.add(new Color(96, 96, 96)); // 13 (grijs)
		colors.add(new Color(96, 96, 255)); // 14 (pastelblauw)
		colors.add(new Color(255, 96, 0)); // 15 (oranje)
		colors.add(new Color(255, 96, 96)); // 16 (rose)
		colors.add(new Color(255, 96, 255)); // 17 (pastelmagenta)
		colors.add(new Color(0, 255, 0)); // 18 (lichtgroen)
		colors.add(new Color(0, 255, 96)); // 19 (zeegroen)
		colors.add(new Color(0, 255, 255)); // 20 (lichtcyaan)
		colors.add(new Color(96, 255, 0)); // 21 (geelgroen)
		colors.add(new Color(96, 255, 96)); // 22 (pastelgroen)
		colors.add(new Color(96, 255, 255)); // 23 (pastelcyaan)
		colors.add(new Color(255, 255, 0)); // 24 (lichtgeel)
		colors.add(new Color(255, 255, 96)); // 25 (pastelgeel)
		colors.add(new Color(255, 255, 255)); // 26 (wit)
	}

	public Color getDefaultBorderColor() {
		return getColors().get(DEFAULT_BORDER_INDEX);
	}

	public Color getDefaultPaperColor() {
		return getColors().get(DEFAULT_PAPER_INDEX);
	}

	public Color getDefaultPenColor() {
		return getColors().get(DEFAULT_PEN_INDEX);
	}

	public List<Color> getColors() {
		return colors;
	}

}