package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class Integer16BitHexadecimalToken extends NumericToken {

	public Integer16BitHexadecimalToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	protected int parseAsInt() {
		if (getSourceFragment().toUpperCase().startsWith("&H")) {
			return Integer.parseInt(getSourceFragment().substring(2), 16); // ex. &H7A1D
		} else {
			return Integer.parseInt(getSourceFragment().substring(1), 16); // ex. &7A1D
		}
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitInteger16BitHexadecimal(this);
	}

	public int getValue() {
		return getInt();
	}

}