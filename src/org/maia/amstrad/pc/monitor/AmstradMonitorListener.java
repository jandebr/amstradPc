package org.maia.amstrad.pc.monitor;

public interface AmstradMonitorListener {

	void amstradMonitorModeChanged(AmstradMonitor monitor);

	void amstradMonitorEffectChanged(AmstradMonitor monitor);

	void amstradMonitorScanLinesEffectChanged(AmstradMonitor monitor);

	void amstradMonitorBilinearEffectChanged(AmstradMonitor monitor);

	void amstradWindowFullscreenChanged(AmstradMonitor monitor);

	void amstradWindowAlwaysOnTopChanged(AmstradMonitor monitor);

	void amstradWindowTitleDynamicChanged(AmstradMonitor monitor);

	void amstradShowSystemStatsChanged(AmstradMonitor monitor);

	void amstradDisplaySourceChanged(AmstradMonitor monitor);

}