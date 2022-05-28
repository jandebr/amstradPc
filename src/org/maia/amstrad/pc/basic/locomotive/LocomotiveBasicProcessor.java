package org.maia.amstrad.pc.basic.locomotive;

import java.text.NumberFormat;
import java.util.Locale;

public abstract class LocomotiveBasicProcessor {

	private LocomotiveTokenMap tokenMap;

	private NumberFormat floatingPointFormat;

	protected LocomotiveBasicProcessor() {
		this.tokenMap = new LocomotiveTokenMap();
		this.floatingPointFormat = createFloatingPointFormat();
	}

	private NumberFormat createFloatingPointFormat() {
		NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
		fmt.setMaximumFractionDigits(8);
		fmt.setGroupingUsed(false);
		return fmt;
	}

	protected LocomotiveTokenMap getTokenMap() {
		return tokenMap;
	}

	protected NumberFormat getFloatingPointFormat() {
		return floatingPointFormat;
	}

}