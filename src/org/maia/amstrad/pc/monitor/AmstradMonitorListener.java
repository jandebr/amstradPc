package org.maia.amstrad.pc.monitor;

import org.maia.amstrad.pc.AmstradPc;

public interface AmstradMonitorListener {

	void amstradMonitorModeChanged(AmstradPc amstradPc);

	void amstradMonitorEffectChanged(AmstradPc amstradPc);

	void amstradMonitorScanLinesEffectChanged(AmstradPc amstradPc);

	void amstradMonitorBilinearEffectChanged(AmstradPc amstradPc);

	void amstradWindowFullscreenChanged(AmstradPc amstradPc);

	void amstradWindowAlwaysOnTopChanged(AmstradPc amstradPc);

	void amstradWindowTitleDynamicChanged(AmstradPc amstradPc);

	void amstradDisplaySourceChanged(AmstradPc amstradPc);

}