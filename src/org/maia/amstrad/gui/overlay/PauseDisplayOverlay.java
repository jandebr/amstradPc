package org.maia.amstrad.gui.overlay;

import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class PauseDisplayOverlay extends AbstractDisplayOverlay {

	public static boolean DEFAULT_SHOW_PAUSE = true;

	public PauseDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().isPaused()) {
			if (isShowPauseEnabled() && !getAmstracPc().getTape().isActive() && !offscreenImage) {
				ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.pauseOverlayIcon
						: UIResources.pauseSmallOverlayIcon;
				drawIconTopRight(icon, displayView, displayBounds, monitorInsets);
			}
		}
	}

	private boolean isShowPauseEnabled() {
		if (isAmstradSystemSetup()) {
			return getAmstradSystem().getCurrentScreen().isShowPause();
		} else {
			return DEFAULT_SHOW_PAUSE;
		}
	}

}