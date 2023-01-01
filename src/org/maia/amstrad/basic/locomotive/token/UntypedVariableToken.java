package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class UntypedVariableToken extends VariableToken {

	public UntypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public String getVariableNameWithoutTypeIndicator() {
		return getSourceFragment();
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitUntypedVariable(this);
	}

}