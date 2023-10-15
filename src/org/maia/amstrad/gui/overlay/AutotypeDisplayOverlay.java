package org.maia.amstrad.gui.overlay;

import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class AutotypeDisplayOverlay extends AbstractDisplayOverlay {

	public AutotypeDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().getMonitor().isAlternativeDisplaySourceShowing() || offscreenImage)
			return;
		if (getAmstracPc().getKeyboard().isAutotyping()) {
			ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.autotypeOverlayIcon
					: UIResources.autotypeSmallOverlayIcon;
			drawIconTopLeft(icon, displayView, displayBounds, monitorInsets);
		}
	}

}