package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class ProgramBrowserResetAction extends AmstradPcAction {

	public ProgramBrowserResetAction(AmstradPc amstradPc) {
		this(amstradPc, "Reset program browser");
	}

	public ProgramBrowserResetAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		reset();
	}

	private void reset() {
		getAmstradPc().getActions().getProgramBrowserAction().getDisplaySource().reset();
	}

}