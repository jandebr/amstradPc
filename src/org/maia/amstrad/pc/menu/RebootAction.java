package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.maia.amstrad.pc.AmstradPc;

public class RebootAction extends AmstradPcAction {

	public RebootAction(AmstradPc amstradPc) {
		this(amstradPc, "Reboot");
	}

	public RebootAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public RebootAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().reboot(false);
	}

}