package org.maia.amstrad.basic.locomotive.source;

public class StringTypedVariableToken extends TypedVariableToken {

	public static final char TYPE_INDICATOR = '$';

	public StringTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitStringTypedVariable(this);
	}

}