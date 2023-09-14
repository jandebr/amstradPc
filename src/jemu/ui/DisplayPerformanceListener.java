package jemu.ui;

public interface DisplayPerformanceListener {

	void displayPerformanceUpdate(Display display, long timeIntervalMillis, int framesPainted, int imagesUpdated);

}