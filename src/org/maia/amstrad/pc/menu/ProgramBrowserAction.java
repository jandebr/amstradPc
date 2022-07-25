package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.display.browser.ProgramBrowserDisplaySource;

public class ProgramBrowserAction extends AmstradPcAction {

	private static String NAME_OPEN = "Open program browser";

	private static String NAME_CLOSE = "Close program browser";

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addMonitorListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (NAME_OPEN.equals(getName())) {
			ProgramBrowserDisplaySource displaySource = new ProgramBrowserDisplaySource(getAmstradPc());
			getAmstradPc().swapDisplaySource(displaySource);
		} else {
			getAmstradPc().resetDisplaySource();
		}
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradPcDisplaySourceChanged(amstradPc);
		updateName();
	}

	private void updateName() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getCurrentAlternativeDisplaySource();
		if (altDisplaySource == null) {
			// primary display is showing
			changeName(NAME_OPEN);
		} else if (altDisplaySource instanceof ProgramBrowserDisplaySource) {
			// program browser is showing
			changeName(NAME_CLOSE);
		}
	}

}