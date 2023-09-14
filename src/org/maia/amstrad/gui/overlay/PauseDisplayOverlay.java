package org.maia.amstrad.gui.overlay;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class PauseDisplayOverlay extends AbstractDisplayOverlay {

	public PauseDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().isPaused() && !getAmstracPc().getTape().isActive() && !offscreenImage) {
			ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.pauseOverlayIcon
					: UIResources.pauseSmallOverlayIcon;
			drawIconTopRight(icon, display, displayBounds, monitorInsets);
		}
	}

}