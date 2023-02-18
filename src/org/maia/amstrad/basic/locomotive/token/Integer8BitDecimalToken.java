package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class Integer8BitDecimalToken extends NumericToken {

	public Integer8BitDecimalToken(String sourceFragment) {
		super(sourceFragment);
	}

	public Integer8BitDecimalToken(int value) {
		this(String.valueOf(value));
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitInteger8BitDecimal(this);
	}

	public int getValue() {
		return getInt();
	}

}