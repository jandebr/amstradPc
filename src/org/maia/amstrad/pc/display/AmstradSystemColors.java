package org.maia.amstrad.pc.display;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradMonitorMode;

public class AmstradSystemColors {

	private static Map<AmstradMonitorMode, AmstradSystemColors> colorsByMonitorMode;

	private static final int DEFAULT_BORDER_INK_INDEX = 1;

	private static final int DEFAULT_PAPER_INK_INDEX = 1;

	private static final int DEFAULT_PEN_INK_INDEX = 24;

	private List<Color> colors;

	static {
		colorsByMonitorMode = new HashMap<AmstradMonitorMode, AmstradSystemColors>();
	}

	private AmstradSystemColors(List<Color> colors) {
		this.colors = colors;
	}

	public static AmstradSystemColors getSystemColors(AmstradMonitorMode mode) {
		AmstradSystemColors systemColors = colorsByMonitorMode.get(mode);
		if (systemColors == null) {
			systemColors = new AmstradSystemColors(getColorsForMonitorMode(mode));
			colorsByMonitorMode.put(mode, systemColors);
		}
		return systemColors;
	}

	private static List<Color> getColorsForMonitorMode(AmstradMonitorMode mode) {
		List<Color> colors = null;
		if (AmstradMonitorMode.GREEN.equals(mode)) {
			colors = getGreenColors();
		} else if (AmstradMonitorMode.GRAY.equals(mode)) {
			colors = getGrayColors();
		} else {
			colors = getTrueColors();
		}
		return colors;
	}

	private static List<Color> getTrueColors() {
		List<Color> colors = new Vector<Color>(27);
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
		return colors;
	}

	private static List<Color> getGreenColors() {
		List<Color> colors = new Vector<Color>(27);
		colors.add(new Color(0, 0, 0)); // 0
		colors.add(new Color(0, 10, 0)); // 1
		colors.add(new Color(0, 19, 0)); // 2
		colors.add(new Color(0, 29, 0)); // 3
		colors.add(new Color(0, 38, 0)); // 4
		colors.add(new Color(0, 48, 0)); // 5
		colors.add(new Color(0, 57, 0)); // 6
		colors.add(new Color(0, 67, 0)); // 7
		colors.add(new Color(0, 76, 0)); // 8
		colors.add(new Color(0, 87, 0)); // 9
		colors.add(new Color(0, 96, 0)); // 10
		colors.add(new Color(0, 106, 0)); // 11
		colors.add(new Color(0, 115, 0)); // 12
		colors.add(new Color(0, 125, 0)); // 13
		colors.add(new Color(0, 134, 0)); // 14
		colors.add(new Color(0, 144, 0)); // 15
		colors.add(new Color(0, 153, 0)); // 16
		colors.add(new Color(0, 163, 0)); // 17
		colors.add(new Color(0, 172, 0)); // 18
		colors.add(new Color(0, 181, 0)); // 19
		colors.add(new Color(0, 191, 0)); // 20
		colors.add(new Color(0, 201, 0)); // 21
		colors.add(new Color(0, 210, 0)); // 22
		colors.add(new Color(0, 220, 0)); // 23
		colors.add(new Color(0, 229, 0)); // 24
		colors.add(new Color(0, 239, 0)); // 25
		colors.add(new Color(0, 248, 0)); // 26
		return colors;
	}

	private static List<Color> getGrayColors() {
		List<Color> colors = new Vector<Color>(27);
		colors.add(new Color(0, 0, 0)); // 0
		colors.add(new Color(8, 8, 8)); // 1
		colors.add(new Color(16, 16, 16)); // 2
		colors.add(new Color(24, 24, 24)); // 3
		colors.add(new Color(32, 32, 32)); // 4
		colors.add(new Color(40, 40, 40)); // 5
		colors.add(new Color(48, 48, 48)); // 6
		colors.add(new Color(56, 56, 56)); // 7
		colors.add(new Color(64, 64, 64)); // 8
		colors.add(new Color(72, 72, 72)); // 9
		colors.add(new Color(80, 80, 80)); // 10
		colors.add(new Color(88, 88, 88)); // 11
		colors.add(new Color(96, 96, 96)); // 12
		colors.add(new Color(104, 104, 104)); // 13
		colors.add(new Color(112, 112, 112)); // 14
		colors.add(new Color(120, 120, 120)); // 15
		colors.add(new Color(128, 128, 128)); // 16
		colors.add(new Color(136, 136, 136)); // 17
		colors.add(new Color(144, 144, 144)); // 18
		colors.add(new Color(152, 152, 152)); // 19
		colors.add(new Color(160, 160, 160)); // 20
		colors.add(new Color(168, 168, 168)); // 21
		colors.add(new Color(176, 176, 176)); // 22
		colors.add(new Color(184, 184, 184)); // 23
		colors.add(new Color(192, 192, 192)); // 24
		colors.add(new Color(200, 200, 200)); // 25
		colors.add(new Color(208, 208, 208)); // 26
		return colors;
	}

	public Color getDefaultBorderInk() {
		return getInk(DEFAULT_BORDER_INK_INDEX);
	}

	public Color getDefaultPaperInk() {
		return getInk(DEFAULT_PAPER_INK_INDEX);
	}

	public Color getDefaultPenInk() {
		return getInk(DEFAULT_PEN_INK_INDEX);
	}

	public int getNumberOfInks() {
		return getColors().size();
	}

	public Color getInk(int index) {
		return getColors().get(index);
	}

	private List<Color> getColors() {
		return colors;
	}

}