package org.maia.amstrad.gui.overlay;

import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class AutotypeDisplayOverlay extends AbstractDisplayOverlay {

	public static boolean DEFAULT_SHOW_AUTOTYPE = true;

	public AutotypeDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		if (getAmstracPc().getKeyboard().isAutotyping()) {
			if (isShowAutotypeEnabled() && !offscreenImage
					&& !getAmstracPc().getMonitor().isAlternativeDisplaySourceShowing()) {
				ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.autotypeOverlayIcon
						: UIResources.autotypeSmallOverlayIcon;
				drawIconTopLeft(icon, displayView, displayBounds, monitorInsets);
			}
		}
	}

	private boolean isShowAutotypeEnabled() {
		if (isAmstradSystemSetup()) {
			return getAmstradSystem().getCurrentScreen().isShowAutotype();
		} else {
			return DEFAULT_SHOW_AUTOTYPE;
		}
	}

}