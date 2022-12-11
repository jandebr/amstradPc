package org.maia.amstrad.basic.locomotive.source;

public abstract class SourceToken {

	private String sourceFragment;

	protected SourceToken(String sourceFragment) {
		this.sourceFragment = sourceFragment;
	}

	public abstract void invite(SourceTokenVisitor visitor);

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getToStringTypeName());
		builder.append("[");
		builder.append(getSourceFragment());
		builder.append("]");
		return builder.toString();
	}

	protected String getToStringTypeName() {
		String name = getClass().getTypeName();
		if (name.endsWith("Token")) {
			name = name.substring(0, name.length() - 5);
		}
		return name;
	}

	public String getSourceFragment() {
		return sourceFragment;
	}

}