package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.cursor.AmstradMonitorCursorController;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.system.AmstradSystemSettings;

public abstract class AmstradAbstractDisplaySource extends KeyAdapter
		implements AmstradAlternativeDisplaySource, MouseListener, MouseMotionListener {

	private AmstradPc amstradPc;

	private AmstradKeyboardController keyboardController;

	private Cursor displayComponentInitialCursor;

	private boolean catchKeyboardEvents;

	private boolean restoreMonitorSettingsOnDispose;

	private boolean autoPauseResume;

	protected AmstradAbstractDisplaySource(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		setKeyboardController(getAmstradPc().getKeyboard().getController());
		acquireKeyboard();
		setDisplayComponentInitialCursor(getCursorController().getCursor());
		resetCursor();
		displayComponent.addKeyListener(this);
		displayComponent.addMouseListener(this);
		displayComponent.addMouseMotionListener(this);
		// Subclasses may extend this method
	}

	@Override
	public void dispose(JComponent displayComponent) {
		// Subclasses may extend this method
		displayComponent.removeKeyListener(this);
		displayComponent.removeMouseListener(this);
		displayComponent.removeMouseMotionListener(this);
		releaseKeyboard();
		setCursor(getDisplayComponentInitialCursor());
	}

	@Override
	public boolean isStretchToFullscreen() {
		// Subclasses may override this setting
		return false;
	}

	/**
	 * Shows this display source
	 * <p>
	 * The default behavior is to swap the AmstradPc monitor to this alternative display source. Consequently, this
	 * display source will get initialized.
	 * </p>
	 * 
	 * @see AmstradMonitor#swapDisplaySource(AmstradAlternativeDisplaySource)
	 * @see #init(JComponent, AmstradGraphicsContext)
	 * @see #close()
	 */
	@Override
	public void show() {
		getAmstradPc().getMonitor().swapDisplaySource(this); // will invoke this.init()
	}

	/**
	 * Closes this display source
	 * <p>
	 * The default behavior is to reset the AmstradPc monitor to the primary display source. Consequently, this display
	 * source will get disposed. A closed display source preserves state and can be shown again.
	 * </p>
	 * 
	 * @see AmstradMonitor#resetDisplaySource()
	 * @see #dispose(JComponent)
	 * @see #show()
	 */
	@Override
	public void close() {
		getAmstradPc().getMonitor().resetDisplaySource(); // will invoke this.dispose()
	}

	public final synchronized void acquireKeyboard() {
		setCatchKeyboardEvents(true);
		getKeyboardController().sendKeyboardEventsToComputer(false);
	}

	public final synchronized void releaseKeyboard() {
		setCatchKeyboardEvents(false);
		getKeyboardController().sendKeyboardEventsToComputer(true);
	}

	@Override
	public boolean isAutoRepeatAccepted(AmstradJoystickCommand command) {
		return true; // Subclasses may restrict autorepeat from joystick
	}

	@Override
	public final void pressKey(KeyEvent keyEvent) {
		keyPressed(keyEvent);
	}

	@Override
	public final void releaseKey(KeyEvent keyEvent) {
		keyReleased(keyEvent);
	}

	@Override
	public final void keyPressed(KeyEvent e) {
		super.keyPressed(e);
		if (isCatchKeyboardEvents()) {
			keyboardKeyPressed(e);
		}
	}

	@Override
	public final void keyReleased(KeyEvent e) {
		super.keyReleased(e);
		if (isCatchKeyboardEvents()) {
			keyboardKeyReleased(e);
		}
	}

	@Override
	public final void keyTyped(KeyEvent e) {
		super.keyTyped(e);
		if (isCatchKeyboardEvents()) {
			keyboardKeyTyped(e);
		}
	}

	protected void keyboardKeyPressed(KeyEvent e) {
		// Subclasses may override this method
	}

	protected void keyboardKeyReleased(KeyEvent e) {
		// Subclasses may override this method
	}

	protected void keyboardKeyTyped(KeyEvent e) {
		// Subclasses may override this method
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// Subclasses may override this method
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Subclasses may override this method
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Subclasses may override this method
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Subclasses may override this method
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Subclasses may override this method
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Subclasses may override this method
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Subclasses may override this method
	}

	protected Cursor getDefaultCursor() {
		// Subclasses may override this method
		if (getDisplayComponentInitialCursor() != null) {
			return getDisplayComponentInitialCursor();
		} else {
			return Cursor.getDefaultCursor();
		}
	}

	protected void resetCursor() {
		setCursor(getDefaultCursor());
	}

	protected void setCursor(Cursor cursor) {
		getCursorController().setCursor(cursor == null ? getDefaultCursor() : cursor);
	}

	private AmstradMonitorCursorController getCursorController() {
		return getAmstradPc().getMonitor().getCursorController();
	}

	protected AmstradSettings getUserSettings() {
		return getAmstradContext().getUserSettings();
	}

	protected AmstradSystemSettings getSystemSettings() {
		return getAmstradContext().getSystemSettings();
	}

	protected AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	protected Dimension getDisplaySize() {
		return getDisplayComponent().getSize();
	}

	protected JComponent getDisplayComponent() {
		return getAmstradPc().getMonitor().getDisplayComponent();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private AmstradKeyboardController getKeyboardController() {
		return keyboardController;
	}

	private void setKeyboardController(AmstradKeyboardController keyboardController) {
		this.keyboardController = keyboardController;
	}

	private Cursor getDisplayComponentInitialCursor() {
		return displayComponentInitialCursor;
	}

	private void setDisplayComponentInitialCursor(Cursor cursor) {
		this.displayComponentInitialCursor = cursor;
	}

	private boolean isCatchKeyboardEvents() {
		return catchKeyboardEvents;
	}

	private void setCatchKeyboardEvents(boolean catchKeyboardEvents) {
		this.catchKeyboardEvents = catchKeyboardEvents;
	}

	@Override
	public boolean isRestoreMonitorSettingsOnDispose() {
		return restoreMonitorSettingsOnDispose;
	}

	public void setRestoreMonitorSettingsOnDispose(boolean restore) {
		this.restoreMonitorSettingsOnDispose = restore;
	}

	@Override
	public boolean isAutoPauseResume() {
		return autoPauseResume;
	}

	public void setAutoPauseResume(boolean autoPauseResume) {
		this.autoPauseResume = autoPauseResume;
	}

}