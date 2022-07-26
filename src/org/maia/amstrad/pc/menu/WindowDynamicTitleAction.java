package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradPc;

public class WindowDynamicTitleAction extends AmstradPcAction {

	public WindowDynamicTitleAction(AmstradPc amstradPc) {
		this(amstradPc, "Dynamic window title");
	}

	public WindowDynamicTitleAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean state = ((JCheckBoxMenuItem) event.getSource()).getState();
		getAmstradPc().setWindowTitleDynamic(state);
	}

}