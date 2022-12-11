package org.maia.amstrad.basic.locomotive.source;

public class UntypedVariableToken extends VariableToken {

	public UntypedVariableToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public String getVariableNameWithoutTypeIndicator() {
		return getSourceFragment();
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitUntypedVariable(this);
	}

}