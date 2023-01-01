package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class LiteralQuotedToken extends AbstractLiteralToken {

	public static final char QUOTE = '"';

	public LiteralQuotedToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitLiteralQuoted(this);
	}

}