package org.maia.amstrad.pc.monitor.display.source;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public interface AmstradAlternativeDisplaySource {

	void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext,
			AmstradKeyboardController keyboardController);

	void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext);

	void dispose(JComponent displayComponent);

	boolean isRestoreMonitorSettingsOnDispose();

}