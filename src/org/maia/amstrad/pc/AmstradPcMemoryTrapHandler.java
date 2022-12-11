package org.maia.amstrad.pc;

public interface AmstradPcMemoryTrapHandler {

	void handleMemoryTrap(AmstradPc amstradPc, int memoryAddress, byte memoryValue);

}