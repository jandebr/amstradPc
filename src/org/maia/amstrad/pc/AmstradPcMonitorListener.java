package org.maia.amstrad.pc;

public interface AmstradPcMonitorListener {

	void amstradPcMonitorModeChanged(AmstradPc amstradPc);

	void amstradPcMonitorEffectChanged(AmstradPc amstradPc);

	void amstradPcMonitorScanLinesEffectChanged(AmstradPc amstradPc);

	void amstradPcMonitorBilinearEffectChanged(AmstradPc amstradPc);

	void amstradPcWindowFullscreenChanged(AmstradPc amstradPc);

	void amstradPcWindowAlwaysOnTopChanged(AmstradPc amstradPc);

	void amstradPcWindowTitleDynamicChanged(AmstradPc amstradPc);

	void amstradPcDisplaySourceChanged(AmstradPc amstradPc);

}