package org.maia.amstrad.program.browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramInfoAction extends AmstradPcAction {

	private ProgramBrowserAction browserAction;

	private ProgramBrowserDisplaySource displaySource;

	private static String NAME_OPEN = "Show program info";

	private static String NAME_CLOSE = "Hide program info";

	public ProgramInfoAction(ProgramBrowserAction browserAction) {
		super(browserAction.getAmstradPc(), "");
		this.browserAction = browserAction;
		updateName();
		getAmstradPc().addStateListener(this);
		getAmstradPc().addMonitorListener(this);
		getAmstradPc().addEventListener(this);
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramInfo();
	}

	public void toggleProgramInfo() {
		if (NAME_OPEN.equals(getName())) {
			showProgramInfo();
		} else {
			hideProgramInfo();
		}
	}

	public void showProgramInfo() {
		if (isEnabled() && getDisplaySource() != null) {
			getAmstradPc().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideProgramInfo() {
		if (isEnabled()) {
			getAmstradPc().resetDisplaySource();
		}
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		super.amstradPcRebooting(amstradPc);
		setEnabled(false);
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradPcDisplaySourceChanged(amstradPc);
		updateName();
		if (!isProgramBrowserShowing()) {
			if (hasProgramInfo()) {
				if (getDisplaySource() == null || !getDisplaySource().getInfoSheetProgram().equals(getProgram())) {
					setDisplaySource(createDisplaySource());
				}
				setEnabled(true);
			} else {
				setEnabled(false);
			}
		}
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			AmstradPcKeyboardEvent keyEvent = (AmstradPcKeyboardEvent) event;
			if (keyEvent.isKeyPressed() && keyEvent.getKeyCode() == KeyEvent.VK_F1) {
				toggleProgramInfo();
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

	public boolean hasProgramInfo() {
		AmstradProgram program = getProgram();
		return program != null && program.hasDescriptiveInfo();
	}

	public AmstradProgram getProgram() {
		return getBrowserAction().getDisplaySource().getLastLaunchedProgram();
	}

	private boolean isProgramBrowserShowing() {
		return getBrowserAction().isProgramBrowserShowing();
	}

	private ProgramBrowserAction getBrowserAction() {
		return browserAction;
	}

	private ProgramBrowserDisplaySource createDisplaySource() {
		ProgramBrowserDisplaySource ds = null;
		if (hasProgramInfo()) {
			ds = getBrowserAction().getDisplaySource().createStandaloneInfoDisplaySource(getProgram());
		}
		return ds;
	}

	private ProgramBrowserDisplaySource getDisplaySource() {
		return displaySource;
	}

	private void setDisplaySource(ProgramBrowserDisplaySource displaySource) {
		this.displaySource = displaySource;
	}

}