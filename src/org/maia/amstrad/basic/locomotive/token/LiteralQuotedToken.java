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

	public String getLiteralBetweenQuotes() {
		return getSourceFragment().substring(1, getSourceFragment().length() - 1);
	}

}