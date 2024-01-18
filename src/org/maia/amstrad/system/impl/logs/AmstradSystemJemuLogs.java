package org.maia.amstrad.system.impl.logs;

import org.maia.amstrad.system.AmstradSystemLogs;

import jemu.ui.Console;

public class AmstradSystemJemuLogs implements AmstradSystemLogs {

	public AmstradSystemJemuLogs() {
		Console.init();
	}

	@Override
	public void show() {
		Console.getInstance().showInFrame();
	}

}