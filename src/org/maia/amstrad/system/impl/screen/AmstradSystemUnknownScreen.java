package org.maia.amstrad.system.impl.screen;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.system.AmstradSystem;

public class AmstradSystemUnknownScreen extends AmstradSystemAbstractScreen {

	public static final String SCREEN_ID = "_UNKNOWN";

	public AmstradSystemUnknownScreen(AmstradSystem amstradSystem) {
		super(SCREEN_ID, amstradSystem);
	}

	@Override
	public final boolean isNativeScreen() {
		return false;
	}

	@Override
	public final boolean isUnknownScreen() {
		return true;
	}

	@Override
	public final AmstradAlternativeDisplaySource getCustomDisplaySource() {
		return null;
	}

}