package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceToken;

public abstract class NumericToken extends LocomotiveBasicSourceToken {

	public static final char AMPERSAND = '&';

	protected NumericToken(String sourceFragment) {
		super(sourceFragment);
	}

	public int getInt() {
		return parseAsInt();
	}

	public double getDouble() {
		return parseAsDouble();
	}

	protected int parseAsInt() {
		return Integer.parseInt(getSourceFragment());
	}

	protected double parseAsDouble() {
		return Double.parseDouble(getSourceFragment());
	}

}