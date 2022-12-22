package org.maia.amstrad.basic.locomotive.source;

public abstract class NumericToken extends SourceToken {

	public static final char AMPERSAND = '&';

	protected NumericToken(String sourceFragment) {
		super(sourceFragment);
	}

	protected int parseAsInt() {
		return Integer.parseInt(getSourceFragment());
	}

	protected double parseAsDouble() {
		return Double.parseDouble(getSourceFragment());
	}

}