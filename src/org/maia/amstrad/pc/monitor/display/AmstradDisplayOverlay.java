package org.maia.amstrad.pc.monitor.display;

import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;

public interface AmstradDisplayOverlay {

	void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext);

	void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext);

	void dispose(JComponent displayComponent);

}