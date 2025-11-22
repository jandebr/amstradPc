package org.maia.amstrad.gui.covers.stock.fabric;

import java.awt.Color;
import java.util.List;
import java.util.Vector;

public class AmfCheckerboardPatchPatternGenerator extends CheckerboardPatchPatternGenerator {

	public AmfCheckerboardPatchPatternGenerator() {
	}

	@Override
	protected Range createColorTransparencyRange() {
		return new Range(0.3f);
	}

	@Override
	protected Color createPrimaryColor(FabricHints hints) {
		return Color.getHSBColor(4f / 360f, 1.0f, 0.7f);
	}

	@Override
	protected List<SecondaryColorDerivation> createSecondaryColorDerivations() {
		List<SecondaryColorDerivation> derivations = new Vector<SecondaryColorDerivation>(2);
		derivations.add(new SecondaryColorDerivation(new Range(-0.8f), new Range(-0.2f)));
		derivations.add(new SecondaryColorDerivation(new Range(-0.8f), new Range(0.8f)));
		return derivations;
	}

	@Override
	protected Range createStripeSequenceCountRange() {
		return new Range(2.0f, 3.0f);
	}

	@Override
	protected Range createStripeRelativeWidthRange(int stripeIndex) {
		if (stripeIndex == 0) {
			return new Range(0.17f);
		} else if (stripeIndex == 1) {
			return new Range(0.085f);
		} else if (stripeIndex == 2) {
			return new Range(0.13f);
		} else {
			return super.createStripeRelativeWidthRange(stripeIndex);
		}
	}

	@Override
	protected boolean hasSlimline() {
		return true;
	}

	@Override
	protected Range getSlimlineRelativeMarginRange() {
		return new Range(0.2f);
	}

	@Override
	protected Range getSlimlineRelativeWidthRange() {
		return new Range(0.2f);
	}

	@Override
	protected Range getFirstVerticalStripeFractionRange() {
		return new Range(0.9f);
	}

	@Override
	protected Range getFirstHorizontalStripeFractionRange() {
		return new Range(0.2f);
	}

}