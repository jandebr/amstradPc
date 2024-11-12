package org.maia.amstrad.system.impl.screen;

import java.util.HashSet;
import java.util.Set;

import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemScreen;
import org.maia.amstrad.system.AmstradSystemScreenSet;

public class AmstradSystemScreenSetImpl implements AmstradSystemScreenSet {

	private AmstradSystem amstradSystem;

	private AmstradSystemScreen nativeScreen;

	private AmstradSystemScreen unknownScreen;

	private Set<AmstradSystemScreen> customScreens;

	public AmstradSystemScreenSetImpl(AmstradSystem amstradSystem) {
		this.amstradSystem = amstradSystem;
		this.customScreens = new HashSet<AmstradSystemScreen>();
	}

	public void addCustomScreen(AmstradSystemScreen customScreen) {
		getCustomScreens().add(customScreen);
	}

	@Override
	public AmstradSystemScreen getNativeScreen() {
		if (nativeScreen == null) {
			nativeScreen = createDefaultNativeScreen();
		}
		return nativeScreen;
	}

	public void setNativeScreen(AmstradSystemScreen nativeScreen) {
		this.nativeScreen = nativeScreen;
	}

	protected AmstradSystemScreen createDefaultNativeScreen() {
		AmstradSystemNativeScreen screen = new AmstradSystemNativeScreen(getAmstradSystem());
		screen.setShowControlKeys(false);
		return screen;
	}

	@Override
	public AmstradSystemScreen getUnknownScreen() {
		if (unknownScreen == null) {
			unknownScreen = createDefaultUnknownScreen();
		}
		return unknownScreen;
	}

	public void setUnknownScreen(AmstradSystemScreen unknownScreen) {
		this.unknownScreen = unknownScreen;
	}

	protected AmstradSystemScreen createDefaultUnknownScreen() {
		AmstradSystemUnknownScreen screen = new AmstradSystemUnknownScreen(getAmstradSystem());
		screen.setShowControlKeys(false);
		return screen;
	}

	@Override
	public Set<AmstradSystemScreen> getCustomScreens() {
		return customScreens;
	}

	protected AmstradSystem getAmstradSystem() {
		return amstradSystem;
	}

}