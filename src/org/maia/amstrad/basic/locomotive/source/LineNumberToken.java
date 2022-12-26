package org.maia.amstrad.basic.locomotive.source;

public class LineNumberToken extends NumericToken {

	public LineNumberToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitLineNumber(this);
	}

	public int getValue() {
		return parseAsInt();
	}

}