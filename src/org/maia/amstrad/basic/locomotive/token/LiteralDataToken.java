package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class LiteralDataToken extends AbstractLiteralToken {

	public LiteralDataToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitLiteralData(this);
	}

	public String[] getDataElements() {
		String[] elements = getSourceFragment().split(",");
		for (int i = 0; i < elements.length; i++) {
			elements[i] = elements[i].trim();
		}
		return elements;
	}

}