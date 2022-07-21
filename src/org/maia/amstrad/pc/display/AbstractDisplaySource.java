package org.maia.amstrad.pc.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;

public abstract class AbstractDisplaySource implements AmstradAlternativeDisplaySource {

	protected AbstractDisplaySource() {
	}

	@Override
	public void init(JComponent displayComponent, AmstradGraphicsContext graphicsContext) {
	}

	@Override
	public void renderOntoDisplay(Graphics2D g2, Rectangle displayBounds, AmstradGraphicsContext graphicsContext) {
		Insets borderInsets = getBorderInsets(displayBounds);
		AmstradSystemColors colors = graphicsContext.getSystemColors();
		renderBorder(g2, displayBounds, borderInsets, colors.getDefaultBorderInk());
		renderPaper(g2, displayBounds, borderInsets, colors.getDefaultPaperInk());
		Graphics2D g2cs = toAmstradGraphicsCoordinateSystem(g2, displayBounds, borderInsets);
		g2cs.setColor(colors.getDefaultPenInk());
		g2cs.setFont(graphicsContext.getSystemFont().deriveFont(16f));
		renderContent(g2cs, graphicsContext);
		g2cs.dispose();
	}

	protected abstract void renderContent(Graphics2D g2, AmstradGraphicsContext graphicsContext);

	protected void renderPaper(Graphics2D g2, Rectangle displayBounds, Insets borderInsets, Color color) {
		int x0 = displayBounds.x + borderInsets.left;
		int y0 = displayBounds.y + borderInsets.top;
		int width = displayBounds.width - borderInsets.left - borderInsets.right;
		int height = displayBounds.height - borderInsets.top - borderInsets.bottom;
		g2.setColor(color);
		g2.fillRect(x0, y0, width, height);
	}

	protected void renderBorder(Graphics2D g2, Rectangle displayBounds, Insets borderInsets, Color color) {
		int x0 = displayBounds.x;
		int y0 = displayBounds.y;
		int x1 = x0 + displayBounds.width - 1;
		int y1 = y0 + displayBounds.height - 1;
		g2.setColor(color);
		g2.fillRect(x0, y0, displayBounds.width, borderInsets.top);
		g2.fillRect(x0, y0, borderInsets.left, displayBounds.height);
		g2.fillRect(x0, y1 - borderInsets.bottom + 1, displayBounds.width, borderInsets.bottom);
		g2.fillRect(x1 - borderInsets.right + 1, y0, borderInsets.right, displayBounds.height);
	}

	private Insets getBorderInsets(Rectangle displayBounds) {
		double sy = displayBounds.height / 272.0;
		double sx = displayBounds.width / 384.0;
		int top = (int) Math.floor(sy * 40.0);
		int left = (int) Math.floor(sx * 32.0);
		int bottom = displayBounds.height - top - (int) Math.round(sy * 200.0);
		int right = displayBounds.width - left - (int) Math.round(sx * 320.0);
		return new Insets(top, left, bottom, right);
	}

	private Graphics2D toAmstradGraphicsCoordinateSystem(Graphics2D g2, Rectangle displayBounds, Insets borderInsets) {
		int paperWidth = displayBounds.width - borderInsets.left - borderInsets.right;
		int paperHeight = displayBounds.height - borderInsets.top - borderInsets.bottom;
		double scaleX = paperWidth / 640.0;
		double scaleY = paperHeight / 400.0;
		Graphics2D g2cs = (Graphics2D) g2.create();
		g2cs.scale(scaleX, scaleY);
		g2cs.translate(borderInsets.left, borderInsets.top);
		return g2cs;
	}

	@Override
	public void dispose() {
	}

}