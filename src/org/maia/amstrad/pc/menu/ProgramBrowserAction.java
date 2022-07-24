package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.display.browser.ProgramBrowserDisplaySource;

public class ProgramBrowserAction extends AmstradPcAction implements AmstradPcStateListener {

	private static String NAME_OPEN = "Open program browser";

	private static String NAME_CLOSE = "Close program browser";

	public ProgramBrowserAction(AmstradPc amstradPc) {
		this(amstradPc, NAME_OPEN);
		amstradPc.addStateListener(this);
	}

	public ProgramBrowserAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public ProgramBrowserAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
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
	public void amstradPcStarted(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc,
			AmstradAlternativeDisplaySource alternativeDisplaySource) {
		if (alternativeDisplaySource == null) {
			// primary display
			changeName(NAME_OPEN);
		} else if (alternativeDisplaySource instanceof ProgramBrowserDisplaySource) {
			changeName(NAME_CLOSE);
		}
	}

}