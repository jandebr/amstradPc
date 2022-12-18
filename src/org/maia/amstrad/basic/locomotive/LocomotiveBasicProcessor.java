package org.maia.amstrad.basic.locomotive;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class LocomotiveBasicProcessor {

	private NumberFormat floatingPointFormat;

	protected LocomotiveBasicProcessor() {
		this.floatingPointFormat = createFloatingPointFormat();
	}

	private NumberFormat createFloatingPointFormat() {
		NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
		fmt.setMaximumFractionDigits(8);
		fmt.setGroupingUsed(false);
		return fmt;
	}

	protected LocomotiveBasicKeywords getBasicKeywords() {
		return LocomotiveBasicKeywords.getInstance();
	}

	protected NumberFormat getFloatingPointFormat() {
		return floatingPointFormat;
	}

}