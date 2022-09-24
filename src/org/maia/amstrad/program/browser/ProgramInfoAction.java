package org.maia.amstrad.program.browser;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramInfoAction extends AmstradPcAction implements ProgramBrowserListener {

	private AmstradProgram program;

	private ProgramBrowserDisplaySource displaySource;

	private boolean infoMode;

	private boolean resumeAfterInfoMode;

	private static String NAME_OPEN = "Show program info";

	private static String NAME_CLOSE = "Hide program info";

	public ProgramInfoAction(ProgramBrowserAction browserAction) {
		super(browserAction.getAmstradPc(), "");
		browserAction.addListener(this);
		getAmstradPc().addStateListener(this);
		getAmstradPc().addMonitorListener(this);
		getAmstradPc().addEventListener(this);
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

	private void updateProgram(AmstradProgram program) {
		if (program != null && program.hasDescriptiveInfo()) {
			setProgram(program);
			setDisplaySource(AmstradFactory.getInstance().createProgramInfo(getAmstradPc(), program));
			setEnabled(true);
		} else {
			setProgram(null);
			setDisplaySource(null);
			setEnabled(false);
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleProgramInfo();
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

	public void toggleProgramInfo() {
		if (NAME_OPEN.equals(getName())) {
			showProgramInfo();
		} else {
			hideProgramInfo();
		}
	}

	public void showProgramInfo() {
		if (isEnabled()) {
			resumeAfterInfoMode = !getAmstradPc().isPaused();
			getAmstradPc().pause(); // auto-pause
			getAmstradPc().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideProgramInfo() {
		if (isEnabled()) {
			getAmstradPc().resetDisplaySource();
		}
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradPcDisplaySourceChanged(amstradPc);
		updateName();
		if (amstradPc.isPrimaryDisplaySourceShowing()) {
			if (infoMode) {
				infoMode = false; // exit info screen
				if (resumeAfterInfoMode) {
					amstradPc.resume(); // auto-resume
				}
			}
		} else if (isProgramInfoShowing()) {
			infoMode = true;
		}
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		super.amstradPcRebooting(amstradPc);
		if (isProgramInfoShowing()) {
			resumeAfterInfoMode = false;
			hideProgramInfo();
		}
		updateProgram(null);
	}

	private void updateName() {
		if (isProgramInfoShowing()) {
			changeName(NAME_CLOSE);
		} else {
			changeName(NAME_OPEN);
		}
	}

	public boolean isProgramInfoShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getCurrentAlternativeDisplaySource();
		if (altDisplaySource == null)
			return false;
		if (!(altDisplaySource instanceof ProgramBrowserDisplaySource))
			return false;
		return ((ProgramBrowserDisplaySource) altDisplaySource).isStandaloneInfo();
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