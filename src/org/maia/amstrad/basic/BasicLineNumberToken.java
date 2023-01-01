package org.maia.amstrad.basic;

public class BasicLineNumberToken extends BasicSourceToken {

	public BasicLineNumberToken(String sourceFragment) {
		super(sourceFragment);
	}

	public int getLineNumber() {
		return Integer.parseInt(getSourceFragment());
	}

}