package org.maia.amstrad.basic.locomotive.source;

public class LiteralQuotedToken extends AbstractLiteralToken {

	public static final char QUOTE = '"';

	public LiteralQuotedToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitLiteralQuoted(this);
	}

}