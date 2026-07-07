package org.maia.amstrad.gui.browser.carousel.animation.item;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.AmstradSymbolRenderer;
import org.maia.amstrad.gui.browser.carousel.item.CarouselProgramItem;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.gui.sprite.SpriteImage;
import org.maia.amstrad.gui.sprite.SpriteImageRLE;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public class CarouselRunProgramRocketAnimation extends CarouselRunProgramAnimation {

	private Rectangle rocketViewBounds;

	private AmstradGraphicsContext graphicsContext;

	private AmstradSymbolRenderer symbolRenderer;

	private Color backgroundColor;

	private Color viewBorderColor;

	private Color rocketTailColor;

	private SpriteColorMap rocketColorMap;

	private SpriteImage rocketImage;

	private String rocketMessage;

	private Rocket rocket;

	public static String DEFAULT_ROCKET_MESSAGE = "RUN";

	public CarouselRunProgramRocketAnimation(CarouselProgramItem item, Rectangle itemCarouselBounds,
			Rectangle rocketViewBounds, AmstradGraphicsContext graphicsContext, Color backgroundColor) {
		super(item, itemCarouselBounds);
		float bri = ColorUtils.getBrightness(backgroundColor);
		this.rocketViewBounds = rocketViewBounds;
		this.graphicsContext = graphicsContext;
		this.backgroundColor = backgroundColor;
		this.viewBorderColor = ColorUtils.adjustBrightness(backgroundColor, bri <= 0.5f ? 0.1f : -0.1f);
		this.rocketTailColor = ColorUtils.adjustBrightness(backgroundColor, bri <= 0.5f ? 1.0f : -1.0f);
		this.rocketColorMap = createRocketColors();
		this.rocketMessage = DEFAULT_ROCKET_MESSAGE;
	}

	private SpriteColorMap createRocketColors() {
		SpriteColorMapImpl colorMap = new SpriteColorMapImpl();
		colorMap.setColor(0, ColorUtils.adjustBrightness(getRocketTailColor(),
				ColorUtils.getBrightness(getRocketTailColor()) <= 0.5f ? 0.5f : -0.5f));
		colorMap.setColor(1, getRocketTailColor());
		colorMap.setColor(2, getBackgroundColor());
		return colorMap;
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		initRocket(displayWidth, displayHeight);
	}

	private void initRocket(int displayWidth, int displayHeight) {
		Rectangle bounds = getRocketViewBounds();
		setRocket(new Rocket(bounds.width, bounds.height));
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		super.renderOntoDisplay(g, displayWidth, displayHeight, elapsedTimeMillis);
		renderRocket(g, elapsedTimeMillis);
		renderViewBorder(g);
	}

	private void renderRocket(Graphics2D g, long elapsedTimeMillis) {
		float unitTime = (elapsedTimeMillis - 200L) / (getMinimumDurationMillis() * 0.5f);
		Rectangle bounds = getRocketViewBounds();
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(bounds.x, bounds.y);
		getRocket().render(g2, unitTime);
		g2.dispose();
	}

	private void renderViewBorder(Graphics2D g) {
		Rectangle bounds = getRocketViewBounds();
		g.setColor(getViewBorderColor());
		g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
	}

	private int getMaximumRocketScaleFactor() {
		return 2;
	}

	public Rectangle getRocketViewBounds() {
		return rocketViewBounds;
	}

	private AmstradSymbolRenderer getSymbolRenderer(Graphics2D g) {
		if (symbolRenderer == null) {
			symbolRenderer = new AmstradSymbolRenderer(getGraphicsContext(), g);
		} else {
			symbolRenderer.replaceGraphics2D(g);
		}
		return symbolRenderer;
	}

	private AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

	private Color getBackgroundColor() {
		return backgroundColor;
	}

	private Color getViewBorderColor() {
		return viewBorderColor;
	}

	private Color getRocketTailColor() {
		return rocketTailColor;
	}

	private SpriteColorMap getRocketColorMap() {
		return rocketColorMap;
	}

	private SpriteImage getRocketImage() {
		if (rocketImage == null) {
			rocketImage = new SpriteImageRLE(41, 19,
					new int[] { -1, 4, 0, 11, -2, -1, 1, 0, 3, 1, 10, 0, 2, -1, 2, 0, 7, -2, 0, 3, 1, 7, 0, 9, 1, 5, 0,
							7, -2, -1, 1, 0, 5, 1, 2, 0, 4, 1, 18, 0, 3, -2, -1, 6, 0, 3, 1, 23, 0, 4, -2, -1, 6, 0, 1,
							1, 14, 0, 5, 1, 6, 0, 1, 1, 2, 0, 3, -2, -1, 5, 0, 1, 1, 14, 0, 1, 2, 5, 0, 1, 1, 4, 0, 2,
							1, 4, 0, 2, -2, -1, 5, 0, 9, 1, 5, 0, 1, 2, 7, 0, 1, 1, 3, 0, 2, 1, 5, 0, 2, -2, -1, 2, 0,
							8, 1, 3, 0, 2, 1, 4, 0, 1, 2, 7, 0, 1, 1, 3, 0, 2, 1, 6, 0, 2, -2, -1, 1, 0, 6, 1, 6, 0, 2,
							1, 4, 0, 1, 2, 7, 0, 1, 1, 3, 0, 2, 1, 6, 0, 2, -2, -1, 5, 0, 9, 1, 5, 0, 1, 2, 7, 0, 1, 1,
							3, 0, 2, 1, 5, 0, 2, -2, -1, 5, 0, 1, 1, 14, 0, 1, 2, 5, 0, 1, 1, 5, 0, 1, 1, 4, 0, 2, -2,
							-1, 5, 0, 2, 1, 14, 0, 5, 1, 6, 0, 1, 1, 2, 0, 3, -2, -1, 6, 0, 2, 1, 24, 0, 3, -2, -1, 4,
							0, 7, 1, 19, 0, 3, -2, 0, 5, 1, 4, 0, 10, 1, 6, 0, 6, -2, 0, 3, 1, 11, 0, 2, -1, 2, 0, 8,
							-2, -1, 2, 0, 4, 1, 6, 0, 3, -2, -1, 6, 0, 6 });
		}
		return rocketImage;
	}

	public String getRocketMessage() {
		return rocketMessage;
	}

	public void setRocketMessage(String msg) {
		this.rocketMessage = msg;
	}

	private Rocket getRocket() {
		return rocket;
	}

	private void setRocket(Rocket rocket) {
		this.rocket = rocket;
	}

	private class Rocket {

		private int viewportWidth;

		private int viewportHeight;

		private int scaleFactor;

		private List<RocketTailSmokeParticle> smokeParticles;

		private long smokeParticlesTimestamp;

		private int smokeParticlesUpdatesPerSecond = 10;

		private Randomizer randomizer;

		public Rocket(int viewportWidth, int viewportHeight) {
			this.viewportWidth = viewportWidth;
			this.viewportHeight = viewportHeight;
			this.scaleFactor = Math.min(
					Math.max(1, (int) Math.floor(Math.sqrt((viewportHeight - 4) / getRocketImage().getHeight()))),
					getMaximumRocketScaleFactor());
			this.smokeParticles = new Vector<RocketTailSmokeParticle>(getSmokeParticlesCount());
			this.randomizer = new Randomizer();
		}

		public void render(Graphics2D g, float unitTime) {
			RocketTailCone cone = RocketTailCone.createCone(this, unitTime);
			updateSmokeParticles(cone);
			int n = getSmokeParticles().size();
			int m = Math.round(n * 0.8f);
			Graphics2D g2 = (Graphics2D) g.create(0, 0, getViewportWidth(), getViewportHeight());
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			renderTailCone(g2, cone);
			renderTailSmokeParticles(g2, 0, m);
			renderMessage(g2, getRocketMessage());
			renderRocket(g2, cone, unitTime);
			renderTailSmokeParticles(g2, m, n);
			g2.dispose();
		}

		private void renderTailCone(Graphics2D g, RocketTailCone cone) {
			Color c1 = getRocketTailColor();
			Color c2 = getBackgroundColor();
			float xcap = cone.getCappedRightX();
			Graphics2D g2 = (Graphics2D) g.create(0, 0, Math.round(xcap), getViewportHeight());
			g2.setPaint(new GradientPaint(cone.getLeftX(), 0f, c1, xcap, 0f, c2));
			g2.fill(cone.getShape());
			g2.dispose();
		}

		private void renderMessage(Graphics2D g, String message) {
			int scale = 2 * getScaleFactor();
			int x = (getViewportWidth() - message.length() * 8 * scale) / 2;
			int y = (getViewportHeight() - 8 * scale) / 2;
			AmstradSymbolRenderer renderer = getSymbolRenderer(g);
			renderer.color(getBackgroundColor());
			renderer.scale(scale);
			renderer.drawStr(message, x, y);
		}

		private void renderRocket(Graphics2D g, RocketTailCone cone, float unitTime) {
			SpriteImage img = getRocketImage();
			int scale = getScaleFactor();
			int x0 = Math.round(cone.getCappedRightX()) - 8;
			int y0 = (getViewportHeight() - img.getHeight() * scale) / 2;
			int dy = Math.round(scale * (float) Math.sin(unitTime * 60.0));
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2.translate(x0, y0 + dy);
			g2.scale(scale, scale);
			img.draw(g2, getRocketColorMap());
			g2.dispose();
		}

		private void renderTailSmokeParticles(Graphics2D g, int indexFrom, int indexTo) {
			for (int i = indexFrom; i < indexTo; i++) {
				RocketTailSmokeParticle particle = getSmokeParticles().get(i);
				int diam = particle.getDiameter();
				int rad = diam / 2;
				g.setColor(particle.getColor());
				g.fillOval(particle.getCenterX() - rad, particle.getCenterY() - rad, diam, diam);
			}
		}

		private void updateSmokeParticles(RocketTailCone cone) {
			long now = System.currentTimeMillis();
			long dt = 1000L / getSmokeParticlesUpdatesPerSecond();
			if (getSmokeParticles().isEmpty() || now >= getSmokeParticlesTimestamp() + dt) {
				refreshSmokeParticles(cone);
				setSmokeParticlesTimestamp(now);
			}
		}

		private void refreshSmokeParticles(RocketTailCone cone) {
			List<RocketTailSmokeParticle> particles = getSmokeParticles();
			particles.clear();
			Randomizer rnd = getRandomizer();
			float x1 = cone.getLeftX();
			float x2 = cone.getCappedRightX();
			int h = getViewportHeight();
			for (int i = 0; i < getSmokeParticlesCount(); i++) {
				float rx = rnd.drawFloatUnitNumber();
				float ry0 = 0.5f + 0.5f * ((float) rnd.drawGaussian() / 5f);
				float ry = ry0 + (rnd.drawFloatUnitNumber() - 0.5f) * (1f - rx) * (1f - rx);
				int cx = Math.round(x1 + rx * (x2 - x1));
				int cy = Math.round(ry * h);
				int diam = 2 + Math.round((1f - rx) * h * (0.8f + 0.2f * rnd.drawFloatUnitNumber()));
				Color color = ColorUtils.setTransparency(getRocketTailColor(), 0.2f + 0.4f * rx);
				particles.add(new RocketTailSmokeParticle(cx, cy, diam, color));
			}
		}

		public int getViewportWidth() {
			return viewportWidth;
		}

		public int getViewportHeight() {
			return viewportHeight;
		}

		private int getScaleFactor() {
			return scaleFactor;
		}

		private int getSmokeParticlesCount() {
			return 50;
		}

		private List<RocketTailSmokeParticle> getSmokeParticles() {
			return smokeParticles;
		}

		private long getSmokeParticlesTimestamp() {
			return smokeParticlesTimestamp;
		}

		private void setSmokeParticlesTimestamp(long timestamp) {
			this.smokeParticlesTimestamp = timestamp;
		}

		private int getSmokeParticlesUpdatesPerSecond() {
			return smokeParticlesUpdatesPerSecond;
		}

		private Randomizer getRandomizer() {
			return randomizer;
		}

	}

	private static class RocketTailCone {

		private Shape shape;

		private float leftX;

		private float rightX;

		private float cappedRightX;

		private RocketTailCone(Shape shape, float leftX, float rightX, float cappedRightX) {
			this.shape = shape;
			this.leftX = leftX;
			this.rightX = rightX;
			this.cappedRightX = cappedRightX;
		}

		public static RocketTailCone createCone(Rocket rocket, float unitTime) {
			float w = rocket.getViewportWidth();
			float h = rocket.getViewportHeight();
			float yc = h / 2f;
			float x2 = unitTime * w;
			float x1 = x2 - Math.min(w / h * 0.8f, 5f) * h;
			float x0 = Math.min(x1, 0f);
			float x2cap = 0.2f * x1 + 0.8f * x2;
			float xctr2 = (x1 + x2) / 2f;
			float xctr1 = (x1 + xctr2) / 2f;
			Path2D.Float path = new Path2D.Float();
			path.moveTo(x0, 0f);
			path.lineTo(x1, 0f);
			path.curveTo(xctr1, 0f, xctr2, yc, x2, yc);
			path.curveTo(xctr2, yc, xctr1, h, x1, h);
			path.lineTo(x0, h);
			path.closePath();
			return new RocketTailCone(path, x1, x2, x2cap);
		}

		public Shape getShape() {
			return shape;
		}

		public float getLeftX() {
			return leftX;
		}

		public float getRightX() {
			return rightX;
		}

		public float getCappedRightX() {
			return cappedRightX;
		}

	}

	private static class RocketTailSmokeParticle {

		private int centerX;

		private int centerY;

		private int diameter;

		private Color color;

		public RocketTailSmokeParticle(int centerX, int centerY, int diameter, Color color) {
			this.centerX = centerX;
			this.centerY = centerY;
			this.diameter = diameter;
			this.color = color;
		}

		public int getCenterX() {
			return centerX;
		}

		public int getCenterY() {
			return centerY;
		}

		public int getDiameter() {
			return diameter;
		}

		public Color getColor() {
			return color;
		}

	}

}