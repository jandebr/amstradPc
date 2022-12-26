package org.maia.amstrad.basic.locomotive.source;

public class LiteralRemarkToken extends AbstractLiteralToken {

	public LiteralRemarkToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitLiteralRemark(this);
	}

}