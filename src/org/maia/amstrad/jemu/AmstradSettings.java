package org.maia.amstrad.jemu;

public abstract class AmstradSettings {

	protected AmstradSettings() {
	}

	public abstract String get(String key, String defaultValue);

	public abstract void set(String key, String value);

}