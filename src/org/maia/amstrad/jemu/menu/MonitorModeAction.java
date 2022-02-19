package org.maia.amstrad.jemu.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.maia.amstrad.jemu.AmstradMonitorMode;
import org.maia.amstrad.jemu.AmstradPc;

public class MonitorModeAction extends AmstradPcAction {

	private AmstradMonitorMode mode;

	public MonitorModeAction(AmstradMonitorMode mode, AmstradPc amstradPc, String name) {
		this(mode, amstradPc, name, null);
	}

	public MonitorModeAction(AmstradMonitorMode mode, AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
		this.mode = mode;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().setMonitorMode(getMode());
	}

	public AmstradMonitorMode getMode() {
		return mode;
	}

}