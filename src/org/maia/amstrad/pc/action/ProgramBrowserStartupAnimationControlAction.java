package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.gui.browser.ProgramBrowserStartupAnimationControl;
import org.maia.amstrad.pc.AmstradPc;

public class ProgramBrowserStartupAnimationControlAction extends AmstradPcAction {

	private ProgramBrowserStartupAnimationControl startupAnimationControl;

	public ProgramBrowserStartupAnimationControlAction(ProgramBrowserStartupAnimationControl startupAnimationControl,
			AmstradPc amstradPc) {
		super(amstradPc, startupAnimationControl.getDisplayName());
		this.startupAnimationControl = startupAnimationControl;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradContext().setStartupAnimationControl(getStartupAnimationControl());
	}

	public ProgramBrowserStartupAnimationControl getStartupAnimationControl() {
		return startupAnimationControl;
	}

}