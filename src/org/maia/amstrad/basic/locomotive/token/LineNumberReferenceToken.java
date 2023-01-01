package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class LineNumberReferenceToken extends NumericToken {

	public LineNumberReferenceToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitLineNumberReference(this);
	}

	public int getLineNumber() {
		return parseAsInt();
	}

}