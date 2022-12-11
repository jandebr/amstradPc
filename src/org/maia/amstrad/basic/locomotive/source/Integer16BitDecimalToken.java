package org.maia.amstrad.basic.locomotive.source;

public class Integer16BitDecimalToken extends NumericToken {

	public Integer16BitDecimalToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitInteger16BitDecimal(this);
	}

}
