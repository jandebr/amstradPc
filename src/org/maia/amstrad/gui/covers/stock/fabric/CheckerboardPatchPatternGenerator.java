package org.maia.amstrad.gui.covers.stock.fabric;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

import org.maia.util.ColorUtils;
import org.maia.util.Randomizer;

public class CheckerboardPatchPatternGenerator extends FabricPatchPatternGenerator {

	public CheckerboardPatchPatternGenerator() {
		this(new Randomizer());
	}

	public CheckerboardPatchPatternGenerator(Randomizer randomizer) {
		super(randomizer);
	}

	@Override
	public FabricPatchPattern generatePattern(int width, int height, FabricHints hints) {
		FabricPatchPattern pattern = new FabricPatchPattern(hints.getBackgroundColor(Color.WHITE));
		pattern.setCurvedEdges(true);
		List<Color> colors = createStripeColorSequence(hints);
		List<Stripe> stripeSequence = createStripeSequence(colors);
		Slimline slimline = createSlimline(stripeSequence, width, height);
		addVerticalStripes(pattern, stripeSequence, slimline, width, height);
		addHorizontalStripes(pattern, stripeSequence, slimline, width, height);
		return pattern;
	}

	private List<Color> createStripeColorSequence(FabricHints hints) {
		float transparency = createColorTransparencyRange().drawValue();
		List<SecondaryColorDerivation> derivations = createSecondaryColorDerivations();
		List<Color> colors = new Vector<Color>(1 + derivations.size());
		Color primaryColor = createPrimaryColor(hints);
		colors.add(ColorUtils.setTransparency(primaryColor, transparency));
		for (SecondaryColorDerivation derivation : derivations) {
			Color secondaryColor = derivation.deriveSecondaryColor(primaryColor);
			colors.add(ColorUtils.setTransparency(secondaryColor, transparency));
		}
		return colors;
	}

	protected Range createColorTransparencyRange() {
		return new Range(0.25f, 0.35f);
	}

	protected Color createPrimaryColor(FabricHints hints) {
		Color primaryColor = hints.getBaseColor();
		if (primaryColor == null) {
			primaryColor = createPrimaryColorChooser().choosePrimaryColor();
		}
		return primaryColor;
	}

	protected PrimaryColorChooser createPrimaryColorChooser() {
		Range hueRange = new Range(0f, 1.0f);
		Range saturationRange = new Range(0.9f, 1.0f);
		Range brightnessRange = new Range(0.5f, 0.8f);
		return new PrimaryColorChooser(hueRange, saturationRange, brightnessRange);
	}

	protected List<SecondaryColorDerivation> createSecondaryColorDerivations() {
		List<SecondaryColorDerivation> derivations = new Vector<SecondaryColorDerivation>(2);
		if (drawBoolean()) {
			derivations.add(new SecondaryColorDerivation(new Range(-0.8f, -0.2f), new Range(-0.4f, -0.2f)));
		}
		if (drawBoolean()) {
			derivations.add(new SecondaryColorDerivation(new Range(-0.8f, -0.2f), new Range(-0.9f, -0.5f)));
		}
		derivations.add(new SecondaryColorDerivation(new Range(-0.8f, -0.2f), new Range(0.6f, 0.8f)));
		return derivations;
	}

	private List<Stripe> createStripeSequence(List<Color> colors) {
		int n = colors.size();
		float[] widths = createStripeRelativeWidths(n);
		List<Stripe> stripeSequence = new Vector<Stripe>(n);
		for (int i = 0; i < n; i++) {
			stripeSequence.add(new Stripe(colors.get(i), widths[i]));
		}
		return stripeSequence;
	}

	private float[] createStripeRelativeWidths(int n) {
		float[] widths = new float[n];
		float totalWidth, sequences;
		Range sequencesRange = createStripeSequenceCountRange();
		do {
			totalWidth = 0f;
			for (int i = 0; i < n; i++) {
				widths[i] = createStripeRelativeWidthRange(i).drawValue();
				totalWidth += widths[i];
			}
			sequences = 1.0f / totalWidth;
		} while (!sequencesRange.contains(sequences));
		return widths;
	}

	protected Range createStripeSequenceCountRange() {
		return new Range(1.5f, 3.5f);
	}

	protected Range createStripeRelativeWidthRange(int stripeIndex) {
		return new Range(0.05f, 0.35f);
	}

	private Slimline createSlimline(List<Stripe> stripeSequence, int width, int height) {
		Slimline slimline = null;
		if (hasSlimline()) {
			Color primaryColor = stripeSequence.get(0).getColor();
			Stripe lastStripe = stripeSequence.get(stripeSequence.size() - 1);
			int lastStripeWidth = Math.round(lastStripe.getRelativeWidth() * Math.min(width, height));
			int slimlineMargin = Math.max(2,
					Math.round(lastStripeWidth * getSlimlineRelativeMarginRange().drawValue()));
			int slimlineWidth = Math.max(2, Math.round(lastStripeWidth * getSlimlineRelativeWidthRange().drawValue()));
			slimline = new Slimline(primaryColor, slimlineMargin, slimlineWidth);
		}
		return slimline;
	}

	protected boolean hasSlimline() {
		return drawBoolean();
	}

	protected Range getSlimlineRelativeMarginRange() {
		return new Range(0.1f, 0.3f);
	}

	protected Range getSlimlineRelativeWidthRange() {
		return new Range(0.15f, 0.25f);
	}

	private void addVerticalStripes(FabricPatchPattern pattern, List<Stripe> stripeSequence, Slimline slimline,
			int width, int height) {
		List<Integer> stripeOffsets = getStripeOffsets(stripeSequence, getFirstVerticalStripeFractionRange(), width,
				Math.min(width, height) / (float) width);
		int sn = stripeSequence.size();
		int n = stripeOffsets.size();
		for (int i = 0; i < n; i++) {
			int stripeOffset = stripeOffsets.get(i);
			int stripeWidth = (i < n - 1 ? stripeOffsets.get(i + 1) : width) - stripeOffset;
			Color stripeColor = stripeSequence.get(i % sn).getColor();
			if (slimline != null && (i + 1) % sn == 0
					&& stripeOffset + slimline.getMargin() + slimline.getWidth() <= width - 4) {
				int slimlineOffset = stripeOffset + slimline.getMargin();
				int slimlineEnd = slimlineOffset + slimline.getWidth();
				pattern.addPatch(new FabricPatch(stripeColor, stripeOffset, 0, slimline.getMargin(), height));
				pattern.addPatch(new FabricPatch(slimline.getColor(), slimlineOffset, 0, slimline.getWidth(), height));
				pattern.addPatch(new FabricPatch(stripeColor, slimlineEnd, 0,
						stripeWidth - slimline.getMargin() - slimline.getWidth(), height));
			} else {
				pattern.addPatch(new FabricPatch(stripeColor, stripeOffset, 0, stripeWidth, height));
			}
		}
	}

	private void addHorizontalStripes(FabricPatchPattern pattern, List<Stripe> stripeSequence, Slimline slimline,
			int width, int height) {
		List<Integer> stripeOffsets = getStripeOffsets(stripeSequence, getFirstHorizontalStripeFractionRange(), height,
				Math.min(width, height) / (float) height);
		int sn = stripeSequence.size();
		int n = stripeOffsets.size();
		for (int i = 0; i < n; i++) {
			int stripeOffset = stripeOffsets.get(i);
			int stripeWidth = (i < n - 1 ? stripeOffsets.get(i + 1) : height) - stripeOffset;
			int y0 = height - stripeOffset;
			Color stripeColor = stripeSequence.get(i % sn).getColor();
			if (slimline != null && (i + 1) % sn == 0
					&& stripeOffset + slimline.getMargin() + slimline.getWidth() <= height - 4) {
				int slimlineOffset = y0 - slimline.getMargin();
				int slimlineEnd = slimlineOffset - slimline.getWidth();
				pattern.addPatch(new FabricPatch(stripeColor, 0, y0 - stripeWidth, width,
						stripeWidth - slimline.getMargin() - slimline.getWidth()));
				pattern.addPatch(new FabricPatch(slimline.getColor(), 0, slimlineEnd, width, slimline.getWidth()));
				pattern.addPatch(new FabricPatch(stripeColor, 0, slimlineOffset, width, slimline.getMargin()));
			} else {
				pattern.addPatch(new FabricPatch(stripeColor, 0, y0 - stripeWidth, width, stripeWidth));
			}
		}
	}

	private List<Integer> getStripeOffsets(List<Stripe> stripeSequence, Range firstStripeFractionRange, int totalWidth,
			float scale) {
		List<Integer> stripeOffsets = new Vector<Integer>();
		int stripeIndex = 0;
		int stripeOffset = 0;
		while (stripeOffset < totalWidth) {
			Stripe stripe = stripeSequence.get(stripeIndex % stripeSequence.size());
			int stripeWidth = Math.round(stripe.getRelativeWidth() * totalWidth * scale);
			if (stripeIndex == 0) {
				stripeWidth = Math.round(stripeWidth * firstStripeFractionRange.drawValue());
			}
			stripeWidth = Math.min(stripeWidth, totalWidth - stripeOffset);
			if (stripeOffset + stripeWidth >= totalWidth - 4) {
				stripeWidth = totalWidth - stripeOffset;
			}
			stripeOffsets.add(stripeOffset);
			stripeOffset += stripeWidth;
			stripeIndex++;
		}
		return stripeOffsets;
	}

	protected Range getFirstVerticalStripeFractionRange() {
		return new Range(0.1f, 0.9f);
	}

	protected Range getFirstHorizontalStripeFractionRange() {
		return new Range(0.1f, 0.9f);
	}

	protected class Range {

		private float minimumValue;

		private float maximumValue;

		public Range(float singularValue) {
			this(singularValue, singularValue);
		}

		public Range(float minimumValue, float maximumValue) {
			this.minimumValue = minimumValue;
			this.maximumValue = maximumValue;
		}

		public boolean contains(float value) {
			return value >= getMinimumValue() && value <= getMaximumValue();
		}

		public float drawValue() {
			float min = getMinimumValue();
			float max = getMaximumValue();
			return min + (max - min) * getRandomizer().drawFloatUnitNumber();
		}

		public float getMinimumValue() {
			return minimumValue;
		}

		public float getMaximumValue() {
			return maximumValue;
		}

	}

	protected class PrimaryColorChooser {

		private Range hueRange;

		private Range saturationRange;

		private Range brightnessRange;

		public PrimaryColorChooser(Range hueRange, Range saturationRange, Range brightnessRange) {
			this.hueRange = hueRange;
			this.saturationRange = saturationRange;
			this.brightnessRange = brightnessRange;
		}

		public Color choosePrimaryColor() {
			float hue = getHueRange().drawValue();
			float saturation = getSaturationRange().drawValue();
			float brightness = getBrightnessRange().drawValue();
			return new Color(Color.HSBtoRGB(hue, saturation, brightness));
		}

		public Range getHueRange() {
			return hueRange;
		}

		public Range getSaturationRange() {
			return saturationRange;
		}

		public Range getBrightnessRange() {
			return brightnessRange;
		}

	}

	protected class SecondaryColorDerivation {

		private Range saturationDeltaRange;

		private Range brightnessDeltaRange;

		public SecondaryColorDerivation(Range saturationDeltaRange, Range brightnessDeltaRange) {
			this.saturationDeltaRange = saturationDeltaRange;
			this.brightnessDeltaRange = brightnessDeltaRange;
		}

		public Color deriveSecondaryColor(Color primaryColor) {
			float saturationDelta = getSaturationDeltaRange().drawValue();
			float brightnessDelta = getBrightnessDeltaRange().drawValue();
			return ColorUtils.adjustSaturationAndBrightness(primaryColor, saturationDelta, brightnessDelta);
		}

		public Range getSaturationDeltaRange() {
			return saturationDeltaRange;
		}

		public Range getBrightnessDeltaRange() {
			return brightnessDeltaRange;
		}

	}

	private static class Stripe {

		private Color color;

		private float relativeWidth;

		public Stripe(Color color, float relativeWidth) {
			this.color = color;
			this.relativeWidth = relativeWidth;
		}

		public Color getColor() {
			return color;
		}

		public float getRelativeWidth() {
			return relativeWidth;
		}

	}

	private static class Slimline {

		private Color color;

		private int margin;

		private int width;

		public Slimline(Color color, int margin, int width) {
			this.color = color;
			this.margin = margin;
			this.width = width;
		}

		public Color getColor() {
			return color;
		}

		public int getMargin() {
			return margin;
		}

		public int getWidth() {
			return width;
		}

	}

}