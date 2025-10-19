package org.maia.amstrad.gui.covers.stock.fabric;

import java.awt.Color;

import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public class TestPatchPatternGenerator extends FabricPatchPatternGenerator {

	public TestPatchPatternGenerator() {
		this(new Randomizer());
	}

	public TestPatchPatternGenerator(Randomizer randomizer) {
		super(randomizer);
	}

	@Override
	public FabricPatchPattern generatePattern(int width, int height) {
		Color c0 = new Color(255, 239, 214);
		Color c1 = new Color(ColorUtils.setTransparency(Color.HSBtoRGB(drawFloatUnitNumber(), 0.9f, 0.4f), 0.1f), true);
		Color c2 = new Color(ColorUtils.setTransparency(Color.HSBtoRGB(drawFloatUnitNumber(), 0.8f, 0.1f), 0.1f), true);
		FabricPatchPattern pattern = new FabricPatchPattern(c0, false);
		pattern.addPatch(new FabricPatch(c1, width / 10, 0, width / 10, height));
		pattern.addPatch(new FabricPatch(c2, width / 3, 0, width / 3, height));
		pattern.addPatch(new FabricPatch(c1, width * 8 / 10, 0, width / 10, height));
		pattern.addPatch(new FabricPatch(c2, 0, (height * 3 / 4) / 2, width, height / 4));
		return pattern;
	}

}