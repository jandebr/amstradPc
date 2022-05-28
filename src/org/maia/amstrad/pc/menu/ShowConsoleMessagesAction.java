package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradContext;
import org.maia.amstrad.pc.AmstradPc;

public class ShowConsoleMessagesAction extends AmstradPcAction {

	public ShowConsoleMessagesAction(AmstradPc amstradPc) {
		this(amstradPc, "Show console messages");
	}

	public ShowConsoleMessagesAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public ShowConsoleMessagesAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean show = ((JCheckBoxMenuItem) event.getSource()).getState();
		AmstradContext.setShowConsoleMessages(show);
	}

}