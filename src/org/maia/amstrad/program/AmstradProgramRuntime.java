package org.maia.amstrad.program;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcProgramListener;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.util.AmstradUtils;

public abstract class AmstradProgramRuntime extends AmstradPcStateAdapter implements AmstradPcProgramListener {

	private AmstradProgram program;

	private AmstradPc amstradPc;

	private List<MemoryTrap> memoryTraps;

	private boolean run;

	private boolean disposed;

	private List<AmstradProgramRuntimeListener> listeners;

	private static MemoryTrapTracker memoryTrapTracker;

	protected AmstradProgramRuntime(AmstradProgram program, AmstradPc amstradPc) {
		this.program = program;
		this.amstradPc = amstradPc;
		this.memoryTraps = new Vector<MemoryTrap>();
		this.listeners = new Vector<AmstradProgramRuntimeListener>();
		amstradPc.addStateListener(this);
		amstradPc.addProgramListener(this);
	}

	public void addListener(AmstradProgramRuntimeListener listener) {
		getListeners().add(listener);
	}

	public void removeListener(AmstradProgramRuntimeListener listener) {
		getListeners().remove(listener);
	}

	public final void run() throws AmstradProgramException {
		checkNotDisposed();
		doRun();
		setRun(true);
		for (AmstradProgramRuntimeListener listener : getListeners()) {
			listener.amstradProgramIsRun(this);
		}
	}

	protected abstract void doRun() throws AmstradProgramException;

	public synchronized void addMemoryTrap(int memoryAddress, byte memoryValueOff, MemoryTrapHandler handler) {
		MemoryTrap memoryTrap = new MemoryTrap(memoryAddress, memoryValueOff, handler);
		getMemoryTraps().add(memoryTrap);
	}

	public synchronized void removeMemoryTrapsAt(int memoryAddress) {
		Iterator<MemoryTrap> it = getMemoryTraps().iterator();
		while (it.hasNext()) {
			if (it.next().getMemoryAddress() == memoryAddress)
				it.remove();
		}
	}

	public synchronized void removeAllMemoryTraps() {
		getMemoryTraps().clear();
	}

	public synchronized final void activateMemoryTraps() {
		checkNotDisposed();
		if (memoryTrapTracker == null) {
			memoryTrapTracker = new MemoryTrapTracker(this);
			memoryTrapTracker.start();
		} else {
			memoryTrapTracker.setProgramRuntimeToTrack(this);
		}
	}

	public synchronized final void deactivateMemoryTraps() {
		if (memoryTrapTracker != null) {
			if (memoryTrapTracker.getProgramRuntimeToTrack() == this) {
				memoryTrapTracker.stopTracking();
				memoryTrapTracker = null;
			}
		}
	}

	public void dispose() {
		deactivateMemoryTraps();
		getAmstradPc().removeStateListener(this);
		getAmstradPc().removeProgramListener(this);
		setDisposed(true);
		for (AmstradProgramRuntimeListener listener : getListeners()) {
			listener.amstradProgramIsDisposed(this);
		}
	}

	@Override
	public void amstradProgramLoaded(AmstradPc amstradPc) {
		dispose(); // another program got loaded
	}

	@Override
	public void doubleEscapeKey(AmstradPc amstradPc) {
		if (isRun()) {
			for (AmstradProgramRuntimeListener listener : getListeners()) {
				listener.amstradProgramIsInterrupted(this);
			}
			dispose();
		}
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		dispose();
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
		dispose();
	}

	protected void checkNotDisposed() {
		if (isDisposed())
			throw new IllegalStateException("This program runtime is disposed");
	}

	public AmstradProgram getProgram() {
		return program;
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	protected BasicRuntime getBasicRuntime() {
		return getAmstradPc().getBasicRuntime();
	}

	private boolean hasMemoryTraps() {
		return !getMemoryTraps().isEmpty();
	}

	private List<MemoryTrap> getMemoryTraps() {
		return memoryTraps;
	}

	public boolean isRun() {
		return run;
	}

	private void setRun(boolean run) {
		this.run = run;
	}

	public boolean isDisposed() {
		return disposed;
	}

	private void setDisposed(boolean disposed) {
		this.disposed = disposed;
	}

	protected List<AmstradProgramRuntimeListener> getListeners() {
		return listeners;
	}

	private class MemoryTrap {

		private int memoryAddress;

		private byte memoryValueOff;

		private MemoryTrapHandler handler;

		public MemoryTrap(int memoryAddress, byte memoryValueOff, MemoryTrapHandler handler) {
			this.memoryAddress = memoryAddress;
			this.memoryValueOff = memoryValueOff;
			this.handler = handler;
		}

		public boolean isOn() {
			return getMemoryValue() != getMemoryValueOff();
		}

		public void reset() {
			AmstradProgramRuntime.this.getBasicRuntime().poke(getMemoryAddress(), getMemoryValueOff());
		}

		public int getMemoryAddress() {
			return memoryAddress;
		}

		public byte getMemoryValueOff() {
			return memoryValueOff;
		}

		public byte getMemoryValue() {
			return AmstradProgramRuntime.this.getBasicRuntime().peek(getMemoryAddress());
		}

		public MemoryTrapHandler getHandler() {
			return handler;
		}

	}

	public static interface MemoryTrapHandler {

		void handleMemoryTrap(AmstradProgramRuntime programRuntime, int memoryAddress, byte memoryValue);

	}

	private static class MemoryTrapTracker extends Thread {

		private AmstradProgramRuntime programRuntimeToTrack;

		private boolean stop;

		public MemoryTrapTracker(AmstradProgramRuntime programRuntimeToTrack) {
			setProgramRuntimeToTrack(programRuntimeToTrack);
			setDaemon(true);
		}

		@Override
		public void run() {
			System.out.println("Memorytrap tracker thread started");
			while (!stop) {
				AmstradProgramRuntime programRuntime = getProgramRuntimeToTrack();
				if (programRuntime != null && programRuntime.hasMemoryTraps()) {
					synchronized (programRuntime) {
						track(programRuntime);
					}
				}
				AmstradUtils.sleep(200L);
			}
			System.out.println("Memorytrap tracker thread stopped");
		}

		private void track(AmstradProgramRuntime programRuntime) {
			for (MemoryTrap memoryTrap : programRuntime.getMemoryTraps()) {
				if (memoryTrap.isOn()) {
					byte value = memoryTrap.getMemoryValue();
					memoryTrap.reset();
					memoryTrap.getHandler().handleMemoryTrap(programRuntime, memoryTrap.getMemoryAddress(), value);
				}
			}
		}

		public void stopTracking() {
			stop = true;
		}

		public AmstradProgramRuntime getProgramRuntimeToTrack() {
			return programRuntimeToTrack;
		}

		public void setProgramRuntimeToTrack(AmstradProgramRuntime programRuntime) {
			this.programRuntimeToTrack = programRuntime;
		}

	}

}