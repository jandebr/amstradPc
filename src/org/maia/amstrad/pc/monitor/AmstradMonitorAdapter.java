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
	public void amstradMonitorGateArraySizeChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorAutoHideCursorChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorSizeChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorFullscreenChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradWindowAlwaysOnTopChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradShowSystemStatsChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		// Subclasses can override this
	}

}