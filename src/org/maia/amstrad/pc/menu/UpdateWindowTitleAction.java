package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import jemu.settings.Settings;

import org.maia.amstrad.pc.AmstradPc;

public class UpdateWindowTitleAction extends AmstradPcAction {

	public UpdateWindowTitleAction(AmstradPc amstradPc) {
		this(amstradPc, "Update window title");
	}

	public UpdateWindowTitleAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public UpdateWindowTitleAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		Settings.set(Settings.UPDATETITLE, String.valueOf(state));
	}

}