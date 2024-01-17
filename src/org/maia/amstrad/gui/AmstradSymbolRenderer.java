package org.maia.amstrad.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;

public class AmstradSymbolRenderer {

	private SymbolCanvas canvas;

	private float scale = 1f;

	public AmstradSymbolRenderer(AmstradGraphicsContext graphicsContext, Graphics2D graphics2D) {
		this.canvas = new SymbolCanvas(graphicsContext, graphics2D);
	}

	public Graphics2D replaceGraphics2D(Graphics2D newGraphics2D) {
		Graphics2D oldGraphics2D = getCanvas().getGraphics2D();
		getCanvas().setGraphics2D(newGraphics2D);
		return oldGraphics2D;
	}

	public AmstradSymbolRenderer symbolAfter() {
		getCanvas().symbolAfter();
		return this;
	}

	public AmstradSymbolRenderer symbol(int code, int... values) {
		getCanvas().symbol(code, values);
		return this;
	}

	public AmstradSymbolRenderer scale(float scale) {
		setScale(scale);
		return this;
	}

	public AmstradSymbolRenderer color(Color color) {
		getCanvas().setPenColor(color);
		return this;
	}

	public AmstradSymbolRenderer drawStr(final String str, int x, int y) {
		return render(new RenderOp() {

			@Override
			public void render() {
				getCanvas().drawStrMonospaced(str);
			}
		}, x, y);
	}

	public AmstradSymbolRenderer drawChr(final int code, int x, int y) {
		return render(new RenderOp() {

			@Override
			public void render() {
				getCanvas().drawChrMonospaced(code);
			}
		}, x, y);
	}

	public AmstradSymbolRenderer drawChr(final char c, int x, int y) {
		return render(new RenderOp() {

			@Override
			public void render() {
				getCanvas().drawChrMonospaced(c);
			}
		}, x, y);
	}

	private synchronized AmstradSymbolRenderer render(RenderOp op, int x, int y) {
		Graphics2D gOriginal = getCanvas().getGraphics2D();
		Graphics2D g = null;
		float scale = getScale();
		if (scale == 1f) {
			getCanvas().move(x, y);
		} else {
			g = (Graphics2D) gOriginal.create();
			g.translate(x, y);
			g.scale(scale, scale);
			getCanvas().setGraphics2D(g);
			getCanvas().move(0, 0);
		}
		op.render();
		if (g != null) {
			g.dispose();
			getCanvas().setGraphics2D(gOriginal);
		}
		return this;
	}

	private SymbolCanvas getCanvas() {
		return canvas;
	}

	private float getScale() {
		return scale;
	}

	private void setScale(float scale) {
		this.scale = scale;
	}

	private static interface RenderOp {

		void render();

	}

	private static class SymbolCanvas extends AmstradDisplayCanvas {

		private Graphics2D graphics2D;

		private Color penColor;

		public SymbolCanvas(AmstradGraphicsContext graphicsContext, Graphics2D graphics2D) {
			super(graphicsContext);
			this.graphics2D = graphics2D;
			this.penColor = graphics2D != null ? graphics2D.getColor() : Color.BLACK;
		}

		@Override
		public Graphics2D getGraphics2D() {
			return graphics2D;
		}

		public void setGraphics2D(Graphics2D graphics2D) {
			this.graphics2D = graphics2D;
		}

		@Override
		public int getWidth() {
			return getGraphicsContext().getTextColumns() * 8;
		}

		@Override
		public Color getPenColor() {
			return penColor;
		}

		public void setPenColor(Color color) {
			this.penColor = color;
		}

	}

}