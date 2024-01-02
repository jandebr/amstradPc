package org.maia.amstrad.system.impl;

import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemTermination;

public class AmstradSystemElementaryTermination implements AmstradSystemTermination {

	private static final String SETTING_TERMINATE_COMMAND = "poweroff.command";

	public AmstradSystemElementaryTermination() {
	}

	@Override
	public void terminate(AmstradSystem system) {
		terminateAmstradPc(system);
		system.getUserSettings().flush();
		if (isExecuteCommand()) {
			executeCommand(system, getTerminationSystemCommand(system));
		}
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