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

	public Memory(String type, int size) {
		super(type);
		this.size = size;
		this.writeObservers = new Vector<MemoryWriteObserver>();
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
		getWriteObservers().add(observer);
	}

	public synchronized void removeWriteObserver(MemoryWriteObserver observer) {
		getWriteObservers().remove(observer);
	}

	public synchronized void removeAllWriteObservers() {
		getWriteObservers().clear();
	}

	public List<MemoryWriteObserver> getWriteObservers() {
		return writeObservers;
	}

}