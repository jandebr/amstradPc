package org.maia.amstrad.gui.covers.stock.badge;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

import org.maia.amstrad.gui.covers.util.RandomImageMaker;
import org.maia.graphics2d.image.ImageUtils;
import org.maia.swing.layout.FillMode;
import org.maia.swing.layout.HorizontalAlignment;
import org.maia.swing.layout.InnerRegionLayout;
import org.maia.swing.layout.VerticalAlignment;
import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public abstract class EmbossedBadgeCoverImageMaker extends RandomImageMaker {

	/**
	 * Shift in brightness outside the embossed badge
	 * <p>
	 * Ranges from -1 (maximum darkness) to +1 (maximum brightness)
	 * </p>
	 */
	private float outsideBadgeBrightnessShift = -0.15f;

	/**
	 * Shift in brightness inside the embossed badge
	 * <p>
	 * Ranges from -1 (maximum darkness) to +1 (maximum brightness)
	 * </p>
	 */
	private float insideBadgeBrightnessShift = 0.15f;

	/**
	 * Contrast of the embossing contours of the badge
	 * <p>
	 * Ranges from 0 (minimum contrast) to +1 (maximum contrast)
	 * </p>
	 */
	private float badgeContourContrast = 0.6f;

	private static final Color MASK_COLOR_OUTSIDE = new Color(64, 64, 64);

	private static final Color MASK_COLOR_INSIDE = new Color(128, 128, 128);

	private static final BufferedImageOp EMBOSS_OP = new ConvolveOp(
			new Kernel(3, 3, new float[] { 2, 0, 0, 0, 1, 0, 0, 0, -2 }));

	protected EmbossedBadgeCoverImageMaker(Randomizer randomizer) {
		super(randomizer);
	}

	public static Insets computeCenteredBadgeInsets(BufferedImage backdrop, float paddingRatio) {
		return computeCenteredBadgeInsets(backdrop, paddingRatio, paddingRatio);
	}

	public static Insets computeCenteredBadgeInsets(BufferedImage backdrop, float horizontalPaddingRatio,
			float verticalPaddingRatio) {
		int padX = Math.round(ImageUtils.getWidth(backdrop) * horizontalPaddingRatio);
		int padY = Math.round(ImageUtils.getHeight(backdrop) * verticalPaddingRatio);
		return new Insets(padY, padX, padY, padX);
	}

	protected Rectangle computeBadgeBounds(BufferedImage backdrop, Insets insets, MonochromeBadge badge) {
		InnerRegionLayout layout = new InnerRegionLayout(ImageUtils.getSize(backdrop), badge.getSize(), insets);
		layout.setHorizontalAlignment(HorizontalAlignment.CENTER);
		layout.setVerticalAlignment(VerticalAlignment.CENTER);
		layout.setFillMode(FillMode.FIT);
		return layout.getInnerRegionLayoutBounds();
	}

	public BufferedImage overlayEmbossedBadge(BufferedImage backdrop, Insets insets) {
		return overlayEmbossedBadge(backdrop, insets, null);
	}

	public BufferedImage overlayEmbossedBadge(BufferedImage backdrop, Insets insets, String suggestedBadgeId) {
		MonochromeBadge badge = drawBadge(suggestedBadgeId);
		if (badge != null) {
			Rectangle badgeBounds = computeBadgeBounds(backdrop, insets, badge);
			return overlayEmbossedBadge(backdrop, badgeBounds, badge);
		} else {
			return backdrop;
		}
	}

	private BufferedImage overlayEmbossedBadge(BufferedImage backdrop, Rectangle badgeBounds, MonochromeBadge badge) {
		Dimension size = ImageUtils.getSize(backdrop);
		BufferedImage image = createEmbossedBadgeMask(size, badgeBounds, badge);
		float brightnessFactor = 0f;
		int maskOut = MASK_COLOR_OUTSIDE.getRGB();
		int maskIn = MASK_COLOR_INSIDE.getRGB();
		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++) {
				int maskRgb = image.getRGB(x, y);
				if (maskRgb == maskOut) {
					brightnessFactor = getOutsideBadgeBrightnessShift();
				} else if (maskRgb == maskIn) {
					brightnessFactor = getInsideBadgeBrightnessShift();
				} else {
					float r = ColorUtils.getBrightness(maskRgb);
					brightnessFactor = (0.5f + 0.5f * getBadgeContourContrast()) * (2f * r - 1f);
				}
				int backRgb = backdrop.getRGB(x, y);
				image.setRGB(x, y, ColorUtils.adjustBrightness(backRgb, brightnessFactor));
			}
		}
		return image;
	}

	private BufferedImage createEmbossedBadgeMask(Dimension maskSize, Rectangle badgeBounds, MonochromeBadge badge) {
		BufferedImage embossedMask = EMBOSS_OP.filter(createBadgeMask(maskSize, badgeBounds, badge), null);
		Graphics2D g = embossedMask.createGraphics();
		g.setColor(MASK_COLOR_OUTSIDE);
		g.drawLine(0, 0, maskSize.width - 1, 0);
		g.drawLine(0, maskSize.height - 1, maskSize.width - 1, maskSize.height - 1);
		g.drawLine(0, 0, 0, maskSize.height - 1);
		g.drawLine(maskSize.width - 1, 0, maskSize.width - 1, maskSize.height - 1);
		g.dispose();
		return embossedMask;
	}

	private BufferedImage createBadgeMask(Dimension maskSize, Rectangle badgeBounds, MonochromeBadge badge) {
		Dimension badgeSize = badge.getSize();
		double scaleX = badgeBounds.getWidth() / badgeSize.getWidth();
		double scaleY = badgeBounds.getHeight() / badgeSize.getHeight();
		double scale = Math.min(scaleX, scaleY);
		BufferedImage mask = ImageUtils.createImage(maskSize, MASK_COLOR_OUTSIDE);
		Graphics2D g = mask.createGraphics();
		g.translate(badgeBounds.x, badgeBounds.y);
		g.scale(scale, scale);
		g.setColor(MASK_COLOR_INSIDE);
		badge.render(g);
		g.dispose();
		return mask;
	}

	protected abstract MonochromeBadge drawBadge(String suggestedBadgeId);

	public float getOutsideBadgeBrightnessShift() {
		return outsideBadgeBrightnessShift;
	}

	public void setOutsideBadgeBrightnessShift(float shift) {
		this.outsideBadgeBrightnessShift = shift;
	}

	public float getInsideBadgeBrightnessShift() {
		return insideBadgeBrightnessShift;
	}

	public void setInsideBadgeBrightnessShift(float shift) {
		this.insideBadgeBrightnessShift = shift;
	}

	public float getBadgeContourContrast() {
		return badgeContourContrast;
	}

	public void setBadgeContourContrast(float contrast) {
		this.badgeContourContrast = contrast;
	}

	protected static interface MonochromeBadge {

		void render(Graphics2D g);

		Dimension getSize();

	}

}