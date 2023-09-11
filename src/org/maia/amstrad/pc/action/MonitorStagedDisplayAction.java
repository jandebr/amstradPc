package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

import jemu.ui.Switches;

public class MonitorStagedDisplayAction extends AmstradPcAction {

	public MonitorStagedDisplayAction(AmstradPc amstradPc) {
		this(amstradPc, "Staged display (beta)");
	}

	public MonitorStagedDisplayAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		Switches.stagedDisplay = state;
	}

}