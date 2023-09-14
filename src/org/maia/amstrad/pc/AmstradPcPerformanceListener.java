package org.maia.amstrad.pc;

import org.maia.amstrad.util.AmstradListener;

public interface AmstradPcPerformanceListener extends AmstradListener {

	void displayPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int framesPainted, int imagesUpdated);

	void processorPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int timerSyncs, int laggingSyncs,
			int throttledSyncs);

}