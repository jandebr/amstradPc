package org.maia.amstrad.gui.overlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;
import org.maia.amstrad.pc.tape.AmstradTape;

public class TapeDisplayOverlay extends AbstractDisplayOverlay {

	public static boolean DEFAULT_SHOW_TAPE_ACTIVITY = false;

	private static Color colorRead = AmstradSystemColors.getSystemColors(AmstradMonitorMode.COLOR).getColor(22);

	private static Color colorWrite = AmstradSystemColors.getSystemColors(AmstradMonitorMode.COLOR).getColor(16);

	private Font labelFont;

	public TapeDisplayOverlay(AmstradPc amstracPc) {
		super(amstracPc);
	}

	@Override
	public void renderOntoDisplay(AmstradDisplayView displayView, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		AmstradTape tape = getAmstracPc().getTape();
		if (tape.isActive() && !tape.isSuppressMessages() && isTapeActivityShowEnabled() && !offscreenImage) {
			String filename = tape.getFilenameAtTapeHead();
			if (filename != null) {
				// tape icon
				ImageIcon icon = isLargeDisplay(displayBounds) ? UIResources.tapeOverlayIcon
						: UIResources.tapeSmallOverlayIcon;
				Rectangle iconBounds = drawIconTopLeft(icon, displayView, displayBounds, monitorInsets);
				int x0 = iconBounds.x + iconBounds.width + 3;
				int yc = iconBounds.y + iconBounds.height / 2;
				// activity icon & label
				String label = filename;
				Font labelFont = getLabelFont(graphicsContext);
				FontMetrics fm = displayView.getFontMetrics(labelFont);
				int labelWidth = fm.stringWidth(label);
				int labelOffset = 24;
				int labelBaseline = 10 + (fm.getAscent() - fm.getDescent()) / 2 + 1;
				Graphics2D g = displayView.createDisplayViewport(x0, yc - 10, labelOffset + labelWidth, 22);
				if (tape.isWriting()) {
					drawIcon(UIResources.tapeWriteOverlayIcon, 0, 0, g);
					g.setColor(colorWrite);
				} else {
					drawIcon(UIResources.tapeReadOverlayIcon, 0, 0, g);
					g.setColor(colorRead);
				}
				g.setFont(labelFont);
				g.drawString(label, labelOffset, labelBaseline);
				g.dispose();
			}
		}
	}

	private boolean isTapeActivityShowEnabled() {
		if (isAmstradSystemSetup()) {
			return getSystemSettings().isTapeActivityShown();
		} else {
			return DEFAULT_SHOW_TAPE_ACTIVITY;
		}
	}

	private Font getLabelFont(AmstradGraphicsContext graphicsContext) {
		if (labelFont == null) {
			labelFont = graphicsContext.getSystemFont().deriveFont(8f);
		}
		return labelFont;
	}

}