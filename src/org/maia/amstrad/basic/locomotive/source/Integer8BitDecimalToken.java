package org.maia.amstrad.basic.locomotive.source;

public class Integer8BitDecimalToken extends NumericToken {

	public Integer8BitDecimalToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitInteger8BitDecimal(this);
	}

}