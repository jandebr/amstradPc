package org.maia.amstrad.pc.display;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.maia.amstrad.pc.basic.BasicRuntime;

public abstract class AmstradDisplayCanvas {

	private AmstradGraphicsContext graphicsContext;

	private AsciiSymbolRenderer asciiSymbolRenderer;

	private int borderColorIndex;

	private int paperColorIndex;

	private int penColorIndex;

	private Point graphicsOrigin;

	private Point graphicsPosition;

	private Point textPosition;

	protected AmstradDisplayCanvas(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
		this.graphicsOrigin = new Point();
		this.graphicsPosition = new Point();
		this.textPosition = new Point();
		resetColors();
		resetTextPosition();
		setAsciiSymbolRenderer(new AsciiSymbolRenderer());
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

	public AmstradDisplayCanvas origin() {
		return origin(0, 0);
	}

	public AmstradDisplayCanvas origin(int x, int y) {
		Point o = getGraphicsOrigin();
		Point p = getGraphicsPosition();
		getGraphicsPosition().setLocation(o.x + p.x - x, o.y + p.y - y);
		getGraphicsOrigin().setLocation(x, y);
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

	public AmstradDisplayCanvas clearRect(int xLower, int yLower, int width, int height) {
		return fillRect(xLower, yLower, width, height, getPaperColorIndex());
	}

	public AmstradDisplayCanvas fillRect(int xLower, int yLower, int width, int height, int colorIndex) {
		int x1 = projectX(xLower);
		int y1 = projectY(yLower);
		int x2 = projectX(xLower + width - 1);
		int y2 = projectY(yLower - height + 1);
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getSystemColor(colorIndex));
		g2.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
		return this;
	}

	public AmstradDisplayCanvas symbolAfter() {
		getAsciiSymbolRenderer().reset();
		return this;
	}

	public AmstradDisplayCanvas symbol(int code, byte... values) {
		// TODO
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
			boolean lastChar = isTextPositionAtEndOfScreen();
			print(str.charAt(i));
			if (lastChar)
				break;
		}
		return this;
	}

	public AmstradDisplayCanvas printchr(int code) {
		return print((char) code);
	}

	public AmstradDisplayCanvas print(char c) {
		printAsciiSymbol(c);
		advanceTextPosition();
		return this;
	}

	private void printAsciiSymbol(char c) {
		Point p = getTextPosition();
		Rectangle bounds = getTextCursorBoundsOnGraphics2D(p.x, p.y);
		Graphics2D g2 = (Graphics2D) getGraphics2D().create(bounds.x, bounds.y, bounds.width, bounds.height);
		double scale = getWidth() / getGraphicsContext().getTextColumns() / 8.0;
		g2.scale(scale, scale);
		g2.setBackground(getPaperColor());
		g2.setColor(getPenColor());
		getAsciiSymbolRenderer().printSymbol(c, g2);
		g2.dispose();
	}

	private void advanceTextPosition() {
		if (!isTextPositionAtEndOfScreen()) {
			Point p = getTextPosition();
			if (p.x < getGraphicsContext().getTextColumns()) {
				p.setLocation(p.x + 1, p.y);
			} else {
				p.setLocation(1, p.y + 1);
			}
		}
	}

	private boolean isTextPositionAtEndOfScreen() {
		Point p = getTextPosition();
		return p.x == getGraphicsContext().getTextColumns() && p.y == getGraphicsContext().getTextRows();
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
		// Subclasses may override this to apply additional transformations
		return getGraphicsOrigin().x + x;
	}

	protected int projectY(int y) {
		// Subclasses may override this to apply additional transformations
		return getGraphicsOrigin().y + y;
	}

	protected abstract Rectangle getTextCursorBoundsOnGraphics2D(int xCursor, int yCursor);

	protected Rectangle getTextCursorBoundsOnCanvas(int xCursor, int yCursor) {
		int charWidth = getWidth() / getGraphicsContext().getTextColumns();
		int charHeight = getHeight() / getGraphicsContext().getTextRows();
		int xLeft = (xCursor - 1) * charWidth;
		int yTop = getHeight() - 1 - (yCursor - 1) * charHeight;
		return new Rectangle(xLeft, yTop, charWidth, charHeight);
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

	private Point getGraphicsOrigin() {
		return graphicsOrigin;
	}

	private Point getGraphicsPosition() {
		return graphicsPosition;
	}

	private Point getTextPosition() {
		return textPosition;
	}

	private AsciiSymbolRenderer getAsciiSymbolRenderer() {
		return asciiSymbolRenderer;
	}

	private void setAsciiSymbolRenderer(AsciiSymbolRenderer renderer) {
		this.asciiSymbolRenderer = renderer;
	}

	private static class AsciiSymbolRenderer {

		private BufferedImage symbolChart;

		public AsciiSymbolRenderer() {
			loadSystemSymbolChart();
		}

		public void reset() {
			loadSystemSymbolChart();
		}

		private void loadSystemSymbolChart() {
			try {
				InputStream in = getClass().getResourceAsStream("image/amstrad-ascii.png");
				symbolChart = ImageIO.read(in);
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void printSymbol(int code, Graphics2D g2) {
			if (g2.getBackground() != null) {
				g2.clearRect(0, 0, 8, 8);
			}
			if (code >= 32 && code <= 255) {
				BufferedImage chart = getSymbolChart();
				int chartX0 = 8 * ((code - 32) % 40);
				int chartY0 = 8 * ((code - 32) / 40);
				for (int i = 0; i < 8; i++) {
					int chartY = chartY0 + i;
					for (int j = 0; j < 8; j++) {
						int chartX = chartX0 + j;
						if ((chart.getRGB(chartX, chartY) & 0xff) != 0) {
							g2.fillRect(j, i, 1, 1);
						}
					}
				}
			}
		}

		private BufferedImage getSymbolChart() {
			return symbolChart;
		}

	}

}