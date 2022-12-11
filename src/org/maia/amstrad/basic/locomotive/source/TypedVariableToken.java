package org.maia.amstrad.basic.locomotive.source;

public abstract class TypedVariableToken extends VariableToken {

	protected TypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public String getVariableNameWithoutTypeIndicator() {
		return getSourceFragment().substring(0, getSourceFragment().length() - 1);
	}

}