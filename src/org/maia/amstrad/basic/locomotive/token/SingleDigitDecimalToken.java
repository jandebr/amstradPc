package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class SingleDigitDecimalToken extends NumericToken {

	public SingleDigitDecimalToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitSingleDigitDecimal(this);
	}

	public int getValue() {
		return parseAsInt();
	}

}