package org.maia.amstrad.pc.memory;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcDevice;
import org.maia.amstrad.util.AmstradUtils;

public abstract class AmstradMemory extends AmstradPcDevice {

	private List<MemoryTrap> memoryTraps;

	private MemoryTrapTracker memoryTrapTracker;

	protected AmstradMemory(AmstradPc amstradPc) {
		super(amstradPc);
		this.memoryTraps = new Vector<MemoryTrap>();
	}

	public abstract byte read(int memoryAddress);

	public abstract byte[] readRange(int memoryOffset, int memoryLength);

	public abstract void write(int memoryAddress, byte value);

	public void writeRange(int memoryOffset, byte[] data) {
		writeRange(memoryOffset, data, 0, data.length);
	}

	public abstract void writeRange(int memoryOffset, byte[] data, int dataOffset, int dataLength);

	public synchronized void addMemoryTrap(int memoryAddress, byte memoryValueOff, boolean resetBeforeAdding,
			AmstradMemoryTrapHandler handler) {
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
			if (getMemoryTrapTracker() == null) {
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
			write(getMemoryAddress(), getMemoryValueOff());
		}

		public int getMemoryAddress() {
			return memoryAddress;
		}

		public byte getMemoryValueOff() {
			return memoryValueOff;
		}

		public byte getMemoryValue() {
			return read(getMemoryAddress());
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
			while (!stop && pc.isStarted() && !pc.isTerminated() && hasMemoryTraps()) {
				List<MemoryTrap> traps = getMemoryTrapsToTrack();
				traps.clear();
				synchronized (pc) {
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
					memoryTrap.getHandler().handleMemoryTrap(getAmstradPc(), memoryTrap.getMemoryAddress(), value);
				}
			}).start();
		}

		public void stopTracking() {
			stop = true;
		}

		private List<MemoryTrap> getMemoryTrapsToTrack() {
			return memoryTrapsToTrack;
		}

	}

}