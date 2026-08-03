package org.maia.amstrad.system.impl.screen;

import java.util.List;

import org.maia.amstrad.gui.overlay.controlkeys.ControlKey;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.system.AmstradSystem;

public class AmstradSystemProgramBrowserScreen extends AmstradSystemCustomScreen {

	private boolean showMonitorOverride;

	private boolean monitorResizableOverride;

	private boolean showPauseOverride;

	private boolean showControlKeysOverride;

	private boolean additionalControlKeysOverride;

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
	public boolean isMonitorResizable() {
		if (monitorResizableOverride) {
			return super.isMonitorResizable();
		} else {
			return getAmstradSystem().getProgramBrowser().isMonitorResizable();
		}
	}

	@Override
	public void setMonitorResizable(boolean resizable) {
		super.setMonitorResizable(resizable);
		monitorResizableOverride = true;
	}

	@Override
	public boolean isShowPause() {
		if (showPauseOverride) {
			return super.isShowPause();
		} else {
			return getAmstradSystem().getProgramBrowser().isShowPause();
		}
	}

	@Override
	public void setShowPause(boolean show) {
		super.setShowPause(show);
		showPauseOverride = true;
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

	@Override
	public List<ControlKey> getAdditionalControlKeys() {
		if (additionalControlKeysOverride) {
			return super.getAdditionalControlKeys();
		} else {
			return getAmstradSystem().getProgramBrowser().getAdditionalControlKeys();
		}
	}

	@Override
	public void setAdditionalControlKeys(List<ControlKey> controlKeys) {
		super.setAdditionalControlKeys(controlKeys);
		additionalControlKeysOverride = true;
	}

}