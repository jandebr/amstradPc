package org.maia.amstrad.gui.covers.fabric;

import org.maia.util.Randomizer;

public abstract class FabricPatchPatternGenerator {

	private Randomizer randomizer;

	protected FabricPatchPatternGenerator(Randomizer randomizer) {
		setRandomizer(randomizer);
	}

	public abstract FabricPatchPattern generatePattern(int width, int height);

	protected int drawIntegerNumber(int minInclusive, int maxInclusive) {
		return getRandomizer().drawIntegerNumber(minInclusive, maxInclusive);
	}

	public Randomizer getRandomizer() {
		return randomizer;
	}

	public void setRandomizer(Randomizer randomizer) {
		this.randomizer = randomizer;
	}

}