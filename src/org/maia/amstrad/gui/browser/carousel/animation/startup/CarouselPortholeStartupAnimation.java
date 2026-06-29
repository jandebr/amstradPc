package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.AmstradSymbolRenderer;
import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.animation.CarouselBaseAnimation;
import org.maia.amstrad.gui.sprite.SpriteColorMap;
import org.maia.amstrad.gui.sprite.SpriteColorMapImpl;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;
import org.maia.graphics2d.function.Function2D;
import org.maia.graphics2d.function.SigmoidFunction2D;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.swing.SwingUtils;
import org.maia.swing.layout.HorizontalAlignment;
import org.maia.swing.text.TextLabel;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public abstract class CarouselPortholeStartupAnimation extends CarouselBaseAnimation
		implements CarouselStartupAnimation {

	private AmstradGraphicsContext graphicsContext;

	private AmstradMonitorMode monitorMode;

	private AmstradSymbolRenderer symbolRenderer;

	private Function2D colorScalingFunction;

	private Randomizer randomizer;

	private Dimension portholeSize;

	private BufferedImage portholeMask;

	private Panorama panorama;

	private boolean showLoadingMessage;

	private Font loadingMessageFont;

	private Color loadingMessageColor;

	private TextLabel loadingLabelTop;

	private TextLabel loadingLabelBottom;

	private String loadingMessageTop;

	private String loadingMessageBottom;

	public static String DEFAULT_LOADING_MESSAGE_TOP = "loading";

	public static String DEFAULT_LOADING_MESSAGE_BOTTOM = "browser";

	protected CarouselPortholeStartupAnimation(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
		this.monitorMode = graphicsContext.getMonitorMode();
		this.colorScalingFunction = createColorScalingFunction();
		this.randomizer = new Randomizer();
		this.showLoadingMessage = true;
		this.loadingMessageFont = graphicsContext.getSystemFont();
		this.loadingMessageColor = toMonitorColor(Color.WHITE);
		this.loadingMessageTop = DEFAULT_LOADING_MESSAGE_TOP;
		this.loadingMessageBottom = DEFAULT_LOADING_MESSAGE_BOTTOM;
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setPortholeSize(derivePortholeSize(displayWidth, displayHeight));
		setPortholeMask(createPortholeMask());
		if (isShowLoadingMessage()) {
			initLoadingLabels(displayWidth);
		}
	}

	private void initLoadingLabels(int displayWidth) {
		String messageTop = getLoadingMessageTop();
		String messageBottom = getLoadingMessageBottom();
		Font font = getLoadingMessageFont();
		int margin = Math.max(getPortholeHeight() / 15, 8);
		int targetLabelWidth = getTargetLoadingMessageWidth();
		float fontSizeTop = TextLabel.getFontSizeForLineWidth(font, messageTop, targetLabelWidth);
		float fontSizeBottom = TextLabel.getFontSizeForLineWidth(font, messageBottom, targetLabelWidth);
		float fontSize = Math.max(Math.min(fontSizeTop, fontSizeBottom), 10f);
		font = font.deriveFont(fontSize);
		Insets insets = new Insets(margin, 0, margin, 0);
		setLoadingLabelTop(
				TextLabel.createLineLabel(messageTop, font, displayWidth, HorizontalAlignment.CENTER, insets));
		setLoadingLabelBottom(
				TextLabel.createLineLabel(messageBottom, font, displayWidth, HorizontalAlignment.CENTER, insets));
		SwingUtils.fixSize(getLoadingLabelTop(), getLoadingLabelTop().getPreferredSize());
		SwingUtils.fixSize(getLoadingLabelBottom(), getLoadingLabelBottom().getPreferredSize());
	}

	@Override
	public void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		renderPorthole(g, displayWidth, displayHeight, elapsedTimeMillis);
		if (isShowLoadingMessage()) {
			renderLoadingMessage(g, displayHeight, elapsedTimeMillis);
		}
	}

	private void renderPorthole(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		// center porthole in display
		int portholeWidth = getPortholeWidth();
		int portholeHeight = getPortholeHeight();
		int x = (displayWidth - portholeWidth) / 2;
		int y = (displayHeight - portholeHeight) / 2;
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(x, y);
		g2.setClip(new Ellipse2D.Float(0, 0, portholeWidth, portholeHeight));
		renderInPorthole(g2, elapsedTimeMillis);
		// fade in
		float alpha = Math.min(elapsedTimeMillis / 1000f, 1f);
		alpha *= alpha;
		if (alpha < 1f) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - alpha));
			g2.setColor(getDisplayBackgroundColor());
			g2.fillRect(0, 0, portholeWidth, portholeHeight);
			g2.dispose();
		}
		// overlay porthole mask
		g.drawImage(getPortholeMask(), x, y, portholeWidth, portholeHeight, null);
	}

	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		renderPanoramaInPorthole(g);
	}

	private void renderPanoramaInPorthole(Graphics2D g) {
		int pw = getPortholeWidth();
		int ph = getPortholeHeight();
		Panorama panorama = getPanorama();
		g.setColor(panorama.getBaseColor());
		BufferedImage sky = panorama.getSky();
		Landscape landscape = panorama.getLandscape();
		if (landscape == null) {
			if (sky == null) {
				g.fillRect(0, 0, pw, ph);
			} else {
				g.drawImage(sky, 0, 0, pw, ph, null);
			}
		} else {
			int y1 = Math.round(landscape.getBaseline() * ph);
			int y0 = y1 - Math.round(landscape.getHeight() * ph) + 1;
			if (sky == null) {
				g.fillRect(0, 0, pw, ph);
			} else {
				g.drawImage(sky, 0, 0, pw, y1 + 1, null);
				g.fillRect(0, y1 + 1, pw, ph - y1 - 1);
			}
			Rectangle r = getLandscapeViewRegion();
			g.drawImage(landscape.getImage(), r.x, r.y, r.width, r.height, null);
		}
	}

	private void renderLoadingMessage(Graphics2D g, int displayHeight, long elapsedTimeMillis) {
		renderLoadingLabelTop(g, displayHeight, elapsedTimeMillis);
		renderLoadingLabelBottom(g, displayHeight, elapsedTimeMillis);
	}

	private void renderLoadingLabelTop(Graphics2D g, int displayHeight, long elapsedTimeMillis) {
		float tra = (float) Math.sqrt(0.7 + 0.28 * (0.5 + Math.sin(elapsedTimeMillis / 400.0 + Math.PI) / 2.0));
		getLoadingLabelTop().setForeground(ColorUtils.setTransparency(getLoadingMessageColor(), tra));
		int y = (displayHeight - getPortholeHeight()) / 2 - getLoadingLabelTop().getHeight();
		renderLoadingLabel(getLoadingLabelTop(), g, y);
	}

	private void renderLoadingLabelBottom(Graphics2D g, int displayHeight, long elapsedTimeMillis) {
		float tra = (float) Math.sqrt(0.7 + 0.28 * (0.5 + Math.sin(elapsedTimeMillis / 400.0) / 2.0));
		getLoadingLabelBottom().setForeground(ColorUtils.setTransparency(getLoadingMessageColor(), tra));
		int y = (displayHeight + getPortholeHeight()) / 2;
		renderLoadingLabel(getLoadingLabelBottom(), g, y);
	}

	private void renderLoadingLabel(TextLabel label, Graphics2D g, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(0, y);
		label.paint(g2);
		g2.dispose();
	}

	protected Rectangle getLandscapeViewRegion() {
		Landscape landscape = getPanorama().getLandscape();
		if (landscape != null) {
			BufferedImage landscapeImg = landscape.getImage();
			int pw = getPortholeWidth();
			int ph = getPortholeHeight();
			int y1 = Math.round(landscape.getBaseline() * ph);
			int y0 = y1 - Math.round(landscape.getHeight() * ph) + 1;
			int lanH = (y1 - y0 + 1);
			int lanW = Math.round(lanH / (float) landscapeImg.getHeight() * landscapeImg.getWidth());
			if (lanW > pw) {
				int lanOffset = landscape.getHorizontalOffset();
				if (!landscape.isHorizontalOffsetDefined()) {
					lanOffset = getRandomizer().drawIntegerNumber(0, lanW - pw);
					landscape.setHorizontalOffset(lanOffset);
				}
				return new Rectangle(-lanOffset, y0, lanW, lanH);
			} else {
				return new Rectangle(0, y0, pw, lanH);
			}
		} else {
			return null;
		}
	}

	protected Point projectLandscapeCoordinateToView(Point coord) {
		Rectangle r = getLandscapeViewRegion();
		if (r != null) {
			BufferedImage landscapeImg = getPanorama().getLandscape().getImage();
			float rx = coord.x / (float) (ImageUtils.getWidth(landscapeImg) - 1);
			float ry = coord.y / (float) (ImageUtils.getHeight(landscapeImg) - 1);
			int vx = Math.round(r.x + rx * (r.width - 1));
			int vy = Math.round(r.y + ry * (r.height - 1));
			return new Point(vx, vy);
		} else {
			return null;
		}
	}

	protected Rectangle projectLandscapeRegionToView(Rectangle region) {
		Point p1 = projectLandscapeCoordinateToView(region.getLocation());
		Point p2 = projectLandscapeCoordinateToView(
				new Point(region.x + region.width - 1, region.y + region.height - 1));
		if (p1 != null && p2 != null) {
			return new Rectangle(p1.x, p1.y, p2.x - p1.x + 1, p2.y - p1.y + 1);
		} else {
			return null;
		}
	}

	protected int getTargetLoadingMessageWidth() {
		return Math.max(getPortholeWidth() / 3, 60);
	}

	protected Dimension derivePortholeSize(int displayWidth, int displayHeight) {
		int s = 40 + (int) Math.round(Math.sqrt(displayHeight) * 8.0);
		s = Math.min(Math.min(s, displayHeight - 8), displayWidth - 8);
		return new Dimension(s, s);
	}

	protected BufferedImage createPortholeMask() {
		if (getPortholeWidth() <= 400 && getPortholeHeight() <= 400) {
			return toMonitorColors(UIResources.loadImage("animations/porthole-mask-400.png"));
		} else {
			return toMonitorColors(UIResources.loadImage("animations/porthole-mask-800.png"));
		}
	}

	protected Panorama createPanorama() {
		return new Panorama(getDisplayBackgroundColor());
	}

	protected Function2D createColorScalingFunction() {
		SigmoidFunction2D sigmoid = SigmoidFunction2D.createCappedFunction(0, 1.0, 0, 1.0);
		return new Function2D() {

			@Override
			public double evaluate(double x) {
				double lin = getColorScalingFunctionLinearity();
				if (lin == 1.0) {
					return x;
				} else {
					return lin * x + (1.0 - lin) * sigmoid.evaluate(x);
				}
			}
		};
	}

	protected BufferedImage toMonitorColors(BufferedImage image) {
		return toMonitorColors(image, getMonitorMode());
	}

	private BufferedImage toMonitorColors(BufferedImage image, AmstradMonitorMode mode) {
		if (AmstradMonitorMode.COLOR.equals(mode)) {
			return image;
		} else {
			int width = ImageUtils.getWidth(image);
			int height = ImageUtils.getHeight(image);
			BufferedImage recoloredImage = ImageUtils.createImage(width, height);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					Color color = new Color(image.getRGB(x, y), true);
					recoloredImage.setRGB(x, y, toMonitorColor(color, mode).getRGB());
				}
			}
			return recoloredImage;
		}
	}

	protected SpriteColorMap toMonitorColors(SpriteColorMap colorMap) {
		SpriteColorMapImpl recoloredColorMap = new SpriteColorMapImpl();
		for (int i = 0; i <= colorMap.getMaxColorIndex(); i++) {
			Color color = colorMap.getColor(i);
			if (color != null) {
				recoloredColorMap.setColor(i, toMonitorColor(color));
			}
		}
		return recoloredColorMap;
	}

	protected Color toMonitorColor(Color color) {
		return toMonitorColor(color, getMonitorMode());
	}

	private Color toMonitorColor(Color color, AmstradMonitorMode mode) {
		if (AmstradMonitorMode.COLOR.equals(mode)) {
			return color;
		} else {
			AmstradSystemColors palette = AmstradSystemColors.getSystemColors(mode);
			int n = palette.getNumberOfColors();
			int alpha = color.getAlpha();
			float bri = (float) getColorScalingFunction().evaluate(ColorUtils.getBrightness(color));
			if (bri == 0f) {
				color = palette.getColor(0);
			} else if (bri == 1f) {
				color = palette.getColor(n - 1);
			} else {
				float r = bri * (n - 1);
				int pi = (int) Math.floor(r);
				r -= pi;
				color = ColorUtils.interpolate(palette.getColor(pi), palette.getColor(pi + 1), r);
			}
			if (alpha != 255) {
				color = new Color(alpha << 24 | (color.getRGB() & 0x00ffffff), true);
			}
			return color;
		}
	}

	@Override
	public Color getDisplayBackgroundColor() {
		return Color.BLACK; // matches the porthole mask
	}

	protected int getPortholeWidth() {
		return getPortholeSize().width;
	}

	protected int getPortholeHeight() {
		return getPortholeSize().height;
	}

	protected AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

	protected AmstradMonitorMode getMonitorMode() {
		return monitorMode;
	}

	protected AmstradSymbolRenderer getSymbolRenderer(Graphics2D g) {
		if (symbolRenderer == null) {
			symbolRenderer = new AmstradSymbolRenderer(getGraphicsContext(), g);
		} else {
			symbolRenderer.replaceGraphics2D(g);
		}
		return symbolRenderer;
	}

	protected Function2D getColorScalingFunction() {
		return colorScalingFunction;
	}

	protected double getColorScalingFunctionLinearity() {
		return 1.0;
	}

	protected Randomizer getRandomizer() {
		return randomizer;
	}

	protected Dimension getPortholeSize() {
		return portholeSize;
	}

	private void setPortholeSize(Dimension size) {
		this.portholeSize = size;
	}

	private BufferedImage getPortholeMask() {
		return portholeMask;
	}

	private void setPortholeMask(BufferedImage mask) {
		this.portholeMask = mask;
	}

	protected Panorama getPanorama() {
		if (panorama == null) {
			panorama = createPanorama();
		}
		return panorama;
	}

	public boolean isShowLoadingMessage() {
		return showLoadingMessage;
	}

	public void setShowLoadingMessage(boolean show) {
		this.showLoadingMessage = show;
	}

	public Font getLoadingMessageFont() {
		return loadingMessageFont;
	}

	public void setLoadingMessageFont(Font font) {
		this.loadingMessageFont = font;
	}

	public Color getLoadingMessageColor() {
		return loadingMessageColor;
	}

	public void setLoadingMessageColor(Color color) {
		this.loadingMessageColor = color;
	}

	public String getLoadingMessageTop() {
		return loadingMessageTop;
	}

	public void setLoadingMessageTop(String msg) {
		this.loadingMessageTop = msg;
	}

	public String getLoadingMessageBottom() {
		return loadingMessageBottom;
	}

	public void setLoadingMessageBottom(String msg) {
		this.loadingMessageBottom = msg;
	}

	private TextLabel getLoadingLabelTop() {
		return loadingLabelTop;
	}

	private void setLoadingLabelTop(TextLabel label) {
		this.loadingLabelTop = label;
	}

	private TextLabel getLoadingLabelBottom() {
		return loadingLabelBottom;
	}

	private void setLoadingLabelBottom(TextLabel label) {
		this.loadingLabelBottom = label;
	}

	public static class Panorama {

		private Color baseColor;

		private BufferedImage sky; // optional

		private Landscape landscape; // optional

		public Panorama(Color baseColor) {
			this(baseColor, null);
		}

		public Panorama(Color baseColor, BufferedImage sky) {
			this(baseColor, sky, null);
		}

		public Panorama(Color baseColor, BufferedImage sky, Landscape landscape) {
			this.baseColor = baseColor;
			this.sky = sky;
			this.landscape = landscape;
		}

		public Color getBaseColor() {
			return baseColor;
		}

		public BufferedImage getSky() {
			return sky;
		}

		public Landscape getLandscape() {
			return landscape;
		}

	}

	public static class Landscape {

		private BufferedImage image;

		private float baseline;

		private float height;

		private int horizontalOffset; // in pixels

		private boolean horizontalOffsetDefined;

		public Landscape(BufferedImage image, float baseline, float height) {
			this.image = image;
			this.baseline = baseline;
			this.height = height;
		}

		public BufferedImage getImage() {
			return image;
		}

		public float getBaseline() {
			return baseline;
		}

		public float getHeight() {
			return height;
		}

		public int getHorizontalOffset() {
			return horizontalOffset;
		}

		public void setHorizontalOffset(int offset) {
			this.horizontalOffset = offset;
			this.horizontalOffsetDefined = true;
		}

		private boolean isHorizontalOffsetDefined() {
			return horizontalOffsetDefined;
		}

	}

}