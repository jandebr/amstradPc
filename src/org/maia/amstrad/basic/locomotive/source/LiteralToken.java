package org.maia.amstrad.basic.locomotive.source;

public class LiteralToken extends AbstractLiteralToken {

	public LiteralToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitLiteral(this);
	}

}