package org.maia.amstrad.basic.locomotive.source;

public class OperatorToken extends SourceToken {

	public OperatorToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitOperator(this);
	}

}