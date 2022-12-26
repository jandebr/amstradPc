package org.maia.amstrad.basic.locomotive.source;

public abstract class SourceToken {

	private String sourceFragment;

	protected SourceToken(String sourceFragment) {
		this.sourceFragment = sourceFragment;
	}

	public abstract void invite(SourceTokenVisitor visitor);

	@Override
	public int hashCode() {
		return getSourceFragment().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceToken other = (SourceToken) obj;
		return getSourceFragment().equals(other.getSourceFragment());
	}

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
		String name = getClass().getSimpleName();
		if (name.endsWith("Token")) {
			name = name.substring(0, name.length() - 5);
		}
		return name;
	}

	public String getSourceFragment() {
		return sourceFragment;
	}

}