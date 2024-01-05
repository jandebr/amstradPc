package org.maia.amstrad.gui.overlay;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayView;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemSettings;

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
	public void dispose(JComponent displayComponent) {
		// Subclasses to override
	}

	protected Color makeColorMoreTransparent(Color baseColor, double transparencyFactor) {
		if (transparencyFactor == 0)
			return baseColor;
		double transparency = 1.0 - baseColor.getAlpha() / 255.0;
		double newTransparency = transparency + (1.0 - transparency) * transparencyFactor;
		int newAlpha = (int) Math.round((1.0 - newTransparency) * 255.0);
		return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), newAlpha);
	}

	protected Rectangle drawIconTopLeft(ImageIcon icon, AmstradDisplayView displayView, Rectangle displayBounds,
			Insets monitorInsets) {
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		int x = displayBounds.x + computeLeftMarginForIcon(icon, monitorInsets);
		int y = displayBounds.y + computeTopMarginForIcon(icon, monitorInsets);
		Graphics2D g = displayView.createDisplayViewport(x, y, width, height);
		drawIcon(icon, 0, 0, g);
		g.dispose();
		iconBounds.setBounds(x, y, width, height);
		return iconBounds;
	}

	protected Rectangle drawIconTopRight(ImageIcon icon, AmstradDisplayView displayView, Rectangle displayBounds,
			Insets monitorInsets) {
		int width = icon.getIconWidth();
		int height = icon.getIconHeight();
		int x = displayBounds.x + displayBounds.width - computeRightMarginForIcon(icon, monitorInsets) - width;
		int y = displayBounds.y + computeTopMarginForIcon(icon, monitorInsets);
		Graphics2D g = displayView.createDisplayViewport(x, y, width, height);
		drawIcon(icon, 0, 0, g);
		g.dispose();
		iconBounds.setBounds(x, y, width, height);
		return iconBounds;
	}

	protected void drawIcon(ImageIcon icon, int x, int y, Graphics2D g) {
		g.drawImage(icon.getImage(), x, y, null);
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

	protected AmstradSystemSettings getSystemSettings() {
		return getAmstradContext().getSystemSettings();
	}

	protected boolean isAmstradSystemSetup() {
		return getAmstradContext().isAmstradSystemSetup();
	}

	protected AmstradSystem getAmstradSystem() {
		return getAmstradContext().getAmstradSystem();
	}

	protected AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	public AmstradPc getAmstracPc() {
		return amstracPc;
	}

}