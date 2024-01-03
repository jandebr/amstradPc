package org.maia.amstrad.system.impl.screen;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.system.AmstradSystem;

public class AmstradSystemNativeScreen extends AmstradSystemAbstractScreen {

	public static final String SCREEN_ID = "_NATIVE";

	public AmstradSystemNativeScreen(AmstradSystem amstradSystem) {
		super(SCREEN_ID, amstradSystem);
	}

	@Override
	public final boolean isNativeScreen() {
		return true;
	}

	@Override
	public final boolean isUnknownScreen() {
		return false;
	}

	@Override
	public boolean isAutohideControlKeys() {
		return !getAmstradSystem().getAmstradPc().getBasicRuntime().isDirectModus();
	}

	@Override
	public final AmstradAlternativeDisplaySource getCustomDisplaySource() {
		return null;
	}

}