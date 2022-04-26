package org.maia.amstrad.pc;

import java.io.PrintStream;

public abstract class AmstradContext {

	protected AmstradContext() {
	}

	public static void sleep(long milliseconds) {
		if (milliseconds > 0L) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

	public abstract AmstradSettings getUserSettings();

	public abstract PrintStream getConsoleOutputStream();

	public abstract PrintStream getConsoleErrorStream();

}