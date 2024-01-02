package org.maia.amstrad.pc.action;

import java.awt.Component;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JOptionPane;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.audio.AmstradAudio;
import org.maia.amstrad.pc.audio.AmstradAudioListener;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardListener;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorListener;

public abstract class AmstradPcAction extends AbstractAction
		implements AmstradPcStateListener, AmstradMonitorListener, AmstradKeyboardListener, AmstradAudioListener {

	private AmstradPc amstradPc;

	protected AmstradPcAction(AmstradPc amstradPc, String name) {
		super(name);
		this.amstradPc = amstradPc;
	}

	protected void showInfoMessageDialog(String dialogMessage) {
		JOptionPane.showMessageDialog(getDisplayComponent(), dialogMessage);
	}

	protected void showErrorMessageDialog(String dialogTitle, String dialogMessage) {
		JOptionPane.showMessageDialog(getDisplayComponent(), dialogMessage, dialogTitle, JOptionPane.ERROR_MESSAGE);
	}

	protected void showErrorMessageDialog(String dialogTitle, String dialogMessage, Exception error) {
		showErrorMessageDialog(dialogTitle, dialogMessage + "\n" + error.getMessage());
	}

	protected void runInSeparateThread(Runnable task) {
		new Thread(task).start();
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

	protected AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	protected Component getDisplayComponent() {
		return getAmstradPc().getMonitor().getDisplayComponent();
	}

	protected boolean isTriggeredByMenuKeyBindings() {
		return getAmstradPc().isMenuKeyBindingsEnabled();
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
	public void amstradPcProgramLoaded(AmstradPc amstradPc) {
		// Subclasses may override after registering with amstradPc as AmstradPcStateListener
	}

	@Override
	public void amstradMonitorModeChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradMonitorEffectChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradMonitorScanLinesEffectChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradMonitorBilinearEffectChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradMonitorGateArraySizeChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradMonitorAutoHideCursorChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradMonitorSizeChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradMonitorFullscreenChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradWindowAlwaysOnTopChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradShowSystemStatsChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		// Subclasses may override after registering with amstradPc's monitor as AmstradMonitorListener
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		// Subclasses may override after registering with amstradPc's keyboard as AmstradKeyboardListener
	}

	@Override
	public void amstradKeyboardBreakEscaped(AmstradKeyboard keyboard) {
		// Subclasses may override after registering with amstradPc's keyboard as AmstradKeyboardListener
	}

	@Override
	public void amstradAudioMuted(AmstradAudio audio) {
		// Subclasses may override after registering with amstradPc's audio as AmstradAudioListener
	}

	@Override
	public void amstradAudioUnmuted(AmstradAudio audio) {
		// Subclasses may override after registering with amstradPc's audio as AmstradAudioListener
	}

}