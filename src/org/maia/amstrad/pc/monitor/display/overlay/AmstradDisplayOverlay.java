package org.maia.amstrad.pc.monitor.display.overlay;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;

import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public interface AmstradDisplayOverlay {

	void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext);

	void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, AmstradGraphicsContext graphicsContext);

	void dispose(JComponent displayComponent);

}