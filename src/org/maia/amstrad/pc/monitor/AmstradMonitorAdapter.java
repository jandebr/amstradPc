package org.maia.amstrad.pc.monitor;

public abstract class AmstradMonitorAdapter implements AmstradMonitorListener {

	protected AmstradMonitorAdapter() {
	}

	@Override
	public void amstradMonitorModeChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorEffectChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorScanLinesEffectChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorBilinearEffectChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradWindowFullscreenChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradWindowAlwaysOnTopChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradWindowTitleDynamicChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

}