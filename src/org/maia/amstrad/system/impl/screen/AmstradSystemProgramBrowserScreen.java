package org.maia.amstrad.system.impl.screen;

import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.system.AmstradSystem;

public class AmstradSystemProgramBrowserScreen extends AmstradSystemCustomScreen {

	private boolean showMonitorOverride;

	private boolean showControlKeysOverride;

	private static final String SCREEN_ID = "PROGRAM_BROWSER";

	public AmstradSystemProgramBrowserScreen(AmstradSystem amstradSystem) {
		super(SCREEN_ID, amstradSystem, AmstradAlternativeDisplaySourceType.PROGRAM_BROWSER);
		setAutohideControlKeys(false);
	}

	@Override
	public boolean isShowMonitor() {
		if (showMonitorOverride) {
			return super.isShowMonitor();
		} else {
			return getAmstradSystem().getProgramBrowser().isShowMonitor();
		}
	}

	@Override
	public void setShowMonitor(boolean show) {
		super.setShowMonitor(show);
		showMonitorOverride = true;
	}

	@Override
	public boolean isShowControlKeys() {
		if (showControlKeysOverride) {
			return super.isShowControlKeys();
		} else {
			return getAmstradSystem().getProgramBrowser().isShowControlKeys();
		}
	}

	@Override
	public void setShowControlKeys(boolean show) {
		super.setShowControlKeys(show);
		showControlKeysOverride = true;
	}

}