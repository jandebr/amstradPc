package org.maia.amstrad.gui.overlay;

import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class TurboDisplayOverlay extends AbstractDisplayOverlay {

	public static boolean DEFAULT_SHOW_TURBO = true;

	public TurboDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().isTurboMode() && !getAmstracPc().isPaused()) {
			if (isShowTurboEnabled() && !offscreenImage) {
				ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.turboOverlayIcon
						: UIResources.turboSmallOverlayIcon;
				drawIconTopRight(icon, displayView, displayBounds, monitorInsets);
			}
		}
	}

	private boolean isShowTurboEnabled() {
		if (isAmstradSystemSetup()) {
			return getAmstradSystem().getCurrentScreen().isShowTurbo();
		} else {
			return DEFAULT_SHOW_TURBO;
		}
	}

}