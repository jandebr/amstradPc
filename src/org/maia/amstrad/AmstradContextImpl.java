package org.maia.amstrad;

import java.io.PrintStream;

public class AmstradContextImpl extends AmstradContext {

	private AmstradSettings userSettings;

	private PrintStream consoleOutputStream;

	private PrintStream consoleErrorStream;

	public AmstradContextImpl(AmstradSettings userSettings, PrintStream consoleOutputStream,
			PrintStream consoleErrorStream) {
		this.userSettings = userSettings;
		this.consoleOutputStream = consoleOutputStream;
		this.consoleErrorStream = consoleErrorStream;
	}

	@Override
	public AmstradSettings getUserSettings() {
		return userSettings;
	}

	@Override
	public PrintStream getConsoleOutputStream() {
		return consoleOutputStream;
	}

	@Override
	public PrintStream getConsoleErrorStream() {
		return consoleErrorStream;
	}

}