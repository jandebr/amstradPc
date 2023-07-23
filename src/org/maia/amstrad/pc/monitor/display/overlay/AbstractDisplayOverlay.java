package org.maia.amstrad.pc.monitor.display.overlay;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public abstract class AbstractDisplayOverlay implements AmstradDisplayOverlay {

	private AmstradPc amstracPc;

	private Rectangle iconBounds;

	private double iconOutsideInnerAreaRatio;

	private static double DEFAULT_ICON_OUTSIDE_INNER_AREA_RATIO = 0.3;

	protected AbstractDisplayOverlay(AmstradPc amstracPc) {
		this.amstracPc = amstracPc;
		this.iconBounds = new Rectangle();
		this.iconOutsideInnerAreaRatio = DEFAULT_ICON_OUTSIDE_INNER_AREA_RATIO;
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
		// Subclasses to override
	}

	@Override
	public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds, Insets monitorInsets,
			boolean offscreenImage, AmstradGraphicsContext graphicsContext) {
		// Subclasses to override
	}

	@Override
	public void dispose(JComponent displayComponent) {
		// Subclasses to override
	}

	protected Rectangle drawIconTopLeft(ImageIcon icon, Graphics2D display, Rectangle displayBounds,
			Insets monitorInsets) {
		int x = computeLeftMarginForIcon(icon, monitorInsets);
		int y = computeTopMarginForIcon(icon, monitorInsets);
		drawIcon(icon, x, y, display);
		iconBounds.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
		return iconBounds;
	}

	protected Rectangle drawIconTopRight(ImageIcon icon, Graphics2D display, Rectangle displayBounds,
			Insets monitorInsets) {
		int x = displayBounds.width - computeRightMarginForIcon(icon, monitorInsets) - icon.getIconWidth();
		int y = computeTopMarginForIcon(icon, monitorInsets);
		drawIcon(icon, x, y, display);
		iconBounds.setBounds(x, y, icon.getIconWidth(), icon.getIconHeight());
		return iconBounds;
	}

	protected void drawIcon(ImageIcon icon, int x, int y, Graphics2D display) {
		display.drawImage(icon.getImage(), x, y, null);
	}

	private int computeLeftMarginForIcon(ImageIcon icon, Insets monitorInsets) {
		int dx = (int) Math.round(icon.getIconWidth() * iconOutsideInnerAreaRatio);
		return monitorInsets.left - dx;
	}

	private int computeRightMarginForIcon(ImageIcon icon, Insets monitorInsets) {
		int dx = (int) Math.round(icon.getIconWidth() * iconOutsideInnerAreaRatio);
		return monitorInsets.right - dx;
	}

	private int computeTopMarginForIcon(ImageIcon icon, Insets monitorInsets) {
		int dy = (int) Math.round(icon.getIconHeight() * iconOutsideInnerAreaRatio);
		return monitorInsets.top - dy;
	}

	protected boolean isLargeDisplay(Rectangle displayBounds) {
		return displayBounds.height >= 540;
	}

	protected AmstradMode getMode() {
		return getAmstradContext().getMode();
	}

	protected AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	public AmstradPc getAmstracPc() {
		return amstracPc;
	}

}