package org.maia.amstrad.gui.covers.cassette;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.covers.util.Randomizer;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.swing.layout.FillMode;
import org.maia.swing.text.TextLabel;

public class ClosedCassetteCoverImageMaker extends CassetteCoverImageMaker {

	public static Dimension CANONICAL_SIZE = new Dimension(330, 600);

	public static Rectangle CANONICAL_POSTER_REGION = new Rectangle(16, 13, 300, 480);

	private float titleRelativeVerticalPosition; // between 0 and 1

	private Rectangle titleBounds;

	private Color titleBackground = new Color(0, 0, 0, 220);

	public ClosedCassetteCoverImageMaker(String title) {
		this(title, 1.0);
	}

	public ClosedCassetteCoverImageMaker(String title, double scaleFactor) {
		this(title, new Randomizer(), scaleFactor);
	}

	public ClosedCassetteCoverImageMaker(String title, Randomizer randomizer, double scaleFactor) {
		super(title, randomizer, UIResources.loadImage("covers/cassette-closed-330x512.png"),
				UIResources.loadImage("covers/cassette-closed-gloss-a-330x348.png"),
				UIResources.loadImage("covers/cassette-texture-300x480.png"), scaleFactor);
		addCassetteGlossImage(UIResources.loadImage("covers/cassette-closed-gloss-b-330x348.png"));
		addCassetteGlossImage(UIResources.loadImage("covers/cassette-closed-gloss-c-330x348.png"));
		this.titleRelativeVerticalPosition = drawTitleRelativeVerticalPosition();
	}

	public float drawTitleRelativeVerticalPosition() {
		if (drawBoolean()) {
			// center
			return 0.5f;
		} else if (drawBoolean()) {
			// random upper
			return 0.25f * drawFloatUnitNumber();
		} else {
			// top
			return 0f;
		}
	}

	@Override
	protected void paintTitleOnPoster(BufferedImage poster, String title) {
		int posterWidth = ImageUtils.getWidth(poster);
		int posterHeight = ImageUtils.getHeight(poster);
		Rectangle titleBounds = computeTitleBounds(posterWidth, posterHeight);
		setTitleBounds(titleBounds);
		int padX = Math.max(posterWidth / 50, 2);
		int padY = Math.max(posterHeight / 50, 2);
		Insets padding = new Insets(padY, padX, padY, padX);
		TextLabel label = TextLabel.createSizedLabel(title, getTitleFont(), new Dimension(
				titleBounds.width - padding.left - padding.right, titleBounds.height - padding.top - padding.bottom));
		label.setForeground(getTitleColor());
		label.setFillMode(FillMode.FIT);
		Graphics2D g = poster.createGraphics();
		g.translate(titleBounds.x, titleBounds.y);
		g.setColor(getTitleBackground());
		g.fillRect(0, 0, titleBounds.width, titleBounds.height);
		g.translate(padding.left, padding.top);
		label.paint(g);
		g.dispose();
	}

	private Rectangle computeTitleBounds(int posterWidth, int posterHeight) {
		int titleHeight = computeTitleHeight(posterHeight);
		int yTop = Math.round((posterHeight - titleHeight) * getTitleRelativeVerticalPosition());
		return new Rectangle(0, yTop, posterWidth, titleHeight);
	}

	protected int computeTitleHeight(int posterHeight) {
		return Math.max(posterHeight / 8, Math.min(20, posterHeight));
	}

	@Override
	protected void projectFrontImageOnCassette(BufferedImage front, EmbeddedCassetteImage embeddedCassette) {
		Rectangle frontBounds = scaleRectangle(CANONICAL_POSTER_REGION);
		Graphics2D g = embeddedCassette.getImage().createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.translate(embeddedCassette.getImagePadding().left, embeddedCassette.getImagePadding().top);
		g.drawImage(front, frontBounds.x, frontBounds.y, frontBounds.width, frontBounds.height, null);
		g.dispose();
	}

	@Override
	protected int getGlossVerticalTranslation(BufferedImage gloss, int cassetteHeight) {
		Rectangle titleBounds = getTitleBounds();
		int glossHeight = ImageUtils.getHeight(gloss);
		int dy = 0;
		int dyMin = -glossHeight / 2;
		int dyMax = cassetteHeight - glossHeight / 2;
		int attempts = 0;
		boolean overExposedTitle;
		do {
			overExposedTitle = false;
			dy = drawIntegerNumber(dyMin, dyMax);
			if (titleBounds != null) {
				int xg = titleBounds.x + titleBounds.width / 2;
				int yg = titleBounds.y + titleBounds.height / 2 - dy;
				if (yg >= 0 && yg < glossHeight) {
					float g = (gloss.getRGB(xg, yg) >>> 24) / 128f;
					overExposedTitle = g >= 0.5f;
				}
			}
		} while (overExposedTitle && ++attempts < 5);
		return dy;
	}

	public Dimension getScaledPosterSize() {
		return scaleSize(CANONICAL_POSTER_REGION.getSize());
	}

	public float getTitleRelativeVerticalPosition() {
		return titleRelativeVerticalPosition;
	}

	public void setTitleRelativeVerticalPosition(float relativeVerticalPosition) {
		this.titleRelativeVerticalPosition = relativeVerticalPosition;
	}

	private Rectangle getTitleBounds() {
		return titleBounds;
	}

	private void setTitleBounds(Rectangle titleBounds) {
		this.titleBounds = titleBounds;
	}

	public Color getTitleBackground() {
		return titleBackground;
	}

	public void setTitleBackground(Color titleBackground) {
		this.titleBackground = titleBackground;
	}

}