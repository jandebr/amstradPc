package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class StringTypedVariableToken extends TypedVariableToken {

	public static final char TYPE_INDICATOR = '$';

	public StringTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitStringTypedVariable(this);
	}

}