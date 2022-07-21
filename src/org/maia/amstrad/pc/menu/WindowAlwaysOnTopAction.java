package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class WindowAlwaysOnTopAction extends AmstradPcAction {

	public WindowAlwaysOnTopAction(AmstradPc amstradPc) {
		this(amstradPc, "Always on top");
	}

	public WindowAlwaysOnTopAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public WindowAlwaysOnTopAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().setAlwaysOnTop(state);
	}

}