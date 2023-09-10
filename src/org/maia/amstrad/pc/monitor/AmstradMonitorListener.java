package org.maia.amstrad.pc.monitor;

import org.maia.amstrad.util.AmstradListener;

public interface AmstradMonitorListener extends AmstradListener {

	void amstradMonitorModeChanged(AmstradMonitor monitor);

	void amstradMonitorEffectChanged(AmstradMonitor monitor);

	void amstradMonitorScanLinesEffectChanged(AmstradMonitor monitor);

	void amstradMonitorBilinearEffectChanged(AmstradMonitor monitor);

	void amstradWindowFullscreenChanged(AmstradMonitor monitor);

	void amstradWindowAlwaysOnTopChanged(AmstradMonitor monitor);

	void amstradShowSystemStatsChanged(AmstradMonitor monitor);

	void amstradDisplaySourceChanged(AmstradMonitor monitor);

}