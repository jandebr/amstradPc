package org.maia.amstrad.pc;

public abstract class AmstradPcMonitorAdapter implements AmstradPcMonitorListener {

	protected AmstradPcMonitorAdapter() {
	}

	@Override
	public void amstradPcMonitorModeChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcFullscreenModeChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc) {
		// Subclasses can override this
	}

}