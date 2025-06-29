package org.maia.amstrad.gui.covers.fabric;

import java.awt.Color;

import org.maia.amstrad.gui.covers.fabric.FabricPatch.Orientation;
import org.maia.amstrad.gui.covers.util.Randomizer;

public class FabricPatchPatternTestGenerator extends FabricPatchPatternGenerator {

	public FabricPatchPatternTestGenerator(Randomizer randomizer) {
		super(randomizer);
	}

	@Override
	public FabricPatchPattern generatePattern(int width, int height) {
		Color c0 = new Color(255, 239, 214);
		Color c1 = new Color(39, 89, 168, 240);
		Color c2 = new Color(145, 25, 16, 240);
		FabricPatchPattern pattern = new FabricPatchPattern(c0, FabricPatchStackingOrder.ORIGINAL);
		pattern.addPatch(new FabricPatch(c1, width / 10, 0, width / 10, height, Orientation.VERTICAL));
		pattern.addPatch(new FabricPatch(c2, width / 3, 0, width / 3, height, Orientation.VERTICAL));
		pattern.addPatch(new FabricPatch(c1, width * 8 / 10, 0, width / 10, height, Orientation.VERTICAL));
		pattern.addPatch(new FabricPatch(c2, 0, (height * 3 / 4) / 2, width, height / 4, Orientation.HORIZONTAL));
		return pattern;
	}

}