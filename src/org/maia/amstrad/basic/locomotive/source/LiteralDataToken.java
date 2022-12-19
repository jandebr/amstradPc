package org.maia.amstrad.basic.locomotive.source;

public class LiteralDataToken extends LiteralToken {

	public LiteralDataToken(String sourceFragment) {
		super(sourceFragment);
	}

	public String[] getDataElements() {
		String[] elements = getSourceFragment().split(",");
		for (int i = 0; i < elements.length; i++) {
			elements[i] = elements[i].trim();
		}
		return elements;
	}

}