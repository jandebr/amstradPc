package jemu.core.device;

public interface ComputerPerformanceListener {

	void displayPerformanceUpdate(Computer computer, long timeIntervalMillis, int framesPainted, int framesSkipped);

	void processorPerformanceUpdate(Computer computer, long timeIntervalMillis, int timerSyncs, int laggingSyncs,
			int throttledSyncs);

}