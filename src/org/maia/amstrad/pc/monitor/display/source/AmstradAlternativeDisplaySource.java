package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.maia.amstrad.pc.joystick.AmstradJoystickCommand;
import org.maia.amstrad.pc.joystick.AmstradJoystickEvent;
import org.maia.amstrad.pc.keyboard.KeyEventTarget;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public interface AmstradAlternativeDisplaySource extends KeyEventTarget {

	void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext);

	void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext);

	void dispose(JComponent displayComponent);

	void show();

	void close();

	/**
	 * Whether this display source should be rendered on the full available screen size
	 * <p>
	 * When <code>false</code> and in full screen mode, the screen area outside the display bounds is not used for
	 * rendering
	 * </p>
	 */
	boolean isStretchToFullscreen();

	/**
	 * Whether the monitor settings should be automatically remembered when this display source is shown and
	 * automatically restored when this display source is closed
	 */
	boolean isRestoreMonitorSettingsOnDispose();

	/**
	 * Whether the <em>AmstradPc</em> should be automatically paused when this display source is shown and automatically
	 * resumed when this display source is closed
	 */
	boolean isAutoPauseResume();

	/**
	 * Whether this display source accepts the given joystick command when fired by <em>autorepeat</em>
	 * <p>
	 * When <code>false</code> the command will not be converted into a <code>KeyEvent</code> and delivered to this
	 * <code>KeyEventTarget</code>
	 * </p>
	 * 
	 * @see AmstradJoystickEvent#isFiredByAutoRepeat()
	 */
	boolean isAutoRepeatAccepted(AmstradJoystickCommand command);

	AmstradAlternativeDisplaySourceType getType();

}