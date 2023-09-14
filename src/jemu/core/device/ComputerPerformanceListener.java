package jemu.core.device;

public interface ComputerPerformanceListener {

	void processorPerformanceUpdate(Computer computer, long timeIntervalMillis, int timerSyncs, int laggingSyncs,
			int throttledSyncs);

}