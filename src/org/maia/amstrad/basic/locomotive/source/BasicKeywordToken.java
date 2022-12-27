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

	@Override
	public int hashCode() {
		return getKeyword().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicKeywordToken other = (BasicKeywordToken) obj;
		return getKeyword().equals(other.getKeyword());
	}

	public BasicKeyword getKeyword() {
		return keyword;
	}

}