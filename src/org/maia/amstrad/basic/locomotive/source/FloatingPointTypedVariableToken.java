package org.maia.amstrad.basic.locomotive.source;

public class FloatingPointTypedVariableToken extends TypedVariableToken {

	public static final char TYPE_INDICATOR = '!';

	public FloatingPointTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitFloatingPointTypedVariable(this);
	}

}