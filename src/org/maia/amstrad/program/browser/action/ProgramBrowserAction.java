package org.maia.amstrad.program.browser.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
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
		amstradPc.getMonitor().addMonitorListener(this);
		amstradPc.getKeyboard().addKeyboardListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramBrowser();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_B && event.isControlDown()
				&& !event.isShiftDown()) {
			toggleProgramBrowser();
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
			getAmstradPc().getMonitor().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideProgramBrowser() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().resetDisplaySource();
		}
	}

	public void reset() {
		invalidateDisplaySource();
		if (isProgramBrowserShowing()) {
			getAmstradPc().getMonitor().swapDisplaySource(getDisplaySource());
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		super.amstradDisplaySourceChanged(monitor);
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
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getMonitor()
				.getCurrentAlternativeDisplaySource();
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