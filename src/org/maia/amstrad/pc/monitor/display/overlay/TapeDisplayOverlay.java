package org.maia.amstrad.pc.monitor.display.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;
import org.maia.amstrad.pc.tape.AmstradTape;

public class TapeDisplayOverlay extends AbstractDisplayOverlay {

	private static Color colorRead = AmstradSystemColors.getSystemColors(AmstradMonitorMode.COLOR).getColor(22);

	private static Color colorWrite = AmstradSystemColors.getSystemColors(AmstradMonitorMode.COLOR).getColor(16);

	private Font labelFont;

	public TapeDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, boolean offscreenImage,
			AmstradGraphicsContext graphicsContext) {
		AmstradTape tape = getAmstracPc().getTape();
		if (tape.isActive() && !offscreenImage) {
			ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.tapeOverlayIcon
					: UIResources.tapeSmallOverlayIcon;
			Rectangle iconBounds = drawIconTopLeft(icon, display, displayBounds);
			int x1 = iconBounds.x + iconBounds.width - 1;
			int yc = iconBounds.y + iconBounds.height / 2;
			if (tape.isWriting()) {
				drawIcon(UIResources.tapeWriteOverlayIcon, x1 + 4, yc - 10, display);
				display.setColor(colorWrite);
			} else {
				drawIcon(UIResources.tapeReadOverlayIcon, x1 + 4, yc - 10, display);
				display.setColor(colorRead);
			}
			display.setFont(getLabelFont(graphicsContext));
			display.drawString(tape.getFilenameAtTapeHead(), x1 + 28, yc + 5);
		}
	}

	private Font getLabelFont(AmstradGraphicsContext graphicsContext) {
		if (labelFont == null) {
			labelFont = graphicsContext.getSystemFont().deriveFont(8f);
		}
		return labelFont;
	}

}