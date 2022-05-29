package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import org.maia.amstrad.pc.AmstradContext;
import org.maia.amstrad.pc.AmstradPc;

public class ShowMessagesAtPromptAction extends AmstradPcAction {

	public ShowMessagesAtPromptAction(AmstradPc amstradPc) {
		this(amstradPc, "Show messages at prompt");
	}

	public ShowMessagesAtPromptAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public ShowMessagesAtPromptAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean show = ((JCheckBoxMenuItem) event.getSource()).getState();
		AmstradContext.setShowMessagesAtPrompt(show);
	}

}