package org.maia.amstrad.pc.monitor.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public abstract class AmstradDisplayCanvas {

	private AmstradGraphicsContext graphicsContext;

	private AsciiSymbolRenderer asciiSymbolRenderer;

	private int borderColorIndex;

	private int paperColorIndex;

	private int penColorIndex;

	private Point graphicsOrigin;

	private Point graphicsPosition;

	private Point textPosition;

	private Rectangle reusableTextCursorBounds;

	protected AmstradDisplayCanvas(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
		this.graphicsOrigin = new Point();
		this.graphicsPosition = new Point();
		this.textPosition = new Point();
		this.reusableTextCursorBounds = new Rectangle();
		resetColors();
		resetTextPosition();
		setAsciiSymbolRenderer(new AsciiSymbolRenderer());
	}

	public void resetColors() {
		setBorderColorIndex(getGraphicsContext().getDefaultBorderColorIndex());
		setPaperColorIndex(getGraphicsContext().getDefaultPaperColorIndex());
		setPenColorIndex(getGraphicsContext().getDefaultPenColorIndex());
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
		resetTextPosition();
		return clearRect(0, getHeight() - 1, getWidth(), getHeight());
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

	public AmstradDisplayCanvas move(Point location) {
		return move(location.x, location.y);
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

	public AmstradDisplayCanvas drawRect(Rectangle rect) {
		return drawRect(rect.x, rect.y, rect.width, rect.height);
	}

	public AmstradDisplayCanvas drawRect(int xLeft, int yTop, int width, int height) {
		return plot(xLeft, yTop).drawr(width, 0).drawr(0, -height).drawr(-width, 0).drawr(0, height);
	}

	public AmstradDisplayCanvas clearRect(Rectangle rect) {
		return clearRect(rect.x, rect.y, rect.width, rect.height);
	}

	public AmstradDisplayCanvas clearRect(int xLeft, int yTop, int width, int height) {
		return fillRect(xLeft, yTop, width, height, getPaperColorIndex());
	}

	public AmstradDisplayCanvas fillRect(Rectangle rect) {
		return fillRect(rect.x, rect.y, rect.width, rect.height);
	}

	public AmstradDisplayCanvas fillRect(int xLeft, int yTop, int width, int height) {
		return fillRect(xLeft, yTop, width, height, getPenColorIndex());
	}

	private AmstradDisplayCanvas fillRect(int xLeft, int yTop, int width, int height, int colorIndex) {
		int x1 = projectX(xLeft);
		int y1 = projectY(yTop);
		int x2 = projectX(xLeft + width - 1);
		int y2 = projectY(yTop - height + 1);
		Graphics2D g2 = getGraphics2D();
		g2.setColor(getSystemColor(colorIndex));
		g2.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1) + 1, Math.abs(y2 - y1) + 1);
		return this;
	}

	public AmstradDisplayCanvas drawImage(Image image, int xLeft, int yTop) {
		int x = projectX(xLeft);
		int y = projectY(yTop);
		getGraphics2D().drawImage(image, x, y, null);
		return this;
	}

	public AmstradDisplayCanvas drawImage(Image image, int xLeft, int yTop, int width, int height) {
		int x = projectX(xLeft);
		int y = projectY(yTop);
		getGraphics2D().drawImage(image, x, y, width, height, null);
		return this;
	}

	public AmstradDisplayCanvas symbolAfter() {
		getAsciiSymbolRenderer().reset();
		return this;
	}

	public AmstradDisplayCanvas symbol(int code, int... values) {
		getAsciiSymbolRenderer().customizeSymbol(code, values);
		return this;
	}

	public AmstradDisplayCanvas locate(int cursorX, int cursorY) {
		if (cursorX < 1 || cursorX > getGraphicsContext().getTextColumns() || cursorY < 1
				|| cursorY > getGraphicsContext().getTextRows()) {
			throw new IndexOutOfBoundsException("Location out of bounds: " + cursorX + "," + cursorY);
		}
		getTextPosition().setLocation(cursorX, cursorY);
		return this;
	}

	public AmstradDisplayCanvas print(String str) {
		return print(str, false);
	}

	public AmstradDisplayCanvas print(String str, boolean transparentBackground) {
		for (int i = 0; i < str.length(); i++) {
			boolean lastChar = isTextPositionAtEndOfScreen();
			printChr(str.charAt(i), transparentBackground);
			if (lastChar)
				break;
		}
		return this;
	}

	public AmstradDisplayCanvas printChr(int code) {
		return printChr(code, false);
	}

	public AmstradDisplayCanvas printChr(int code, boolean transparentBackground) {
		return printChr((char) code, transparentBackground);
	}

	public AmstradDisplayCanvas printChr(char c) {
		return printChr(c, false);
	}

	public AmstradDisplayCanvas printChr(char c, boolean transparentBackground) {
		printAsciiSymbolAtTextPosition(c, transparentBackground);
		advanceTextPosition();
		return this;
	}

	public AmstradDisplayCanvas drawStrProportional(String str) {
		return drawStrProportional(str, 1.0f);
	}

	public AmstradDisplayCanvas drawStrProportional(String str, float scale) {
		Point p = getGraphicsPosition();
		Font font = getGraphicsContext().getSystemFont();
		if (scale != 1.0f) {
			float fontSize = getWidth() / getGraphicsContext().getTextColumns() * scale;
			font = font.deriveFont(fontSize);
		}
		Graphics2D g2 = getGraphics2D();
		g2.setFont(font);
		g2.setColor(getPenColor());
		g2.drawString(str, projectX(p.x), projectY(p.y) + g2.getFontMetrics().getAscent());
		return mover(g2.getFontMetrics().stringWidth(str), 0);
	}

	public AmstradDisplayCanvas drawStrMonospaced(String str) {
		for (int i = 0; i < str.length(); i++) {
			drawChrMonospaced(str.charAt(i));
		}
		return this;
	}

	public AmstradDisplayCanvas drawChrMonospaced(int code) {
		return drawChrMonospaced((char) code);
	}

	public AmstradDisplayCanvas drawChrMonospaced(char c) {
		printAsciiSymbolAtGraphicsPosition(c, true);
		return mover(getWidth() / getGraphicsContext().getTextColumns(), 0);
	}

	private void printAsciiSymbolAtTextPosition(char c, boolean transparentBackground) {
		Point p = getTextPosition();
		Rectangle bounds = getTextCursorBoundsOnGraphics2D(p.x, p.y, getReusableTextCursorBounds());
		printAsciiSymbolInBounds(c, bounds, transparentBackground);
	}

	private void printAsciiSymbolAtGraphicsPosition(char c, boolean transparentBackground) {
		Point p = getGraphicsPosition();
		int charWidth = getWidth() / getGraphicsContext().getTextColumns();
		int charHeight = getHeight() / getGraphicsContext().getTextRows();
		Rectangle bounds = new Rectangle(projectX(p.x), projectY(p.y), charWidth, charHeight);
		printAsciiSymbolInBounds(c, bounds, transparentBackground);
	}

	private void printAsciiSymbolInBounds(char c, Rectangle bounds, boolean transparentBackground) {
		int scale = (int) Math.round(getWidth() / getGraphicsContext().getTextColumns() / 8f);
		Graphics2D g2 = getGraphics2D();
		g2.setBackground(transparentBackground ? null : getPaperColor());
		g2.setColor(getPenColor());
		getAsciiSymbolRenderer().printSymbol(c, bounds.x, bounds.y, scale, g2);
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
		// Subclasses may override this method to apply additional transformations
		return getGraphicsOrigin().x + x;
	}

	protected int projectY(int y) {
		// Subclasses may override this method to apply additional transformations
		return getGraphicsOrigin().y + y;
	}

	protected abstract Rectangle getTextCursorBoundsOnGraphics2D(int cursorX, int cursorY, Rectangle returnValue);

	public Rectangle getTextCursorBoundsOnCanvas(int cursorX, int cursorY) {
		int charWidth = getWidth() / getGraphicsContext().getTextColumns();
		int charHeight = getHeight() / getGraphicsContext().getTextRows();
		int xLeft = (cursorX - 1) * charWidth;
		int yTop = getHeight() - 1 - (cursorY - 1) * charHeight;
		return new Rectangle(xLeft, yTop, charWidth, charHeight);
	}

	public Rectangle getTextAreaBoundsOnCanvas(int textAreaX1, int textAreaY1, int textAreaX2, int textAreaY2) {
		Rectangle r1 = getTextCursorBoundsOnCanvas(textAreaX1, textAreaY1);
		Rectangle r2 = getTextCursorBoundsOnCanvas(textAreaX2, textAreaY2);
		int xLeft = Math.min(r1.x, r2.x);
		int xRight = Math.max(r1.x + r1.width - 1, r2.x + r2.width - 1);
		int yTop = Math.max(r1.y, r2.y);
		int yBottom = Math.min(r1.y - r1.height + 1, r2.y - r2.height + 1);
		return new Rectangle(xLeft, yTop, Math.abs(xRight - xLeft) + 1, Math.abs(yTop - yBottom) + 1);
	}

	public Dimension getTextAreaSizeOnCanvas(int charsWide, int charsHigh) {
		int charWidth = getWidth() / getGraphicsContext().getTextColumns();
		int charHeight = getHeight() / getGraphicsContext().getTextRows();
		return new Dimension(charsWide * charWidth, charsHigh * charHeight);
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

	public int getBorderColorIndex() {
		return borderColorIndex;
	}

	private void setBorderColorIndex(int borderColorIndex) {
		this.borderColorIndex = borderColorIndex;
	}

	public int getPaperColorIndex() {
		return paperColorIndex;
	}

	private void setPaperColorIndex(int paperColorIndex) {
		this.paperColorIndex = paperColorIndex;
	}

	public int getPenColorIndex() {
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

	private Rectangle getReusableTextCursorBounds() {
		return reusableTextCursorBounds;
	}

	private AsciiSymbolRenderer getAsciiSymbolRenderer() {
		return asciiSymbolRenderer;
	}

	private void setAsciiSymbolRenderer(AsciiSymbolRenderer renderer) {
		this.asciiSymbolRenderer = renderer;
	}

	private static class AsciiSymbolRenderer {

		private BufferedImage symbolChart;

		private Raster symbolChartRaster;

		private boolean customSymbol32;

		public AsciiSymbolRenderer() {
			reset();
		}

		public void reset() {
			loadSystemSymbolChart();
		}

		private void loadSystemSymbolChart() {
			try {
				InputStream in = getClass().getResourceAsStream("image/amstrad-ascii.png");
				symbolChart = ImageIO.read(in);
				symbolChartRaster = symbolChart.getRaster();
				customSymbol32 = false;
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void printSymbol(int code, int x0, int y0, int scale, Graphics2D g2) {
			if (g2.getBackground() != null) {
				// fill background
				Color fg = g2.getColor();
				g2.setColor(g2.getBackground());
				g2.fillRect(x0, y0, 8 * scale, 8 * scale);
				g2.setColor(fg);
			}
			if ((code > 32 && code <= 255) || (code == 32 && customSymbol32)) {
				Raster raster = getSymbolChartRaster();
				int chartX0 = 8 * ((code - 32) % 40);
				int chartY0 = 8 * ((code - 32) / 40);
				for (int yi = 0; yi < 8; yi++) {
					int chartY = chartY0 + yi;
					int y1 = y0 + yi * scale;
					boolean inside = false;
					int insideX = 0;
					for (int xi = 0; xi < 8; xi++) {
						int chartX = chartX0 + xi;
						boolean chartOn = raster.getSample(chartX, chartY, 0) != 0;
						if (!inside && chartOn) {
							inside = true;
							insideX = xi;
						} else if (inside && !chartOn) {
							inside = false;
							g2.fillRect(x0 + insideX * scale, y1, scale * (xi - insideX), scale);
						}
					}
					if (inside) {
						g2.fillRect(x0 + insideX * scale, y1, scale * (8 - insideX), scale);
					}
				}
			}
		}

		public void customizeSymbol(int code, int... values) {
			if (code >= 32 && code <= 255) {
				int chartX0 = 8 * ((code - 32) % 40);
				int chartY0 = 8 * ((code - 32) / 40);
				BufferedImage chart = getSymbolChart();
				Graphics2D g2 = chart.createGraphics();
				g2.setColor(Color.BLACK);
				g2.fillRect(chartX0, chartY0, 8, 8);
				g2.setColor(Color.WHITE);
				for (int i = 0; i < Math.min(values.length, 8); i++) {
					int chartY = chartY0 + i;
					int value = values[i];
					if (value > 0 && value <= 255) {
						int j = 0;
						int bit = 128;
						while (value > 0 && j < 8) {
							if (value >= bit) {
								value -= bit;
								int chartX = chartX0 + j;
								g2.drawLine(chartX, chartY, chartX, chartY);
							}
							bit /= 2;
							j++;
						}
					}
				}
				g2.dispose();
				if (code == 32)
					customSymbol32 = true;
			}
		}

		private BufferedImage getSymbolChart() {
			return symbolChart;
		}

		private Raster getSymbolChartRaster() {
			return symbolChartRaster;
		}

	}

}