package org.maia.amstrad.basic.locomotive.source;

public class FloatingPointNumberToken extends NumericToken {

	public FloatingPointNumberToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitFloatingPointNumber(this);
	}

}