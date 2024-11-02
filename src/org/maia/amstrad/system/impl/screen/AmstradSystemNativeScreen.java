package org.maia.amstrad.system.impl.screen;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.system.AmstradSystem;

public class AmstradSystemNativeScreen extends AmstradSystemAbstractScreen {

	public static final String SCREEN_ID = "_NATIVE";

	private boolean autohideControlKeysOverride;

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
		if (autohideControlKeysOverride) {
			return super.isAutohideControlKeys();
		} else {
			return !getAmstradSystem().getAmstradPc().getBasicRuntime().isDirectModus();
		}
	}

	@Override
	public void setAutohideControlKeys(boolean autohide) {
		super.setAutohideControlKeys(autohide);
		autohideControlKeysOverride = true;
	}

	@Override
	public final AmstradAlternativeDisplaySourceType getCustomDisplaySourceType() {
		return null;
	}

}