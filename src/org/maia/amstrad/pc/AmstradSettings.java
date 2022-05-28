package org.maia.amstrad.pc;

import jemu.settings.Settings;

public abstract class AmstradSettings {

	protected AmstradSettings() {
	}

	public AmstradMonitorMode getMonitorMode() {
		AmstradMonitorMode mode = AmstradMonitorMode.COLOR;
		String monitor = get(Settings.MONITOR, "");
		if (Settings.MONITOR_COLOUR.equals(monitor)) {
			mode = AmstradMonitorMode.COLOR;
		} else if (Settings.MONITOR_GREEN.equals(monitor)) {
			mode = AmstradMonitorMode.GREEN;
		} else if (Settings.MONITOR_GRAY.equals(monitor)) {
			mode = AmstradMonitorMode.GRAY;
		}
		return mode;
	}
	
	public abstract String get(String key, String defaultValue);

	public abstract void set(String key, String value);

	public abstract void reset();

}