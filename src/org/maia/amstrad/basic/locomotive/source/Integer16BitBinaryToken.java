package org.maia.amstrad.basic.locomotive.source;

public class Integer16BitBinaryToken extends NumericToken {

	public Integer16BitBinaryToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public int parseAsInt() {
		return Integer.parseInt(getSourceFragment().substring(2), 2); // ex. &X11010
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitInteger16BitBinary(this);
	}

}
