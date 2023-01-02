package org.maia.amstrad.pc.memory;

public interface AmstradMemoryTrapHandler {

	void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue);

}