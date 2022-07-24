package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorEffectAction extends AmstradPcAction {

	public MonitorEffectAction(AmstradPc amstradPc) {
		this(amstradPc, "Show monitor effect");
	}

	public MonitorEffectAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public MonitorEffectAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().setMonitorEffect(state);
	}

}