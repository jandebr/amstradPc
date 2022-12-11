package org.maia.amstrad.basic.locomotive.source;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeywords.BasicKeyword;

public class BasicKeywordToken extends SourceToken {

	private BasicKeyword keyword;

	public static final char REMARK_SHORTHAND = '\'';

	public BasicKeywordToken(String sourceFragment, BasicKeyword keyword) {
		super(sourceFragment);
		this.keyword = keyword;
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitBasicKeyword(this);
	}

	public BasicKeyword getKeyword() {
		return keyword;
	}

}