package org.maia.amstrad.jemu.impl;

import java.io.PrintStream;

import org.maia.amstrad.jemu.AmstradContext;
import org.maia.amstrad.jemu.AmstradSettings;

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