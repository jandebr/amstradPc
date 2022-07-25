package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorBilinearEffectAction extends AmstradPcAction {

	public MonitorBilinearEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Bilinear display effect");
	}

	public MonitorBilinearEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().setMonitorBilinearEffect(state);
	}

}