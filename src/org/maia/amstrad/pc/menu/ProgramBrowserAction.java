package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.repo.AmstradProgramRepository;

public class ProgramBrowserAction extends AmstradPcAction {

	private ProgramBrowserDisplaySource displaySource;

	private static String NAME_OPEN = "Open program browser";

	private static String NAME_CLOSE = "Close program browser";

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addStateListener(this);
		amstradPc.addMonitorListener(this);
		amstradPc.addEventListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramBrowser();
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		super.amstradPcPausing(amstradPc);
		setEnabled(false);
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		super.amstradPcResuming(amstradPc);
		setEnabled(true);
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			KeyEvent key = ((AmstradPcKeyboardEvent) event).getKeyPressed();
			if (key.getKeyCode() == KeyEvent.VK_HOME && NAME_OPEN.equals(getName())) {
				toggleProgramBrowser();
			}
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

	private void toggleProgramBrowser() {
		if (isEnabled()) {
			if (NAME_OPEN.equals(getName())) {
				getAmstradPc().swapDisplaySource(getDisplaySource());
			} else {
				getAmstradPc().resetDisplaySource();
			}
		}
	}

	private ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			AmstradProgramRepository repository = AmstradFactory.getInstance().getAmstradContext()
					.getAmstradProgramRepository();
			displaySource = new ProgramBrowserDisplaySource(getAmstradPc(), repository);
		}
		return displaySource;
	}

}