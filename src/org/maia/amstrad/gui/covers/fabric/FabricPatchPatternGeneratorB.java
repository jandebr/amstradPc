package org.maia.amstrad.gui.covers.fabric;

import java.awt.Color;

import org.maia.util.Randomizer;

public class FabricPatchPatternGeneratorB extends FabricPatchPatternGenerator {

	public FabricPatchPatternGeneratorB(Randomizer randomizer) {
		super(randomizer);
	}

	@Override
	public FabricPatchPattern generatePattern(int width, int height) {
		Color c0 = new Color(255, 239, 214);
		Color c1 = new Color(67, 37, 156, 240);
		Color c2 = new Color(26, 135, 29, 240);
		FabricPatchPattern pattern = new FabricPatchPattern(c0);
		pattern.addPatch(new FabricPatch(c1, width / 10, 0, width / 10, height));
		pattern.addPatch(new FabricPatch(c2, width / 3, 0, width / 3, height));
		pattern.addPatch(new FabricPatch(c1, width * 8 / 10, 0, width / 10, height));
		pattern.addPatch(new FabricPatch(c2, 0, (height * 3 / 4) / 2, width, height / 4));
		return pattern;
	}

}