package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorScanLinesEffectAction extends AmstradPcAction {

	public MonitorScanLinesEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Show scan lines");
	}

	public MonitorScanLinesEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public MonitorScanLinesEffectAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().setMonitorScanLinesEffect(state);
	}

}