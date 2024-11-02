package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.ProgramBrowserListener;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramInfoAction extends AmstradPcAction implements ProgramBrowserListener {

	private AmstradProgram program;

	private ProgramBrowserDisplaySource displaySource;

	private boolean infoMode;

	private boolean resumeAfterInfoMode;

	private static String NAME_OPEN = "Show program info";

	private static String NAME_CLOSE = "Hide program info";

	public static String KEY_TRIGGER_TEXT = "Ctrl F1";

	public ProgramInfoAction(ProgramBrowserAction browserAction) {
		super(browserAction.getAmstradPc(), "");
		browserAction.addListener(this);
		getAmstradPc().addStateListener(this);
		getAmstradPc().getMonitor().addMonitorListener(this);
		getAmstradPc().getKeyboard().addKeyboardListener(this);
		updateName();
		setEnabled(false);
	}

	@Override
	public void programLoadedFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program) {
		updateProgram(program);
	}

	@Override
	public void programRunFromBrowser(ProgramBrowserDisplaySource displaySource, AmstradProgram program) {
		updateProgram(program);
	}

	public void updateProgram(AmstradProgram program) {
		if (program != null && program.hasDescriptiveInfo()) {
			setProgram(program);
			setDisplaySource(AmstradFactory.getInstance().createProgramInfo(getAmstradPc(), program));
			setEnabled(true);
		} else {
			setEnabled(false);
			setProgram(null);
			setDisplaySource(null);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramInfo();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (!isTriggeredByMenuKeyBindings()) {
			if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_F1 && event.isControlDown()) {
				toggleProgramInfo();
			}
		}
	}

	public void toggleProgramInfo() {
		if (isEnabled()) {
			if (NAME_OPEN.equals(getName())) {
				showProgramInfo();
			} else {
				hideProgramInfo();
			}
		}
	}

	public void showProgramInfo() {
		if (isEnabled()) {
			resumeAfterInfoMode = !getAmstradPc().isPaused();
			getAmstradPc().pause(); // auto-pause
			getDisplaySource().setBackdropImage(getAmstradPc().getMonitor().makeScreenshot(false));
			getAmstradPc().getMonitor().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideProgramInfo() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().resetDisplaySource();
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		super.amstradDisplaySourceChanged(monitor);
		updateName();
		if (isProgramInfoShowing()) {
			infoMode = true; // enter info screen
		} else {
			if (monitor.isPrimaryDisplaySourceShowing()) {
				if (infoMode && resumeAfterInfoMode) {
					monitor.getAmstradPc().resume(); // auto-resume
				}
				setEnabled(getDisplaySource() != null);
			} else {
				setEnabled(false);
			}
			infoMode = false;
		}
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		super.amstradPcRebooting(amstradPc);
		updateProgram(null);
		if (isProgramInfoShowing()) {
			resumeAfterInfoMode = false;
			hideProgramInfo();
		}
	}

	private void updateName() {
		if (isProgramInfoShowing()) {
			changeName(NAME_CLOSE);
		} else {
			changeName(NAME_OPEN);
		}
	}

	public boolean isProgramInfoShowing() {
		return getAmstradContext().isProgramStandaloneInfoShowing(getAmstradPc());
	}

	public AmstradProgram getProgram() {
		return program;
	}

	private void setProgram(AmstradProgram program) {
		this.program = program;
	}

	private ProgramBrowserDisplaySource getDisplaySource() {
		return displaySource;
	}

	private void setDisplaySource(ProgramBrowserDisplaySource displaySource) {
		this.displaySource = displaySource;
	}

}