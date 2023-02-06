package jemu.core.device.memory;

public interface MemoryWriteObserver {

	int getObservedMemoryAddress();

	void notifyWrite(int memoryAddress, byte memoryValue);

}