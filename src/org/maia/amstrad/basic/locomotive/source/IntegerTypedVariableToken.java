package org.maia.amstrad.basic.locomotive.source;

public class IntegerTypedVariableToken extends TypedVariableToken {

	public static final char TYPE_INDICATOR = '%';
	
	public IntegerTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitIntegerTypedVariable(this);
	}

}