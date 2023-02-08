package jemu.core.device.memory;

import java.util.List;
import java.util.Vector;

import jemu.core.device.Device;

/**
 * Title: JEMU Description: The Java Emulation Platform Copyright: Copyright (c) 2002 Company:
 * 
 * @author
 * @version 1.0
 */

public abstract class Memory extends Device {

	protected int size;

	private List<MemoryWriteObserver> writeObservers;

	private boolean[] writeObserversMask;

	public Memory(String type, int size) {
		super(type);
		this.size = size;
		this.writeObservers = new Vector<MemoryWriteObserver>();
		this.writeObserversMask = new boolean[size];
	}

	public int getAddressSize() {
		return size;
	}

	public int readByte(int address, Object config) {
		return readByte(address);
	}

	public void writeByte(int address, int value, Object config) {
		writeByte(address, value);
	}

	public synchronized void addWriteObserver(MemoryWriteObserver observer) {
		int addr = observer.getObservedMemoryAddress();
		if (addr < 0 || addr >= getAddressSize())
			throw new IllegalArgumentException(
					"Observed memory address is out of range: " + addr + " (max " + getAddressSize() + ")");
		if (!getWriteObservers().contains(observer)) {
			getWriteObservers().add(observer);
			writeObserversMask[addr] = true;
		}
	}

	public synchronized void removeWriteObserver(MemoryWriteObserver observer) {
		if (getWriteObservers().remove(observer)) {
			int addr = observer.getObservedMemoryAddress();
			boolean moreOnAddr = false;
			for (MemoryWriteObserver obs : getWriteObservers()) {
				if (obs.getObservedMemoryAddress() == addr) {
					moreOnAddr = true;
					break;
				}
			}
			if (!moreOnAddr) {
				writeObserversMask[addr] = false;
			}
		}
	}

	public synchronized void removeAllWriteObservers() {
		if (!getWriteObservers().isEmpty()) {
			for (MemoryWriteObserver obs : getWriteObservers()) {
				writeObserversMask[obs.getObservedMemoryAddress()] = false;
			}
			getWriteObservers().clear();
		}
	}

	protected boolean hasWriteObserversAt(int memoryAddress) {
		return memoryAddress >= 0 && memoryAddress < writeObserversMask.length && writeObserversMask[memoryAddress];
	}

	public List<MemoryWriteObserver> getWriteObservers() {
		return writeObservers;
	}

}