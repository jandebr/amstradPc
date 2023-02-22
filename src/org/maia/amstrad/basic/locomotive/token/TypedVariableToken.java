package org.maia.amstrad.basic.locomotive.token;

public abstract class TypedVariableToken extends VariableToken {

	protected TypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public final String getVariableNameWithoutTypeIndicator() {
		return getSourceFragment().substring(0, getSourceFragment().length() - 1);
	}

	@Override
	protected final String getCanonicalSourceForm() {
		return getVariableNameWithoutTypeIndicator().toUpperCase() + getTypeIndicator();
	}

	protected abstract char getTypeIndicator();

}