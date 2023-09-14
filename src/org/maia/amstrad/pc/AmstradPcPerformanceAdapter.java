package org.maia.amstrad.pc;

public abstract class AmstradPcPerformanceAdapter implements AmstradPcPerformanceListener {

	protected AmstradPcPerformanceAdapter() {
	}

	@Override
	public void displayPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int framesPainted,
			int imagesUpdated) {
		// Subclasses can override this
	}

	@Override
	public void processorPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int timerSyncs,
			int laggingSyncs, int throttledSyncs) {
		// Subclasses can override this
	}

}