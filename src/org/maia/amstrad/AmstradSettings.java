package org.maia.amstrad;

import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

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

	public boolean getBool(String key, boolean defaultValue) {
		return Boolean.parseBoolean(get(key, String.valueOf(defaultValue)));
	}

	public abstract void set(String key, String value);

	public void setBool(String key, boolean value) {
		set(key, String.valueOf(value));
	}

	public abstract void flush();

	public abstract void reset();

}