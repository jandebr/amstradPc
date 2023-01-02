package org.maia.amstrad.pc.memory;

import org.maia.amstrad.pc.AmstradPc;

public interface AmstradMemoryTrapHandler {

	void handleMemoryTrap(AmstradPc amstradPc, int memoryAddress, byte memoryValue);

}