package org.maia.amstrad.pc.memory;

import java.util.concurrent.locks.ReentrantLock;

import org.maia.amstrad.pc.AmstradPcDevice;
import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradMemory extends AmstradPcDevice {

	private ReentrantLock exclusiveThreadUseLock;

	protected AmstradMemory(AmstradPc amstradPc) {
		super(amstradPc);
		this.exclusiveThreadUseLock = new ReentrantLock(true);
	}

	public void startThreadExclusiveSession() {
		this.exclusiveThreadUseLock.lock();
		// Subclasses may want to extend this
	}

	public void endThreadExclusiveSession() {
		// Subclasses may want to extend this
		this.exclusiveThreadUseLock.unlock();
	}

	protected final boolean isNestedThreadExclusiveSession() {
		return this.exclusiveThreadUseLock.getHoldCount() > 1;
	}

	public abstract byte readByte(int memoryAddress);

	public abstract byte[] readBytes(int memoryOffset, int memoryLength);

	public int readWord(int memoryAddress) {
		// little Endian
		byte b1 = readByte(memoryAddress);
		byte b2 = readByte(memoryAddress + 1);
		return (b1 & 0xff) | ((b2 << 8) & 0xff00);
	}

	public abstract void writeByte(int memoryAddress, byte value);

	public void writeWord(int memoryOffset, int value) {
		// little Endian
		byte b1 = (byte) (value % 256);
		byte b2 = (byte) (value / 256);
		writeByte(memoryOffset, b1);
		writeByte(memoryOffset + 1, b2);
	}

	public void writeBytes(int memoryOffset, byte[] data) {
		writeBytes(memoryOffset, data, 0, data.length);
	}

	public abstract void writeBytes(int memoryOffset, byte[] data, int dataOffset, int dataLength);

	public void eraseByte(int memoryAddress) {
		writeByte(memoryAddress, (byte) 0);
	}

	public void eraseWord(int memoryAddress) {
		writeWord(memoryAddress, 0);
	}

	public void eraseBytes(int memoryOffset, int memoryLength) {
		if (memoryLength == 1) {
			eraseByte(memoryOffset);
		} else if (memoryLength == 2) {
			eraseWord(memoryOffset);
		} else if (memoryLength > 2) {
			writeBytes(memoryOffset, new byte[memoryLength]);
		}
	}

	public void eraseBytesBetween(int memoryAddressStartInclusive, int memoryAddressEndExclusive) {
		eraseBytes(memoryAddressStartInclusive, memoryAddressEndExclusive - memoryAddressStartInclusive);
	}

	public final void addMemoryTrap(int memoryAddress, boolean resetBeforeAdding, AmstradMemoryTrapHandler handler) {
		addMemoryTrap(memoryAddress, (byte) 0, resetBeforeAdding, handler);
	}

	public final void addMemoryTrap(int memoryAddress, byte memoryValueOff, boolean resetBeforeAdding,
			AmstradMemoryTrapHandler handler) {
		checkStarted();
		checkNotTerminated();
		AmstradMemoryTrap memoryTrap = new AmstradMemoryTrap(this, memoryAddress, memoryValueOff, handler);
		if (resetBeforeAdding) {
			memoryTrap.reset();
		}
		addMemoryTrap(memoryTrap);
	}

	protected abstract void addMemoryTrap(AmstradMemoryTrap memoryTrap);

	public abstract void removeMemoryTrapsAt(int memoryAddress);

	public abstract void removeAllMemoryTraps();

}