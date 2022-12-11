package org.maia.amstrad.basic.locomotive.source;

public class SingleDigitDecimalToken extends NumericToken {

	public SingleDigitDecimalToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitSingleDigitDecimal(this);
	}

}