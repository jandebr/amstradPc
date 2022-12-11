package org.maia.amstrad.basic.locomotive.source;

public abstract class VariableToken extends SourceToken {

	protected VariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	public abstract String getVariableNameWithoutTypeIndicator();

}