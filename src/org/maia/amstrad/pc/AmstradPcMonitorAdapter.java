package org.maia.amstrad.pc;

public abstract class AmstradPcMonitorAdapter implements AmstradPcMonitorListener {

	protected AmstradPcMonitorAdapter() {
	}

	@Override
	public void amstradPcMonitorModeChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcMonitorEffectChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcMonitorScanLinesEffectChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcMonitorBilinearEffectChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcWindowFullscreenChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcWindowAlwaysOnTopChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcWindowTitleDynamicChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

}