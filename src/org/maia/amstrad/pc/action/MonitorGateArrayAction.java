package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorGateArrayAction extends AmstradPcAction {

	public MonitorGateArrayAction(AmstradPc amstradPc) {
		this(amstradPc, "Full gate array");
	}

	public MonitorGateArrayAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().getMonitor().setFullGateArray(state);
	}

}