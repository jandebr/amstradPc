package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class Integer8BitDecimalToken extends NumericToken {

	public Integer8BitDecimalToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitInteger8BitDecimal(this);
	}

	public int getValue() {
		return parseAsInt();
	}

}