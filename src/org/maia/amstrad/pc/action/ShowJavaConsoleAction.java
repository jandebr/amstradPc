package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class ShowJavaConsoleAction extends AmstradPcAction {

	public ShowJavaConsoleAction(AmstradPc amstradPc) {
		this(amstradPc, "Show Java console");
	}

	public ShowJavaConsoleAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradContext().showJavaConsole();
	}

}