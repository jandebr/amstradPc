package org.maia.amstrad.system.impl;

import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemTermination;
import org.maia.util.SystemUtils;

public class AmstradSystemCoreTermination implements AmstradSystemTermination {

	private static final String SETTING_TERMINATE_COMMAND = "poweroff.command";

	public AmstradSystemCoreTermination() {
	}

	@Override
	public void terminate(AmstradSystem system) {
		terminateAmstradPc(system);
		system.getUserSettings().flush();
		if (isExecuteCommand()) {
			executeCommand(system, getTerminationSystemCommand(system));
		}
		System.out.println("Exiting JVM");
		SystemUtils.sleep(200L);
		System.exit(0);
	}

	protected void terminateAmstradPc(AmstradSystem system) {
		system.getAmstradPc().terminate();
	}

	protected void executeCommand(AmstradSystem system, String command) {
		system.getAmstradContext().executeSystemCommand(command);
	}

	protected String getTerminationSystemCommand(AmstradSystem system) {
		AmstradSettings settings = system.getUserSettings();
		String command = settings.get(SETTING_TERMINATE_COMMAND + "." + system.getName().toLowerCase(), null);
		if (command == null) {
			command = settings.get(SETTING_TERMINATE_COMMAND, null);
		}
		return command;
	}

	protected boolean isExecuteCommand() {
		return true;
	}

}