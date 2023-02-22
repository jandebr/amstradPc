package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceToken;

public abstract class VariableToken extends LocomotiveBasicSourceToken {

	protected VariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public int hashCode() {
		return getCanonicalSourceForm().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!VariableToken.class.isAssignableFrom(obj.getClass()))
			return false;
		VariableToken other = (VariableToken) obj;
		return getCanonicalSourceForm().equals(other.getCanonicalSourceForm());
	}

	public abstract String getVariableNameWithoutTypeIndicator();

	protected abstract String getCanonicalSourceForm();

}