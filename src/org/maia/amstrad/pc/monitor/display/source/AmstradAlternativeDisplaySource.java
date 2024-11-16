package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.maia.amstrad.pc.keyboard.KeyEventTarget;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public interface AmstradAlternativeDisplaySource extends KeyEventTarget {

	void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext);

	void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext);

	void dispose(JComponent displayComponent);

	void show();

	void close();

	/**
	 * Tells whether this display source should be rendered on the full available screen size
	 * <p>
	 * When <code>false</code> and in full screen mode, the screen area outside the display bounds is not used for
	 * rendering
	 * </p>
	 */
	boolean isStretchToFullscreen();

	/**
	 * Tells whether the monitor settings should be automatically remembered when this display source is shown and
	 * automatically restored when this display source is closed
	 */
	boolean isRestoreMonitorSettingsOnDispose();

	/**
	 * Tells whether the <em>AmstradPc</em> should be automatically paused when this display source is shown and
	 * automatically resumed when this display source is closed
	 */
	boolean isAutoPauseResume();

	AmstradAlternativeDisplaySourceType getType();

}