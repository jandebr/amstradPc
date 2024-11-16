package org.maia.amstrad.pc.action;

import org.maia.amstrad.gui.colors.AmstradSystemColorsDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public class AmstradSystemColorsDisplayAction extends ToggleDisplaySourceAction {

	private AmstradSystemColorsDisplaySource displaySource;

	public AmstradSystemColorsDisplayAction(AmstradPc amstradPc) {
		super(amstradPc, "Show Amstrad colors", "Hide Amstrad colors");
	}

	@Override
	protected boolean isDisplaySourceShowing() {
		return isSystemColorsShowing();
	}

	public boolean isSystemColorsShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getMonitor()
				.getCurrentAlternativeDisplaySource();
		return altDisplaySource != null && altDisplaySource instanceof AmstradSystemColorsDisplaySource;
	}

	@Override
	protected AmstradSystemColorsDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = new AmstradSystemColorsDisplaySource(getAmstradPc());
		}
		return displaySource;
	}

}