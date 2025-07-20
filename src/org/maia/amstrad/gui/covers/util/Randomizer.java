package org.maia.amstrad.gui.covers.util;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Randomizer {

	private Random randomNumberGenerator;

	public Randomizer() {
		this(new Random());
	}

	public Randomizer(Object seed) {
		this(seed.hashCode());
	}

	public Randomizer(long seed) {
		this(new Random(seed));
	}

	private Randomizer(Random randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public boolean drawBoolean() {
		return getRandomNumberGenerator().nextBoolean();
	}

	public int drawIntegerNumber(int minInclusive, int maxInclusive) {
		return minInclusive + (int) Math.floor(drawFloatUnitNumber() * (maxInclusive - minInclusive + 1));
	}

	public float drawFloatUnitNumber() {
		return getRandomNumberGenerator().nextFloat();
	}

	public void shuffle(List<?> list) {
		Collections.shuffle(list, getRandomNumberGenerator());
	}

	private Random getRandomNumberGenerator() {
		return randomNumberGenerator;
	}

}