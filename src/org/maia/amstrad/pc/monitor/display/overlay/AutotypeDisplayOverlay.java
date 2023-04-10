package org.maia.amstrad.pc.monitor.display.overlay;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class AutotypeDisplayOverlay extends AbstractDisplayOverlay {

	public AutotypeDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, boolean offscreenImage,
			AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().getMonitor().isAlternativeDisplaySourceShowing() || offscreenImage)
			return;
		if (getAmstracPc().getKeyboard().isAutotyping()) {
			ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.autotypeOverlayIcon
					: UIResources.autotypeSmallOverlayIcon;
			drawIconTopRight(icon, display, displayBounds);
		}
	}

}