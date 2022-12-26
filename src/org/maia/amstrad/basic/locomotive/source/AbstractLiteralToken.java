package org.maia.amstrad.basic.locomotive.source;

import org.maia.amstrad.util.StringUtils;

public abstract class AbstractLiteralToken extends SourceToken {

	protected AbstractLiteralToken(String sourceFragment) {
		super(sourceFragment);
	}

	public boolean isBlank() {
		return StringUtils.isBlank(getSourceFragment());
	}

}