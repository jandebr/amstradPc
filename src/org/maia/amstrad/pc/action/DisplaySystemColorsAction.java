package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.display.AmstradSystemColorsDisplaySource;

public class DisplaySystemColorsAction extends AmstradPcAction {

	private AmstradSystemColorsDisplaySource displaySource;

	private static String NAME_OPEN = "Show Amstrad colors";

	private static String NAME_CLOSE = "Hide Amstrad colors";

	public DisplaySystemColorsAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.addMonitorListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleSystemColors();
	}

	public void toggleSystemColors() {
		if (NAME_OPEN.equals(getName())) {
			showSystemColors();
		} else {
			hideSystemColors();
		}
	}

	public void showSystemColors() {
		if (isEnabled()) {
			getAmstradPc().swapDisplaySource(getDisplaySource());
		}
	}

	public void hideSystemColors() {
		if (isEnabled()) {
			getAmstradPc().resetDisplaySource();
		}
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		super.amstradPcDisplaySourceChanged(amstradPc);
		updateName();
	}

	private void updateName() {
		if (isSystemColorsShowing()) {
			changeName(NAME_CLOSE);
		} else {
			changeName(NAME_OPEN);
		}
	}

	public boolean isSystemColorsShowing() {
		AmstradAlternativeDisplaySource altDisplaySource = getAmstradPc().getCurrentAlternativeDisplaySource();
		return altDisplaySource != null && altDisplaySource instanceof AmstradSystemColorsDisplaySource;
	}

	private AmstradSystemColorsDisplaySource getDisplaySource() {
		if (displaySource == null) {
			displaySource = new AmstradSystemColorsDisplaySource(getAmstradPc());
		}
		return displaySource;
	}

}