package org.maia.amstrad.basic.locomotive.source;

public class FloatingPointTypedVariableToken extends TypedVariableToken {

	public FloatingPointTypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitFloatingPointTypedVariable(this);
	}

}