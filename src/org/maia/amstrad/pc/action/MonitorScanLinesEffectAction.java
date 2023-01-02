package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorScanLinesEffectAction extends AmstradPcAction {

	public MonitorScanLinesEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Show scan lines");
	}

	public MonitorScanLinesEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().getMonitor().setMonitorScanLinesEffect(state);
	}

}