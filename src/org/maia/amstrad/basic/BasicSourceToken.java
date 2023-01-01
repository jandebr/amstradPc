package org.maia.amstrad.basic;

import org.maia.amstrad.util.StringUtils;

public abstract class BasicSourceToken {

	private String sourceFragment;

	protected BasicSourceToken(String sourceFragment) {
		this.sourceFragment = sourceFragment;
	}

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
		BasicSourceToken other = (BasicSourceToken) obj;
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

	public boolean isBlank() {
		return StringUtils.isBlank(getSourceFragment());
	}

	public String getSourceFragment() {
		return sourceFragment;
	}

}