package org.maia.amstrad.pc;

public interface AmstradPcMonitorListener {

	void amstradPcMonitorModeChanged(AmstradPc amstradPc);

	void amstradPcFullscreenModeChanged(AmstradPc amstradPc);

	void amstradPcDisplaySourceChanged(AmstradPc amstradPc);

}