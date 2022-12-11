package org.maia.amstrad.basic.locomotive.source;

public abstract class NumericToken extends SourceToken {

	public static final char AMPERSAND = '&';

	protected NumericToken(String sourceFragment) {
		super(sourceFragment);
	}

	public int parseAsInt() {
		return Integer.parseInt(getSourceFragment());
	}

	public double parseAsDouble() {
		return Double.parseDouble(getSourceFragment());
	}

}