package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.gui.browser.ProgramBrowserStartupAnimationTrigger;
import org.maia.amstrad.pc.AmstradPc;

public class ProgramBrowserStartupAnimationTriggerAction extends AmstradPcAction {

	private ProgramBrowserStartupAnimationTrigger startupAnimationTrigger;

	public ProgramBrowserStartupAnimationTriggerAction(ProgramBrowserStartupAnimationTrigger startupAnimationTrigger,
			AmstradPc amstradPc) {
		super(amstradPc, startupAnimationTrigger.getDisplayName());
		this.startupAnimationTrigger = startupAnimationTrigger;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradContext().setStartupAnimationTrigger(getStartupAnimationTrigger());
	}

	public ProgramBrowserStartupAnimationTrigger getStartupAnimationTrigger() {
		return startupAnimationTrigger;
	}

}