package org.maia.amstrad.pc.memory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.maia.amstrad.pc.AmstradDevice;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.util.AmstradUtils;

public abstract class AmstradMemory extends AmstradDevice {

	private ReentrantLock exclusiveThreadUseLock;

	private List<MemoryTrap> memoryTraps;

	private MemoryTrapTracker memoryTrapTracker;

	protected AmstradMemory(AmstradPc amstradPc) {
		super(amstradPc);
		this.exclusiveThreadUseLock = new ReentrantLock(true);
		this.memoryTraps = new Vector<MemoryTrap>();
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

	public synchronized void addMemoryTrap(int memoryAddress, boolean resetBeforeAdding,
			AmstradMemoryTrapHandler handler) {
		addMemoryTrap(memoryAddress, (byte) 0, resetBeforeAdding, handler);
	}

	public synchronized void addMemoryTrap(int memoryAddress, byte memoryValueOff, boolean resetBeforeAdding,
			AmstradMemoryTrapHandler handler) {
		checkStarted();
		checkNotTerminated();
		MemoryTrap memoryTrap = new MemoryTrap(memoryAddress, memoryValueOff, handler);
		if (resetBeforeAdding) {
			memoryTrap.reset();
		}
		getMemoryTraps().add(memoryTrap);
		trackMemoryTrapsAsNeeded();
	}

	public synchronized void removeMemoryTrapsAt(int memoryAddress) {
		Iterator<MemoryTrap> it = getMemoryTraps().iterator();
		while (it.hasNext()) {
			if (it.next().getMemoryAddress() == memoryAddress)
				it.remove();
		}
		trackMemoryTrapsAsNeeded();
	}

	public synchronized void removeAllMemoryTraps() {
		getMemoryTraps().clear();
		trackMemoryTrapsAsNeeded();
	}

	private void trackMemoryTrapsAsNeeded() {
		if (hasMemoryTraps()) {
			if (getMemoryTrapTracker() == null || getMemoryTrapTracker().isStopped()) {
				MemoryTrapTracker tracker = new MemoryTrapTracker();
				setMemoryTrapTracker(tracker);
				tracker.start();
			}
		} else {
			if (getMemoryTrapTracker() != null) {
				getMemoryTrapTracker().stopTracking();
				setMemoryTrapTracker(null);
			}
		}
	}

	private boolean hasMemoryTraps() {
		return !getMemoryTraps().isEmpty();
	}

	private List<MemoryTrap> getMemoryTraps() {
		return memoryTraps;
	}

	private MemoryTrapTracker getMemoryTrapTracker() {
		return memoryTrapTracker;
	}

	private void setMemoryTrapTracker(MemoryTrapTracker tracker) {
		this.memoryTrapTracker = tracker;
	}

	private class MemoryTrap {

		private int memoryAddress;

		private byte memoryValueOff;

		private AmstradMemoryTrapHandler handler;

		public MemoryTrap(int memoryAddress, byte memoryValueOff, AmstradMemoryTrapHandler handler) {
			this.memoryAddress = memoryAddress;
			this.memoryValueOff = memoryValueOff;
			this.handler = handler;
		}

		public boolean isOn() {
			return getMemoryValue() != getMemoryValueOff();
		}

		public void reset() {
			writeByte(getMemoryAddress(), getMemoryValueOff());
		}

		public int getMemoryAddress() {
			return memoryAddress;
		}

		public byte getMemoryValueOff() {
			return memoryValueOff;
		}

		public byte getMemoryValue() {
			return readByte(getMemoryAddress());
		}

		public AmstradMemoryTrapHandler getHandler() {
			return handler;
		}

	}

	private class MemoryTrapTracker extends Thread {

		private boolean stop;

		private List<MemoryTrap> memoryTrapsToTrack;

		public MemoryTrapTracker() {
			setDaemon(true);
			this.memoryTrapsToTrack = new Vector<MemoryTrap>();
		}

		@Override
		public void run() {
			System.out.println("Memorytrap tracker thread started");
			AmstradPc pc = getAmstradPc();
			while (!isStopped() && pc.isStarted() && !pc.isTerminated() && hasMemoryTraps()) {
				List<MemoryTrap> traps = getMemoryTrapsToTrack();
				traps.clear();
				synchronized (AmstradMemory.this) {
					traps.addAll(getMemoryTraps());
				}
				track(traps);
				AmstradUtils.sleep(200L);
			}
			System.out.println("Memorytrap tracker thread stopped");
		}

		private void track(List<MemoryTrap> memoryTraps) {
			for (MemoryTrap memoryTrap : memoryTraps) {
				if (memoryTrap.isOn()) {
					byte value = memoryTrap.getMemoryValue();
					memoryTrap.reset();
					handleInSeparateThread(memoryTrap, value);
				}
			}
		}

		private void handleInSeparateThread(final MemoryTrap memoryTrap, final byte value) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					memoryTrap.getHandler().handleMemoryTrap(AmstradMemory.this, memoryTrap.getMemoryAddress(), value);
				}
			}).start();
		}

		public void stopTracking() {
			stop = true;
		}

		public boolean isStopped() {
			return stop;
		}

		private List<MemoryTrap> getMemoryTrapsToTrack() {
			return memoryTrapsToTrack;
		}

	}

}