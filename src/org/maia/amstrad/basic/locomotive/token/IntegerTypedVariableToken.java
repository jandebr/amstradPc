package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class IntegerTypedVariableToken extends TypedVariableToken {

	public static final char TYPE_INDICATOR = '%';

	public static IntegerTypedVariableToken forName(String variableNameWithoutTypeIndicator) {
		return new IntegerTypedVariableToken(variableNameWithoutTypeIndicator + TYPE_INDICATOR);
	}

	public IntegerTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitIntegerTypedVariable(this);
	}

	@Override
	public char getTypeIndicator() {
		return TYPE_INDICATOR;
	}

}