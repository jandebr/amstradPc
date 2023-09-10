package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class MonitorModeAction extends AmstradPcAction {

	private AmstradMonitorMode mode;

	public MonitorModeAction(AmstradMonitorMode mode, AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		this.mode = mode;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().getMonitor().setMode(getMode());
	}

	public AmstradMonitorMode getMode() {
		return mode;
	}

}