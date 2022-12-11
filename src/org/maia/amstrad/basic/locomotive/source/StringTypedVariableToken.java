package org.maia.amstrad.basic.locomotive.source;

public class StringTypedVariableToken extends TypedVariableToken {

	public StringTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitStringTypedVariable(this);
	}

}