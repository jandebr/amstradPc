package org.maia.amstrad.gui.covers.fabric;

import org.maia.util.Randomizer;

public abstract class FabricPatchPatternGenerator {

	private Randomizer randomizer;

	protected FabricPatchPatternGenerator(Randomizer randomizer) {
		setRandomizer(randomizer);
	}

	public abstract FabricPatchPattern generatePattern(int width, int height);

	protected boolean drawBoolean() {
		return getRandomizer().drawBoolean();
	}

	protected int drawIntegerNumber(int minInclusive, int maxInclusive) {
		return getRandomizer().drawIntegerNumber(minInclusive, maxInclusive);
	}

	protected float drawFloatUnitNumber() {
		return getRandomizer().drawFloatUnitNumber();
	}

	protected double drawDoubleUnitNumber() {
		return getRandomizer().drawDoubleUnitNumber();
	}

	protected double drawGaussian() {
		return getRandomizer().drawGaussian();
	}

	public Randomizer getRandomizer() {
		return randomizer;
	}

	public void setRandomizer(Randomizer randomizer) {
		this.randomizer = randomizer;
	}

}