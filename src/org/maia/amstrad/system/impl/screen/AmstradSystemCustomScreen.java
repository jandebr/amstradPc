package org.maia.amstrad.system.impl.screen;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.system.AmstradSystem;

public class AmstradSystemCustomScreen extends AmstradSystemAbstractScreen {

	private AmstradAlternativeDisplaySource customDisplaySource;

	public AmstradSystemCustomScreen(String screenIdentifier, AmstradSystem amstradSystem,
			AmstradAlternativeDisplaySource customDisplaySource) {
		super(screenIdentifier, amstradSystem);
		if (customDisplaySource == null)
			throw new NullPointerException("Custom display source is null");
		this.customDisplaySource = customDisplaySource;
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
	public AmstradAlternativeDisplaySource getCustomDisplaySource() {
		return customDisplaySource;
	}

}