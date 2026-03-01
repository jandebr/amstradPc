package org.maia.amstrad.gui.browser.carousel.animation.startup;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.carousel.animation.CarouselBaseAnimation;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;
import org.maia.graphics2d.function.Function2D;
import org.maia.graphics2d.function.SigmoidFunction2D;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public abstract class CarouselPortholeStartupAnimation extends CarouselBaseAnimation
		implements CarouselStartupAnimation {

	private AmstradMonitorMode monitorMode;

	private Function2D colorScalingFunction;

	private Randomizer randomizer;

	private Dimension portholeSize;

	private BufferedImage portholeMask;

	private Panorama panorama;

	protected CarouselPortholeStartupAnimation(AmstradMonitorMode monitorMode) {
		this.monitorMode = monitorMode;
		this.colorScalingFunction = createColorScalingFunction();
		this.randomizer = new Randomizer();
	}

	@Override
	public void init(int displayWidth, int displayHeight) {
		super.init(displayWidth, displayHeight);
		setPortholeSize(derivePortholeSize(displayWidth, displayHeight));
		setPortholeMask(createPortholeMask());
	}

	@Override
	public final void renderOntoDisplay(Graphics2D g, int displayWidth, int displayHeight, long elapsedTimeMillis) {
		float alpha = Math.min((elapsedTimeMillis - 200L) / 1000f, 1f);
		if (alpha > 0f) {
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
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f - alpha));
			g2.setColor(getDisplayBackgroundColor());
			g2.fillRect(0, 0, portholeWidth, portholeHeight);
			g2.dispose();
			// overlay porthole mask
			g.drawImage(getPortholeMask(), x, y, portholeWidth, portholeHeight, null);
		}
	}

	protected void renderInPorthole(Graphics2D g, long elapsedTimeMillis) {
		renderPanorama(g);
	}

	private void renderPanorama(Graphics2D g) {
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
			BufferedImage landscapeImg = landscape.getImage();
			int y1 = Math.round(landscape.getBaseline() * ph);
			int y0 = y1 - Math.round(landscape.getHeight() * ph) + 1;
			if (sky == null) {
				g.fillRect(0, 0, pw, ph);
			} else {
				g.drawImage(sky, 0, 0, pw, y1 + 1, null);
				g.fillRect(0, y1 + 1, pw, ph - y1 - 1);
			}
			int lanH = (y1 - y0 + 1);
			int lanW = Math.round(lanH / (float) landscapeImg.getHeight() * landscapeImg.getWidth());
			if (lanW > pw) {
				int lanOffset = landscape.getHorizontalOffset();
				if (!landscape.isHorizontalOffsetDefined()) {
					lanOffset = getRandomizer().drawIntegerNumber(0, lanW - pw);
					landscape.setHorizontalOffset(lanOffset);
				}
				g.drawImage(landscapeImg, -lanOffset, y0, lanW, lanH, null);
			} else {
				g.drawImage(landscapeImg, 0, y0, pw, lanH, null);
			}
		}
	}

	protected Dimension derivePortholeSize(int displayWidth, int displayHeight) {
		int s = 40 + (int) Math.round(Math.sqrt(displayHeight) * 8.0);
		s = Math.min(Math.min(s, displayHeight - 8), displayWidth - 8);
		return new Dimension(s, s);
	}

	protected BufferedImage createPortholeMask() {
		if (getPortholeWidth() <= 400 && getPortholeHeight() <= 400) {
			return toMonitorColors(UIResources.loadImage("animations/porthole-mask400.png"));
		} else {
			return toMonitorColors(UIResources.loadImage("animations/porthole-mask800.png"));
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
		return toMonitorColor(Color.BLACK);
	}

	protected int getPortholeWidth() {
		return getPortholeSize().width;
	}

	protected int getPortholeHeight() {
		return getPortholeSize().height;
	}

	protected AmstradMonitorMode getMonitorMode() {
		return monitorMode;
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

	protected static class Panorama {

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

	protected static class Landscape {

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