package org.maia.amstrad.pc.action;

import java.awt.event.KeyEvent;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.classic.ClassicProgramInfoDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramInfoAction extends ToggleDisplaySourceAction {

	private AmstradProgram program;

	private ClassicProgramInfoDisplaySource displaySource;

	public static String KEY_TRIGGER_TEXT = "Ctrl F1";

	public ProgramInfoAction(AmstradPc amstradPc) {
		super(amstradPc, "Show program info", "Hide program info",
				new ToggleActionKey(KeyEvent.VK_F1, CTRL_KEY_MODIFIER));
		amstradPc.addStateListener(this);
		clearProgram();
	}

	public final void clearProgram() {
		updateProgram(null);
	}

	public void updateProgram(AmstradProgram program) {
		if (program != null && program.hasDescriptiveInfo()) {
			setProgram(program);
			setDisplaySource(AmstradFactory.getInstance().createProgramInfoDisplaySource(getAmstradPc(), program));
			setEnabled(true);
		} else {
			setEnabled(false);
			setProgram(null);
			setDisplaySource(null);
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		super.amstradDisplaySourceChanged(monitor);
		if (!isProgramInfoShowing()) {
			if (monitor.isPrimaryDisplaySourceShowing()) {
				setEnabled(getDisplaySource() != null);
			} else {
				setEnabled(false);
			}
		}
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		super.amstradPcRebooting(amstradPc);
		if (isProgramInfoShowing()) {
			doCloseDisplaySource();
		}
		clearProgram();
	}

	@Override
	protected void doShowDisplaySource() {
		getDisplaySource().setBackdropImage(getAmstradPc().getMonitor().makeScreenshot(false));
		super.doShowDisplaySource();
	}

	@Override
	protected boolean isDisplaySourceShowing() {
		return isProgramInfoShowing();
	}

	public boolean isProgramInfoShowing() {
		return getAmstradContext().isProgramStandaloneInfoShowing(getAmstradPc());
	}

	@Override
	protected ClassicProgramInfoDisplaySource getDisplaySource() {
		return displaySource;
	}

	private void setDisplaySource(ClassicProgramInfoDisplaySource displaySource) {
		this.displaySource = displaySource;
	}

	public AmstradProgram getProgram() {
		return program;
	}

	private void setProgram(AmstradProgram program) {
		this.program = program;
	}

}