package org.maia.amstrad.basic.locomotive.source;

import org.maia.amstrad.util.StringUtils;

public class LiteralToken extends SourceToken {

	public static final char QUOTE = '"';

	public LiteralToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitLiteral(this);
	}

	public boolean isBlank() {
		return StringUtils.isBlank(getSourceFragment());
	}

}