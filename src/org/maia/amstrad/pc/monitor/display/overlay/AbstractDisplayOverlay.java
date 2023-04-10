package org.maia.amstrad.pc.monitor.display.overlay;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public abstract class AbstractDisplayOverlay implements AmstradDisplayOverlay {

	private AmstradPc amstracPc;

	private Rectangle iconBounds;

	protected AbstractDisplayOverlay(AmstradPc amstracPc) {
		this.amstracPc = amstracPc;
		this.iconBounds = new Rectangle();
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		// Subclasses to override
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, boolean offscreenImage,
			AmstradGraphicsContext graphicsContext) {
		// Subclasses to override
	}

	@Override
	public void dispose(JComponent displayComponent) {
		// Subclasses to override
	}

	protected Rectangle drawIconTopLeft(ImageIcon icon, Graphics2D display, Rectangle displayBounds) {
		int x = computeXmarginForIcon(icon, displayBounds);
		int y = computeYmarginForIcon(icon, displayBounds);
		drawIcon(icon, x, y, display);
		iconBounds.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
		return iconBounds;
	}

	protected Rectangle drawIconTopRight(ImageIcon icon, Graphics2D display, Rectangle displayBounds) {
		int x = displayBounds.width - computeXmarginForIcon(icon, displayBounds) - icon.getIconWidth();
		int y = computeYmarginForIcon(icon, displayBounds);
		drawIcon(icon, x, y, display);
		iconBounds.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
		return iconBounds;
	}

	protected void drawIcon(ImageIcon icon, int x, int y, Graphics2D display) {
		display.drawImage(icon.getImage(), x, y, null);
	}

	private int computeXmarginForIcon(ImageIcon icon, Rectangle displayBounds) {
		return Math.max(8, (int) Math.ceil(16.0 * Math.pow(displayBounds.getWidth() / 768.0, 1.2)));
	}

	private int computeYmarginForIcon(ImageIcon icon, Rectangle displayBounds) {
		return Math.max(6, (int) Math.ceil(12.0 * Math.pow(displayBounds.getHeight() / 544.0, 1.6)));
	}

	protected boolean isLargeDisplay(Rectangle displayBounds) {
		return displayBounds.height >= 540;
	}

	public AmstradPc getAmstracPc() {
		return amstracPc;
	}

}