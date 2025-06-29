package org.maia.amstrad.gui.covers.util;

public abstract class RandomImageMaker {

	private Randomizer randomizer;

	protected RandomImageMaker(Randomizer randomizer) {
		setRandomizer(randomizer);
	}

	protected boolean drawBoolean() {
		return getRandomizer().drawBoolean();
	}

	protected int drawIntegerNumber() {
		return getRandomizer().drawIntegerNumber();
	}

	protected int drawIntegerNumber(int minInclusive, int maxInclusive) {
		return getRandomizer().drawIntegerNumber(minInclusive, maxInclusive);
	}

	protected float drawFloatUnitNumber() {
		return getRandomizer().drawFloatUnitNumber();
	}

	public Randomizer getRandomizer() {
		return randomizer;
	}

	public void setRandomizer(Randomizer randomizer) {
		this.randomizer = randomizer;
	}

}