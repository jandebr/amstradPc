package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class QuitAction extends AmstradPcAction {

	public QuitAction(AmstradPc amstradPc) {
		this(amstradPc, "Quit");
	}

	public QuitAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().terminate();
	}

}