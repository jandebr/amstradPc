package org.maia.amstrad.pc.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import org.maia.amstrad.pc.basic.BasicRuntime;

public abstract class AmstradDisplayCanvas {

	private AmstradGraphicsContext graphicsContext;

	private int borderColorIndex;

	private int paperColorIndex;

	private int penColorIndex;

	private Point graphicsPosition;

	private Point textPosition;

	protected AmstradDisplayCanvas(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
		this.graphicsPosition = new Point();
		this.textPosition = new Point();
		resetColors();
		resetTextPosition();
	}

	public void resetColors() {
		setBorderColorIndex(BasicRuntime.DEFAULT_BORDER_COLOR_INDEX);
		setPaperColorIndex(BasicRuntime.DEFAULT_PAPER_COLOR_INDEX);
		setPenColorIndex(BasicRuntime.DEFAULT_PEN_COLOR_INDEX);
	}

	private void resetTextPosition() {
		getTextPosition().setLocation(1, 1);
	}

	public AmstradDisplayCanvas border(int colorIndex) {
		setBorderColorIndex(colorIndex);
		return this;
	}

	public AmstradDisplayCanvas paper(int colorIndex) {
		setPaperColorIndex(colorIndex);
		return this;
	}

	public AmstradDisplayCanvas pen(int colorIndex) {
		setPenColorIndex(colorIndex);
		return this;
	}

	public AmstradDisplayCanvas cls() {
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getPaperColor());
		g2.fillRect(0, 0, getWidth(), getHeight());
		resetTextPosition();
		return this;
	}

	public AmstradDisplayCanvas move(int x, int y) {
		getGraphicsPosition().setLocation(x, y);
		return this;
	}

	public AmstradDisplayCanvas mover(int rx, int ry) {
		Point p = getGraphicsPosition();
		return move(p.x + rx, p.y + ry);
	}

	public AmstradDisplayCanvas draw(int x, int y) {
		Point p = getGraphicsPosition();
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getPenColor());
		g2.drawLine(projectX(p.x), projectY(p.y), projectX(x), projectY(y));
		return move(x, y);
	}

	public AmstradDisplayCanvas drawr(int rx, int ry) {
		Point p = getGraphicsPosition();
		return draw(p.x + rx, p.y + ry);
	}

	public AmstradDisplayCanvas plot(int x, int y) {
		int px = projectX(x);
		int py = projectY(y);
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getPenColor());
		g2.drawLine(px, py, px, py);
		return move(x, y);
	}

	public AmstradDisplayCanvas plotr(int rx, int ry) {
		Point p = getGraphicsPosition();
		return plot(p.x + rx, p.y + ry);
	}

	public AmstradDisplayCanvas clearRect(int x, int y, int width, int height) {
		return fillRect(x, y, width, height, getPaperColorIndex());
	}

	public AmstradDisplayCanvas fillRect(int x, int y, int width, int height, int colorIndex) {
		int x1 = projectX(x);
		int y1 = projectY(y);
		int x2 = projectX(x + width - 1);
		int y2 = projectY(y - height + 1);
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getSystemColor(colorIndex));
		g2.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
		return this;
	}

	public AmstradDisplayCanvas locate(int x, int y) {
		if (x < 1 || x > getGraphicsContext().getTextColumns() || y < 1 || y > getGraphicsContext().getTextRows()) {
			throw new IndexOutOfBoundsException("Location out of bounds: " + x + "," + y);
		}
		getTextPosition().setLocation(x, y);
		return this;
	}

	public AmstradDisplayCanvas print(String str) {
		for (int i = 0; i < str.length(); i++) {
			print(str.charAt(i));
		}
		return this;
	}

	public AmstradDisplayCanvas print(char c) {
		Point p = getTextPosition();
		int columns = getGraphicsContext().getTextColumns();
		int rows = getGraphicsContext().getTextRows();
		int charWidth = getWidth() / columns;
		int charHeight = getHeight() / rows;
		int x = (p.x - 1) * charWidth;
		int y = (rows - p.y) * charHeight;
		clearRect(x, y + charHeight - 1, charWidth, charHeight);
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getPenColor());
		g2.setFont(getGraphicsContext().getSystemFont());
		g2.drawString(String.valueOf(c), projectX(x), projectY(y) - 1);
		// update text position
		if (p.x < columns) {
			p.setLocation(p.x + 1, p.y);
		} else {
			p.setLocation(1, Math.min(p.y + 1, rows));
		}
		return this;
	}

	public int getWidth() {
		return getGraphicsContext().getDisplayCanvasSize().width;
	}

	public int getHeight() {
		return getGraphicsContext().getDisplayCanvasSize().height;
	}

	public AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

	protected abstract Graphics2D getGraphics2D();

	protected int projectX(int x) {
		return x;
	}

	protected int projectY(int y) {
		return getHeight() - 1 - y;
	}

	public Color getBorderColor() {
		return getSystemColor(getBorderColorIndex());
	}

	public Color getPaperColor() {
		return getSystemColor(getPaperColorIndex());
	}

	public Color getPenColor() {
		return getSystemColor(getPenColorIndex());
	}

	public Color getSystemColor(int colorIndex) {
		return getGraphicsContext().getSystemColors().getColor(colorIndex);
	}

	private int getBorderColorIndex() {
		return borderColorIndex;
	}

	private void setBorderColorIndex(int borderColorIndex) {
		this.borderColorIndex = borderColorIndex;
	}

	private int getPaperColorIndex() {
		return paperColorIndex;
	}

	private void setPaperColorIndex(int paperColorIndex) {
		this.paperColorIndex = paperColorIndex;
	}

	private int getPenColorIndex() {
		return penColorIndex;
	}

	private void setPenColorIndex(int penColorIndex) {
		this.penColorIndex = penColorIndex;
	}

	private Point getGraphicsPosition() {
		return graphicsPosition;
	}

	private Point getTextPosition() {
		return textPosition;
	}

}