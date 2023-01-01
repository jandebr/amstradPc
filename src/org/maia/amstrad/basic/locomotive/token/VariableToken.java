package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceToken;

public abstract class VariableToken extends LocomotiveBasicSourceToken {

	protected VariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	public abstract String getVariableNameWithoutTypeIndicator();

}