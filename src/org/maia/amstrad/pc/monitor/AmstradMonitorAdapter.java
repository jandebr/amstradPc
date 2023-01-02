package org.maia.amstrad.pc.monitor;

import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradMonitorAdapter implements AmstradMonitorListener {

	protected AmstradMonitorAdapter() {
	}

	@Override
	public void amstradMonitorModeChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorEffectChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorScanLinesEffectChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradMonitorBilinearEffectChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradWindowFullscreenChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradWindowAlwaysOnTopChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradWindowTitleDynamicChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

}