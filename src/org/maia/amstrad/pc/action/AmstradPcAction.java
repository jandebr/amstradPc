package org.maia.amstrad.pc.action;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcMonitorListener;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcEventListener;

public abstract class AmstradPcAction extends AbstractAction implements AmstradPcStateListener,
		AmstradPcMonitorListener, AmstradPcEventListener {

	private AmstradPc amstradPc;

	protected AmstradPcAction(AmstradPc amstradPc, String name) {
		super(name);
		this.amstradPc = amstradPc;
	}

	protected void showInfoMessageDialog(String dialogMessage) {
		JOptionPane.showMessageDialog(getAmstradPc().getDisplayPane(), dialogMessage);
	}

	protected void showErrorMessageDialog(String dialogTitle, String dialogMessage) {
		JOptionPane.showMessageDialog(getAmstradPc().getDisplayPane(), dialogMessage, dialogTitle,
				JOptionPane.ERROR_MESSAGE);
	}

	protected void showErrorMessageDialog(String dialogTitle, String dialogMessage, Exception error) {
		showErrorMessageDialog(dialogTitle, dialogMessage + "\n" + error.getMessage());
	}

	protected void setToolTipText(String text) {
		putValue(Action.SHORT_DESCRIPTION, text);
	}

	protected void changeName(String name) {
		putValue(Action.NAME, name);
	}

	protected void changeSmallIcon(Icon icon) {
		putValue(Action.SMALL_ICON, icon);
	}

	protected String getName() {
		return getValue(Action.NAME).toString();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcStateListener
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcStateListener
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcStateListener
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcStateListener
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcStateListener
	}

	@Override
	public void amstradPcMonitorModeChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcMonitorEffectChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcMonitorScanLinesEffectChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcMonitorBilinearEffectChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcWindowFullscreenChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcWindowAlwaysOnTopChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcWindowTitleDynamicChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcMonitorListener
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		// Subclasses may override after registering with amstradPc as AmstradPcEventListener
	}

}