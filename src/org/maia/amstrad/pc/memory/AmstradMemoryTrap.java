package org.maia.amstrad.pc.memory;

public class AmstradMemoryTrap {

	private AmstradMemory memory;

	private int memoryAddress;

	private byte memoryValueOff;

	private AmstradMemoryTrapHandler handler;

	public AmstradMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValueOff,
			AmstradMemoryTrapHandler handler) {
		this.memory = memory;
		this.memoryAddress = memoryAddress;
		this.memoryValueOff = memoryValueOff;
		this.handler = handler;
	}

	public boolean isOn() {
		return getMemoryValue() != getMemoryValueOff();
	}

	public void reset() {
		getMemory().writeByte(getMemoryAddress(), getMemoryValueOff());
	}

	public AmstradMemory getMemory() {
		return memory;
	}

	public int getMemoryAddress() {
		return memoryAddress;
	}

	public byte getMemoryValueOff() {
		return memoryValueOff;
	}

	public byte getMemoryValue() {
		return getMemory().readByte(getMemoryAddress());
	}

	public AmstradMemoryTrapHandler getHandler() {
		return handler;
	}

}