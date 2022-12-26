package org.maia.amstrad.basic.locomotive.source;

public class LiteralDataToken extends AbstractLiteralToken {

	public LiteralDataToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
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