package org.maia.amstrad.pc.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class AmstradDisplayCanvas {

	private AmstradGraphicsContext graphicsContext;

	private Color borderColor;

	private Color paperColor;

	private Color penColor;

	private Point graphicsCursor;

	protected AmstradDisplayCanvas(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
		this.graphicsCursor = new Point();
		resetColors();
	}

	public void resetColors() {
		AmstradSystemColors systemColors = getGraphicsContext().getSystemColors();
		setBorderColor(systemColors.getDefaultBorderColor());
		setPaperColor(systemColors.getDefaultPaperColor());
		setPenColor(systemColors.getDefaultPenColor());
	}

	public AmstradDisplayCanvas border(int colorIndex) {
		border(getSystemColor(colorIndex));
		return this;
	}

	public AmstradDisplayCanvas border(Color color) {
		setBorderColor(color);
		return this;
	}

	public AmstradDisplayCanvas paper(int colorIndex) {
		paper(getSystemColor(colorIndex));
		return this;
	}

	public AmstradDisplayCanvas paper(Color color) {
		setPaperColor(color);
		return this;
	}

	public AmstradDisplayCanvas pen(int colorIndex) {
		pen(getSystemColor(colorIndex));
		return this;
	}

	public AmstradDisplayCanvas pen(Color color) {
		setPenColor(color);
		return this;
	}

	public AmstradDisplayCanvas cls() {
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getPaperColor());
		g2.fillRect(0, 0, getWidth(), getHeight());
		return this;
	}

	public AmstradDisplayCanvas move(int x, int y) {
		getGraphicsCursor().setLocation(x, y);
		return this;
	}

	public AmstradDisplayCanvas mover(int rx, int ry) {
		Point c = getGraphicsCursor();
		return move(c.x + rx, c.y + ry);
	}

	public AmstradDisplayCanvas draw(int x, int y) {
		Point c = getGraphicsCursor();
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getPenColor());
		g2.drawLine(c.x, c.y, x, y);
		return move(x, y);
	}

	public AmstradDisplayCanvas drawr(int rx, int ry) {
		Point c = getGraphicsCursor();
		return draw(c.x + rx, c.y + ry);
	}

	public AmstradDisplayCanvas plot(int x, int y) {
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getPenColor());
		g2.drawLine(x, y, x, y);
		return move(x, y);
	}

	public AmstradDisplayCanvas plotr(int rx, int ry) {
		Point c = getGraphicsCursor();
		return plot(c.x + rx, c.y + ry);
	}

	public Color getSystemColor(int colorIndex) {
		return getGraphicsContext().getSystemColors().getColor(colorIndex);
	}

	public int getWidth() {
		return getGraphicsContext().getDisplayCanvasResolution().width;
	}

	public int getHeight() {
		return getGraphicsContext().getDisplayCanvasResolution().height;
	}

	public AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

	protected abstract Graphics2D getGraphics2D();

	public Color getBorderColor() {
		return borderColor;
	}

	private void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}

	public Color getPaperColor() {
		return paperColor;
	}

	private void setPaperColor(Color paperColor) {
		this.paperColor = paperColor;
	}

	public Color getPenColor() {
		return penColor;
	}

	private void setPenColor(Color penColor) {
		this.penColor = penColor;
	}

	private Point getGraphicsCursor() {
		return graphicsCursor;
	}

}