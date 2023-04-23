package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorShowSystemStatsAction extends AmstradPcAction {

	public MonitorShowSystemStatsAction(AmstradPc amstradPc) {
		this(amstradPc, "Show system stats");
	}

	public MonitorShowSystemStatsAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().getMonitor().setShowSystemStats(state);
	}

}