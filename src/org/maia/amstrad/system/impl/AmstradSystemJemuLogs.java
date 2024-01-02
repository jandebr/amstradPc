package org.maia.amstrad.system.impl;

import org.maia.amstrad.system.AmstradSystemLogs;

import jemu.ui.Console;

public class AmstradSystemJemuLogs implements AmstradSystemLogs {

	public AmstradSystemJemuLogs() {
	}

	@Override
	public void init() {
		Console.init();
	}

	@Override
	public void show() {
		Console.frameconsole.setVisible(true);
	}

}