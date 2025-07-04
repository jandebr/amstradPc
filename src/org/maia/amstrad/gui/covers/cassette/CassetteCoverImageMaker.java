package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.covers.util.RandomImageMaker;
import org.maia.amstrad.gui.covers.util.Randomizer;
import org.maia.amstrad.gui.covers.util.ReflectionImageMaker;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.util.ColorUtils;

public abstract class CassetteCoverImageMaker extends RandomImageMaker {

	private double scaleFactor;

	private BufferedImage cassetteImage;

	private List<BufferedImage> cassetteGlossImages = new Vector<BufferedImage>();

	private PosterTexture posterTexture;

	private ReflectionImageMaker reflectionMaker;

	private String title;

	private Font titleFont = new Font(Font.DIALOG, Font.BOLD, 24);

	private Color titleColor = Color.WHITE;

	protected CassetteCoverImageMaker(String title, Randomizer randomizer, BufferedImage cassetteImage,
			BufferedImage cassetteGloss, BufferedImage posterTexture) {
		this(title, randomizer, cassetteImage, cassetteGloss, posterTexture, 1.0);
	}

	protected CassetteCoverImageMaker(String title, Randomizer randomizer, BufferedImage cassetteImage,
			BufferedImage cassetteGloss, BufferedImage posterTexture, double scaleFactor) {
		super(randomizer);
		this.title = title;
		this.scaleFactor = scaleFactor;
		this.reflectionMaker = createReflectionMaker();
		this.cassetteImage = scaleImage(cassetteImage);
		this.posterTexture = new PosterTexture(scaleImage(posterTexture));
		addCassetteGlossImage(cassetteGloss);
	}

	protected ReflectionImageMaker createReflectionMaker() {
		ReflectionImageMaker reflection = new ReflectionImageMaker(0);
		reflection.setReflectionLuminance(0.2f);
		return reflection;
	}

	public void addCassetteGlossImage(BufferedImage glossImage) {
		getCassetteGlossImages().add(scaleImage(glossImage));
	}

	public BufferedImage makeCoverImage(BufferedImage posterImage) {
		return makeCoverImage(posterImage, new CoverImageEmbedding(ImageUtils.getSize(posterImage)));
	}

	public BufferedImage makeCoverImage(BufferedImage posterImage, CoverImageEmbedding embedding) {
		return makeCoverImage(posterImage, false, embedding);
	}

	public BufferedImage makeCoverImage(BufferedImage posterImage, boolean posterImageReadOnly,
			CoverImageEmbedding embedding) {
		BufferedImage front = createCassetteFront(posterImage, posterImageReadOnly);
		EmbeddedCassetteImage embeddedCassette = createEmbeddedCassetteImage(embedding);
		projectFrontImageOnCassette(front, embeddedCassette);
		applyGloss(embeddedCassette);
		applyReflection(embeddedCassette);
		return embeddedCassette.getImage();
	}

	protected BufferedImage createCassetteFront(BufferedImage posterImage, boolean posterImageReadOnly) {
		BufferedImage image = posterImageReadOnly ? ImageUtils.duplicateImage(posterImage) : posterImage;
		if (hasTitle()) {
			paintTitleOnPoster(image, getTitle());
		}
		applyPosterTexture(image);
		return image;
	}

	protected void paintTitleOnPoster(BufferedImage poster, String title) {
		// for subclasses to implement
	}

	protected void applyPosterTexture(BufferedImage poster) {
		PosterTexture texture = getPosterTexture();
		float brightness = getPosterBrightnessAdjustment();
		float intensity = getPosterTextureIntensity();
		int width = ImageUtils.getWidth(poster);
		int height = ImageUtils.getHeight(poster);
		int textureWidth = texture.getWidth();
		int textureHeight = texture.getHeight();
		for (int y = 0; y < height; y++) {
			int ty = (int) Math.floor((y + 0.5f) / height * textureHeight);
			for (int x = 0; x < width; x++) {
				int tx = (int) Math.floor((x + 0.5f) / width * textureWidth);
				poster.setRGB(x, y, texture.texturizeRGB(poster.getRGB(x, y), tx, ty, brightness, intensity));
			}
		}
	}

	protected EmbeddedCassetteImage createEmbeddedCassetteImage(CoverImageEmbedding embedding) {
		BufferedImage cassette = getCassetteImage();
		Dimension cassetteSize = ImageUtils.getSize(cassette);
		Dimension embedSize = embedding.getSize();
		int padHor = Math.max(embedSize.width - cassetteSize.width, 0);
		int padVer = Math.max(embedSize.height - cassetteSize.height, 0);
		int padLeft = Math.round(padHor * embedding.getPadLeftFraction());
		int padRight = padHor - padLeft;
		int padTop = Math.round(padVer * embedding.getPadTopFraction());
		int padBottom = padVer - padTop;
		Insets padding = new Insets(padTop, padLeft, padBottom, padRight);
		BufferedImage newCassette = ImageUtils.createImage(embedSize, embedding.getBackground());
		Graphics2D g = newCassette.createGraphics();
		g.drawImage(cassette, padLeft, padTop, null);
		g.dispose();
		return new EmbeddedCassetteImage(newCassette, embedding, padding);
	}

	protected abstract void projectFrontImageOnCassette(BufferedImage front, EmbeddedCassetteImage embeddedCassette);

	protected void applyGloss(EmbeddedCassetteImage embeddedCassette) {
		BufferedImage gloss = drawCassetteGlossImage();
		BufferedImage cassette = embeddedCassette.getImage();
		float intensity = getGlossIntensity();
		Insets padding = embeddedCassette.getImagePadding();
		int padLeft = padding.left;
		int padTop = padding.top;
		int padBottom = padding.bottom;
		int width = embeddedCassette.getWidth();
		int height = embeddedCassette.getHeight();
		int glossWidth = ImageUtils.getWidth(gloss);
		int glossHeight = ImageUtils.getHeight(gloss);
		int dy = getGlossVerticalTranslation(gloss, height - padTop - padBottom);
		for (int yg = 0; yg < glossHeight; yg++) {
			int y = padTop + dy + yg;
			if (y >= padTop && y < height - padBottom) {
				for (int xg = 0; xg < glossWidth; xg++) {
					int x = padLeft + xg;
					if (x < width) {
						float glossFactor = intensity * (gloss.getRGB(xg, yg) >>> 24) / 128f;
						if (glossFactor > 0f) {
							int rgb = cassette.getRGB(x, y);
							rgb = ColorUtils.adjustBrightness(rgb, glossFactor);
							cassette.setRGB(x, y, rgb);
						}
					}
				}
			}
		}
	}

	protected void applyReflection(EmbeddedCassetteImage embeddedCassette) {
		ReflectionImageMaker reflection = getReflectionMaker();
		reflection.setMaxReflectionHeight(getMaxReflectionHeight(embeddedCassette));
		reflection.setAlphaColor(embeddedCassette.getEmbedding().getBackground());
		BufferedImage cassetteImage = embeddedCassette.getImage();
		BufferedImage reflectedImage = reflection.reflectImage(cassetteImage, reflection.getLastImageOutline());
		embeddedCassette.setImage(reflectedImage);
	}

	protected BufferedImage drawCassetteGlossImage() {
		int i = drawIntegerNumber(0, getCassetteGlossImages().size() - 1);
		return getCassetteGlossImages().get(i);
	}

	protected int getGlossVerticalTranslation(BufferedImage gloss, int cassetteHeight) {
		return 0;
	}

	protected float getPosterTextureIntensity() {
		return 0.1f + 0.2f * drawFloatUnitNumber();
	}

	protected float getPosterBrightnessAdjustment() {
		return 0f;
	}

	protected float getGlossIntensity() {
		return 0.1f + 0.1f * drawFloatUnitNumber();
	}

	protected int getMaxReflectionHeight(EmbeddedCassetteImage embeddedCassette) {
		return embeddedCassette.getHeight() / 10;
	}

	protected BufferedImage scaleImage(BufferedImage image) {
		double scale = getScaleFactor();
		if (scale == 1.0) {
			return image;
		} else {
			return ImageUtils.scale(image, getScaleFactor());
		}
	}

	protected Rectangle scaleRectangle(Rectangle rect) {
		double scale = getScaleFactor();
		if (scale == 1.0) {
			return rect;
		} else {
			int x = (int) Math.round(rect.x * scale);
			int y = (int) Math.round(rect.y * scale);
			int width = (int) Math.round(rect.width * scale);
			int height = (int) Math.round(rect.height * scale);
			return new Rectangle(x, y, width, height);
		}
	}

	public Dimension scaleSize(Dimension size) {
		double scale = getScaleFactor();
		int width = (int) Math.round(size.width * scale);
		int height = (int) Math.round(size.height * scale);
		return new Dimension(width, height);
	}

	public double getScaleFactor() {
		return scaleFactor;
	}

	public BufferedImage getCassetteImage() {
		return cassetteImage;
	}

	public void setCassetteImage(BufferedImage cassetteImage) {
		this.cassetteImage = cassetteImage;
	}

	private List<BufferedImage> getCassetteGlossImages() {
		return cassetteGlossImages;
	}

	private PosterTexture getPosterTexture() {
		return posterTexture;
	}

	protected ReflectionImageMaker getReflectionMaker() {
		return reflectionMaker;
	}

	public boolean hasTitle() {
		return getTitle() != null;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Font getTitleFont() {
		return titleFont;
	}

	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	public Color getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}

	public static class CoverImageEmbedding {

		private Dimension size;

		private Color background;

		private float padLeftFraction = 0.5f; // center

		private float padTopFraction = 0.5f; // center

		public CoverImageEmbedding(Dimension size) {
			this(size, new Color(0, 0, 0, 0)); // transparent background
		}

		public CoverImageEmbedding(Dimension size, Color background) {
			this.size = size;
			this.background = background;
		}

		public Dimension getSize() {
			return size;
		}

		public void setSize(Dimension size) {
			this.size = size;
		}

		public Color getBackground() {
			return background;
		}

		public void setBackground(Color background) {
			this.background = background;
		}

		public float getPadLeftFraction() {
			return padLeftFraction;
		}

		public void setPadLeftFraction(float fraction) {
			this.padLeftFraction = fraction;
		}

		public float getPadTopFraction() {
			return padTopFraction;
		}

		public void setPadTopFraction(float fraction) {
			this.padTopFraction = fraction;
		}

	}

	protected static class EmbeddedCassetteImage {

		private BufferedImage image;

		private CoverImageEmbedding embedding;

		private Insets imagePadding;

		public EmbeddedCassetteImage(BufferedImage image, CoverImageEmbedding embedding, Insets imagePadding) {
			this.image = image;
			this.embedding = embedding;
			this.imagePadding = imagePadding;
		}

		public int getWidth() {
			return getEmbedding().getSize().width;
		}

		public int getHeight() {
			return getEmbedding().getSize().height;
		}

		public BufferedImage getImage() {
			return image;
		}

		protected void setImage(BufferedImage image) {
			this.image = image;
		}

		public CoverImageEmbedding getEmbedding() {
			return embedding;
		}

		public Insets getImagePadding() {
			return imagePadding;
		}

	}

	private static class PosterTexture {

		private BufferedImage textureImage;

		private int[][] alphas;

		private float[][] saturationFactors;

		private float[][] brightnessFactors;

		public PosterTexture(BufferedImage textureImage) {
			this.textureImage = textureImage;
			initData();
		}

		private void initData() {
			BufferedImage image = getTextureImage();
			int width = getWidth();
			int height = getHeight();
			alphas = new int[width][height];
			saturationFactors = new float[width][height];
			brightnessFactors = new float[width][height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int textureRgb = image.getRGB(x, y);
					alphas[x][y] = textureRgb & 0xff000000;
					saturationFactors[x][y] = (((textureRgb & 0xff00) >>> 8) - 128) / 127f;
					brightnessFactors[x][y] = ((textureRgb & 0xff) - 128) / 127f;
				}
			}
		}

		public int texturizeRGB(int rgb, int x, int y, float brightnessAdjustment, float intensity) {
			float saturationFactor = intensity * saturationFactors[x][y];
			float brightnessFactor = Math.max(Math.min(intensity * brightnessFactors[x][y] + brightnessAdjustment, 1f),
					-1f);
			return alphas[x][y]
					| (ColorUtils.adjustSaturationAndBrightness(rgb, saturationFactor, brightnessFactor) & 0x00ffffff);
		}

		public int getWidth() {
			return ImageUtils.getWidth(getTextureImage());
		}

		public int getHeight() {
			return ImageUtils.getHeight(getTextureImage());
		}

		public BufferedImage getTextureImage() {
			return textureImage;
		}

	}

}