package org.maia.amstrad.system.impl.screen;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.system.AmstradSystem;

public class AmstradSystemCustomScreen extends AmstradSystemAbstractScreen {

	private AmstradAlternativeDisplaySourceType customDisplaySourceType;

	public AmstradSystemCustomScreen(String screenIdentifier, AmstradSystem amstradSystem,
			AmstradAlternativeDisplaySourceType customDisplaySourceType) {
		super(screenIdentifier, amstradSystem);
		if (customDisplaySourceType == null)
			throw new NullPointerException("Custom display source type is null");
		this.customDisplaySourceType = customDisplaySourceType;
	}

	@Override
	public final boolean isNativeScreen() {
		return false;
	}

	@Override
	public final boolean isUnknownScreen() {
		return false;
	}

	@Override
	public AmstradAlternativeDisplaySourceType getCustomDisplaySourceType() {
		return customDisplaySourceType;
	}

}