package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.browser.ProgramBrowserDisplaySource;

public class OpenProgramBrowserAction extends AmstradPcAction {

	public OpenProgramBrowserAction(AmstradPc amstradPc) {
		this(amstradPc, "Open program browser");
	}

	public OpenProgramBrowserAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public OpenProgramBrowserAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		ProgramBrowserDisplaySource displaySource = new ProgramBrowserDisplaySource(getAmstradPc());
		getAmstradPc().swapDisplaySource(displaySource);
	}

}