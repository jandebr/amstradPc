package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.browser.AmstradProgramBrowserStyle;

public class ProgramBrowserStyleAction extends AmstradPcAction {

	private AmstradProgramBrowserStyle style;

	public ProgramBrowserStyleAction(AmstradProgramBrowserStyle style, AmstradPc amstradPc) {
		super(amstradPc, style.getDisplayName());
		this.style = style;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradContext().getProgramBrowserStyleManager().applyStyle(getStyle(), getAmstradPc());
	}

	public AmstradProgramBrowserStyle getStyle() {
		return style;
	}

}