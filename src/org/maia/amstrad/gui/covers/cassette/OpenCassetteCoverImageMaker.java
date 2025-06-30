package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.covers.util.Randomizer;
import org.maia.amstrad.gui.covers.util.ReflectionImageMaker;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.graphics2d.image.ops.QuadrilateralImageProjection;
import org.maia.graphics2d.image.ops.QuadrilateralImageProjection.PseudoPerspective;
import org.maia.graphics2d.image.ops.QuadrilateralImageProjection.Quadrilateral;
import org.maia.swing.layout.FillMode;
import org.maia.swing.text.TextLabel;
import org.maia.util.ColorUtils;

public class OpenCassetteCoverImageMaker extends CassetteCoverImageMaker {

	public static Dimension CANONICAL_SIZE = new Dimension(300, 600);

	private BufferedImage printTexture;

	private Color printColor;

	private QuadrilateralImageProjection projection;

	public OpenCassetteCoverImageMaker(String title) {
		this(title, 1.0);
	}

	public OpenCassetteCoverImageMaker(String title, double scaleFactor) {
		this(title, new Randomizer(), scaleFactor);
	}

	public OpenCassetteCoverImageMaker(String title, Randomizer randomizer, double scaleFactor) {
		super(title, randomizer, UIResources.loadImage("covers/cassette-open-300x586.png"),
				UIResources.loadImage("covers/cassette-open-gloss-81x586.png"),
				UIResources.loadImage("covers/cassette-texture-300x480.png"), scaleFactor);
		this.printTexture = scaleImage(UIResources.loadImage("covers/cassette-texture-print-300x586.png"));
		this.printColor = drawPrintColor();
		this.projection = createProjection();
	}

	protected QuadrilateralImageProjection createProjection() {
		QuadrilateralImageProjection projection = new QuadrilateralImageProjection();
		projection.setRememberLast(true);
		return projection;
	}

	protected Quadrilateral createProjectionTargetArea(int xOffset, int yOffset) {
		double scale = getScaleFactor();
		Point p1 = new Point(xOffset + (int) Math.round(1 * scale), yOffset + (int) Math.round(70 * scale));
		Point p2 = new Point(xOffset + (int) Math.round(75 * scale), yOffset + (int) Math.round(6 * scale));
		Point p3 = new Point(xOffset + (int) Math.round(82 * scale), yOffset + (int) Math.round(576 * scale));
		Point p4 = new Point(xOffset + (int) Math.round(6 * scale), yOffset + (int) Math.round(479 * scale));
		return new Quadrilateral(p1, p2, p3, p4);
	}

	@Override
	protected ReflectionImageMaker createReflectionMaker() {
		return new OpenReflectionImageMaker();
	}

	@Override
	protected EmbeddedCassetteImage createEmbeddedCassetteImage(CoverImageEmbedding embedding) {
		EmbeddedCassetteImage embeddedCassette = super.createEmbeddedCassetteImage(embedding);
		applyCassettePrint(embeddedCassette);
		if (hasTitle()) {
			paintTitleOnCassette(embeddedCassette, getTitle());
		}
		return embeddedCassette;
	}

	protected void applyCassettePrint(EmbeddedCassetteImage embeddedCassette) {
		BufferedImage texture = getPrintTexture();
		BufferedImage cassette = embeddedCassette.getImage();
		int printRgb = getPrintColor().getRGB();
		float intensity = getPrintTextureIntensity();
		int padLeft = embeddedCassette.getImagePadding().left;
		int padTop = embeddedCassette.getImagePadding().top;
		int width = embeddedCassette.getWidth();
		int height = embeddedCassette.getHeight();
		int textureWidth = ImageUtils.getWidth(texture);
		int textureHeight = ImageUtils.getHeight(texture);
		for (int yt = 0; yt < textureHeight; yt++) {
			for (int xt = 0; xt < textureWidth; xt++) {
				int textureRgb = texture.getRGB(xt, yt);
				int textureAlpha = textureRgb >>> 24;
				if (textureAlpha != 0) {
					int x = padLeft + xt;
					int y = padTop + yt;
					if (x < width && y < height) {
						int textureGreen = (textureRgb & 0xff00) >>> 8;
						int textureBlue = textureRgb & 0xff;
						float saturationFactor = intensity * (textureGreen - 128) / 127f;
						float brightnessFactor = intensity * (textureBlue - 128) / 127f;
						int rgb = ColorUtils.adjustSaturationAndBrightness(printRgb, saturationFactor,
								brightnessFactor);
						int frontRgb = textureAlpha << 24 | (rgb & 0x00ffffff);
						int backRgb = cassette.getRGB(x, y);
						cassette.setRGB(x, y, ColorUtils.combineByTransparency(frontRgb, backRgb));
					}
				}
			}
		}
	}

	protected void paintTitleOnCassette(EmbeddedCassetteImage embeddedCassette, String title) {
		Rectangle titleBounds = getTitleBounds(embeddedCassette);
		TextLabel label = TextLabel.createSizedLabel(title, getTitleFont(),
				new Dimension(titleBounds.height, titleBounds.width));
		label.setForeground(getTitleColor());
		label.setFillMode(FillMode.FIT);
		int dx = titleBounds.height / 2;
		int dy = titleBounds.width / 2;
		Graphics2D g = embeddedCassette.getImage().createGraphics();
		g.translate(titleBounds.x + dy, titleBounds.y + dx);
		g.shear(0, 0.2);
		g.rotate(Math.PI / 2);
		g.translate(-dx, -dy);
		label.paint(g);
		g.dispose();
	}

	protected Rectangle getTitleBounds(EmbeddedCassetteImage embeddedCassette) {
		double scale = getScaleFactor();
		int x0 = embeddedCassette.getImagePadding().left;
		int y0 = embeddedCassette.getImagePadding().top;
		int x = x0 + (int) Math.round(205 * scale);
		int y = y0 + (int) Math.round(110 * scale);
		int width = (int) Math.round(40 * scale);
		int height = (int) Math.round(380 * scale);
		return new Rectangle(x, y, width, height);
	}

	@Override
	protected void projectFrontImageOnCassette(BufferedImage front, EmbeddedCassetteImage embeddedCassette) {
		int xOffset = embeddedCassette.getImagePadding().left;
		int yOffset = embeddedCassette.getImagePadding().top;
		Quadrilateral targetArea = createProjectionTargetArea(xOffset, yOffset);
		getProjection().projectOntoTargetImage(front, embeddedCassette.getImage(), targetArea,
				new PseudoPerspective(0.5f, 0f));
	}

	@Override
	protected float getPosterTextureIntensity() {
		return 0.1f + 0.1f * drawFloatUnitNumber();
	}

	@Override
	protected float getPosterBrightnessAdjustment() {
		return -0.5f;
	}

	@Override
	protected float getGlossIntensity() {
		return 0.6f + 0.2f * drawFloatUnitNumber();
	}

	protected float getPrintTextureIntensity() {
		return 0.4f + 0.4f * drawFloatUnitNumber();
	}

	protected Color drawPrintColor() {
		if (drawIntegerNumber(1, 3) == 1) {
			// black
			return new Color(10, 10, 10);
		} else {
			// color
			float hue = drawFloatUnitNumber();
			float sat = 0.5f + 0.4f * drawFloatUnitNumber();
			float bri = 0.1f + 0.2f * drawFloatUnitNumber();
			return Color.getHSBColor(hue, sat, bri);
		}
	}

	public Dimension getScaledFrontImageSize() {
		return createProjectionTargetArea(0, 0).getBoundingBox().getSize();
	}

	public Color getPrintColor() {
		return printColor;
	}

	public void setPrintColor(Color color) {
		this.printColor = color;
	}

	private QuadrilateralImageProjection getProjection() {
		return projection;
	}

	private BufferedImage getPrintTexture() {
		return printTexture;
	}

	private static class OpenReflectionImageMaker extends ReflectionImageMaker {

		public OpenReflectionImageMaker() {
			super(0);
			setOutlineFullyAttachedToSurface(true);
			setReflectionLuminance(0.3f);
		}

		@Override
		protected boolean isOpaque(int argb) {
			if (!super.isOpaque(argb)) {
				return false;
			} else if (getAlphaColor() != null) {
				float lum = ColorUtils.getBrightness(argb);
				float alphaLum = ColorUtils.getBrightness(getAlphaColor());
				return Math.abs(lum - alphaLum) > 0.5f;
			} else {
				return true;
			}
		}

	}

}