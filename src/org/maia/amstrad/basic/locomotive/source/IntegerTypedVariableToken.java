package org.maia.amstrad.basic.locomotive.source;

public class IntegerTypedVariableToken extends TypedVariableToken {

	public IntegerTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitIntegerTypedVariable(this);
	}

}