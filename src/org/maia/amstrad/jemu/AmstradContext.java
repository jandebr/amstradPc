package org.maia.amstrad.jemu;

import java.io.PrintStream;

public abstract class AmstradContext {

	protected AmstradContext() {
	}

	public abstract AmstradSettings getUserSettings();

	public abstract PrintStream getConsoleOutputStream();

	public abstract PrintStream getConsoleErrorStream();

}