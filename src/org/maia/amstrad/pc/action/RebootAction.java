package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class RebootAction extends AmstradPcAction {

	public RebootAction(AmstradPc amstradPc) {
		this(amstradPc, "Reboot");
	}

	public RebootAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().reboot(false, false);
	}

}