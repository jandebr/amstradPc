package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorAutoHideCursorAction extends AmstradPcAction {

	public MonitorAutoHideCursorAction(AmstradPc amstradPc) {
		this(amstradPc, "Autohide cursor");
	}

	public MonitorAutoHideCursorAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().getMonitor().setAutoHideCursor(state);
	}

}