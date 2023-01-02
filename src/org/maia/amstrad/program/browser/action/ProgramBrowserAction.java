package org.maia.amstrad.program.browser.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;
import org.maia.amstrad.pc.monitor.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.browser.ProgramBrowserListener;

public class ProgramBrowserAction extends AmstradPcAction implements ProgramBrowserListener {

	private ProgramBrowserDisplaySource displaySource;

	private List<ProgramBrowserListener> browserListeners;

	private static String NAME_OPEN = "Open program browser";

	private static String NAME_CLOSE = "Close program browser";

	public ProgramBrowserAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		this.browserListeners = new Vector<ProgramBrowserListener>();
		updateName();
		amstradPc.addMonitorListener(this);
		amstradPc.addEventListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramBrowser();
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			AmstradPcKeyboardEvent keyEvent = (AmstradPcKeyboardEvent) event;
			if (keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_B && keyEvent.isControlDown()
					&& !keyEvent.isShiftDown()) {
				toggleProgramBrowser();
			}
		}
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

	public void reset() {
		invalidateDisplaySource();
		if (isProgramBrowserShowing()) {
			getAmstradPc().swapDisplaySource(getDisplaySource());
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradDisplaySourceChanged(amstradPc);
		updateName();
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
		if (altDisplaySource == null)
			return false;
		if (!(altDisplaySource instanceof ProgramBrowserDisplaySource))
			return false;
		return !((ProgramBrowserDisplaySource) altDisplaySource).isStandaloneInfo();
	}

	public void addListener(ProgramBrowserListener listener) {
		getBrowserListeners().add(listener);
	}

	public void removeListener(ProgramBrowserListener listener) {
		getBrowserListeners().remove(listener);
	}

	@Override
	public void programLoadedFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programLoadedFromBrowser(displaySource, program);
		}
	}

	@Override
	public void programRunFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program) {
		for (ProgramBrowserListener listener : getBrowserListeners()) {
			listener.programRunFromBrowser(displaySource, program);
		}
	}

	private void invalidateDisplaySource() {
		if (displaySource != null) {
			displaySource.removeListener(this);
			displaySource = null;
		}
	}

	private ProgramBrowserDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = AmstradFactory.getInstance().createProgramRepositoryBrowser(getAmstradPc());
			displaySource.addListener(this);
		}
		return displaySource;
	}

	private List<ProgramBrowserListener> getBrowserListeners() {
		return browserListeners;
	}

}