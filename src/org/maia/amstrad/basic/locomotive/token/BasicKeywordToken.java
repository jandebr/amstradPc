package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicKeyword;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceToken;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class BasicKeywordToken extends LocomotiveBasicSourceToken {

	private LocomotiveBasicKeyword keyword;

	public static final char REMARK_SHORTHAND = '\'';

	public BasicKeywordToken(LocomotiveBasicKeyword keyword) {
		super(keyword.getSourceForm());
		this.keyword = keyword;
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
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

	public LocomotiveBasicKeyword getKeyword() {
		return keyword;
	}

}