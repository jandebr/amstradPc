package org.maia.amstrad.program.browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;

public class ProgramBrowserAction extends AmstradPcAction {

	private ProgramBrowserDisplaySource displaySource;

	private static String NAME_OPEN = "Open program browser";

	private static String NAME_CLOSE = "Close program browser";

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addMonitorListener(this);
		amstradPc.addEventListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramBrowser();
	}

	public void toggleProgramBrowser() {
		if (NAME_OPEN.equals(getName())) {
			showProgramBrowser();
		} else {
			hideProgramBrowser();
		}
	}

	public void showProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().resetDisplaySource();
		}
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradPcDisplaySourceChanged(amstradPc);
		updateName();
		if (isProgramBrowserShowing()) {
			setEnabled(!((ProgramBrowserDisplaySource) amstradPc.getCurrentAlternativeDisplaySource())
					.isStandaloneInfo());
		} else {
			setEnabled(true);
		}
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			AmstradPcKeyboardEvent keyEvent = (AmstradPcKeyboardEvent) event;
			if (keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_B && keyEvent.isControlDown()) {
				toggleProgramBrowser();
			}
		}
	}

	private void updateName() {
		if (isProgramBrowserShowing()) {
			changeName(NAME_CLOSE);
		} else {
			changeName(NAME_OPEN);
		}
	}

	public boolean isProgramBrowserShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getCurrentAlternativeDisplaySource();
		return altDisplaySource != null && altDisplaySource instanceof ProgramBrowserDisplaySource;
	}

	ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = AmstradFactory.getInstance().createProgramBrowserDisplaySource(getAmstradPc());
		}
		return displaySource;
	}

}