package org.maia.amstrad.gui.covers.fabric;

import org.maia.amstrad.gui.covers.util.Randomizer;

public abstract class FabricPatchPatternGenerator {

	private Randomizer randomizer;

	protected FabricPatchPatternGenerator(Randomizer randomizer) {
		setRandomizer(randomizer);
	}

	public abstract FabricPatchPattern generatePattern(int width, int height);

	protected int drawIntegerNumber(int minInclusive, int maxInclusive) {
		return getRandomizer().drawIntegerNumber(minInclusive, maxInclusive);
	}

	protected FabricPatchStackingOrder drawStackingOrder() {
		FabricPatchStackingOrder[] orders = FabricPatchStackingOrder.values();
		int i = drawIntegerNumber(0, orders.length - 1);
		return orders[i];
	}

	public Randomizer getRandomizer() {
		return randomizer;
	}

	public void setRandomizer(Randomizer randomizer) {
		this.randomizer = randomizer;
	}

}