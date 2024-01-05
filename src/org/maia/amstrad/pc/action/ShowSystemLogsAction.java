package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class ShowSystemLogsAction extends AmstradPcAction {

	public ShowSystemLogsAction(AmstradPc amstradPc) {
		this(amstradPc, "Show system logs");
	}

	public ShowSystemLogsAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		setEnabled(isAmstradSystemSetup());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (isEnabled()) {
			getAmstradSystem().getSystemLogs().show();
		}
	}

}