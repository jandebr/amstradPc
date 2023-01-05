package org.maia.amstrad.basic;

public abstract class BasicCode implements Cloneable {

	private BasicLanguage language;

	protected BasicCode(BasicLanguage language) {
		this.language = language;
	}

	public BasicLanguage getLanguage() {
		return language;
	}

}