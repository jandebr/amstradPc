package org.maia.amstrad.pc;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcEventListener;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.program.loader.AmstradProgramLoader;
import org.maia.amstrad.program.loader.AmstradProgramLoaderFactory;
import org.maia.amstrad.util.AmstradUtils;
import org.maia.swing.dialog.ActionableDialog;

public abstract class AmstradPc {

	private AmstradPcFrame frame;

	private List<MemoryTrap> memoryTraps;

	private MemoryTrapTracker memoryTrapTracker;

	private List<AmstradPcStateListener> stateListeners;

	private List<AmstradPcMonitorListener> monitorListeners;

	private List<AmstradPcEventListener> eventListeners;

	private List<AmstradPcProgramListener> programListeners;

	protected AmstradPc() {
		this.memoryTraps = new Vector<MemoryTrap>();
		this.stateListeners = new Vector<AmstradPcStateListener>();
		this.monitorListeners = new Vector<AmstradPcMonitorListener>();
		this.eventListeners = new Vector<AmstradPcEventListener>();
		this.programListeners = new Vector<AmstradPcProgramListener>();
	}

	public AmstradPcFrame displayInFrame(boolean exitOnClose) {
		checkNotTerminated();
		AmstradPcFrame frame = new AmstradPcFrame(this, exitOnClose);
		setFrame(frame);
		if (isStarted()) {
			frame.amstradPcStarted(this);
		}
		return frame;
	}

	public void showActionableDialog(ActionableDialog dialog) {
		dialog.setVisible(true);
	}

	public abstract boolean isStarted();

	public abstract boolean isPaused();

	public abstract boolean isTerminated();

	public void launch(File file) throws AmstradProgramException {
		launch(new AmstradProgramStoredInFile(file));
	}

	public void launch(AmstradProgram program) throws AmstradProgramException {
		checkNotTerminated();
		if (!isStarted()) {
			start();
		} else {
			reboot();
		}
		AmstradProgramLoader loader = AmstradProgramLoaderFactory.getInstance().createLoaderFor(program, this);
		if (loader != null) {
			System.out.println("Loading program " + program.getProgramName());
			AmstradProgramRuntime rt = loader.load(program);
			System.out.println("Launching program " + program.getProgramName());
			rt.run();
		} else {
			System.err.println("Cannot load program " + program.getProgramName());
		}
	}

	public abstract void load(AmstradPcSnapshotFile snapshotFile) throws IOException;

	public abstract void save(AmstradPcSnapshotFile snapshotFile) throws IOException;

	public void start() {
		start(true, false);
	}

	public abstract void start(boolean waitUntilReady, boolean silent);

	public void reboot() {
		reboot(true, false);
	}

	public abstract void reboot(boolean waitUntilReady, boolean silent);

	public abstract void pause();

	public abstract void resume();

	public abstract void terminate();

	public abstract BasicRuntime getBasicRuntime();

	public abstract Component getDisplayPane();

	public AmstradMonitorMode getMonitorMode() {
		return AmstradFactory.getInstance().getAmstradContext().getUserSettings().getMonitorMode();
	}

	public abstract void setMonitorMode(AmstradMonitorMode mode);

	public abstract boolean isMonitorEffectOn();

	public abstract void setMonitorEffect(boolean monitorEffect);

	public abstract boolean isMonitorScanLinesEffectOn();

	public abstract void setMonitorScanLinesEffect(boolean scanLinesEffect);

	public abstract boolean isMonitorBilinearEffectOn();

	public abstract void setMonitorBilinearEffect(boolean bilinearEffect);

	public abstract boolean isWindowFullscreen();

	public abstract void toggleWindowFullscreen();

	public abstract boolean isWindowAlwaysOnTop();

	public abstract void setWindowAlwaysOnTop(boolean alwaysOnTop);

	public abstract boolean isWindowTitleDynamic();

	public abstract void setWindowTitleDynamic(boolean dynamicTitle);

	public abstract BufferedImage makeScreenshot(boolean monitorEffect);

	public abstract void swapDisplaySource(AmstradAlternativeDisplaySource displaySource);

	public abstract void resetDisplaySource();

	public abstract AmstradAlternativeDisplaySource getCurrentAlternativeDisplaySource();

	public boolean isAlternativeDisplaySourceShowing() {
		return getCurrentAlternativeDisplaySource() != null;
	}

	public boolean isPrimaryDisplaySourceShowing() {
		return !isAlternativeDisplaySourceShowing();
	}

	public synchronized void addMemoryTrap(int memoryAddress, byte memoryValueOff, boolean resetBeforeAdding,
			AmstradPcMemoryTrapHandler handler) {
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

	private synchronized void trackMemoryTrapsAsNeeded() {
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

	protected void checkStarted() {
		if (!isStarted())
			throw new IllegalStateException("This Amstrad PC has not been started");
	}

	protected void checkNotStarted() {
		if (isStarted())
			throw new IllegalStateException("This Amstrad PC is already started");
	}

	protected void checkNotTerminated() {
		if (isTerminated())
			throw new IllegalStateException("This Amstrad PC was terminated");
	}

	public void addStateListener(AmstradPcStateListener listener) {
		getStateListeners().add(listener);
	}

	public void removeStateListener(AmstradPcStateListener listener) {
		getStateListeners().remove(listener);
	}

	public void addMonitorListener(AmstradPcMonitorListener listener) {
		getMonitorListeners().add(listener);
	}

	public void removeMonitorListener(AmstradPcMonitorListener listener) {
		getMonitorListeners().remove(listener);
	}

	public void addEventListener(AmstradPcEventListener listener) {
		getEventListeners().add(listener);
	}

	public void removeEventListener(AmstradPcEventListener listener) {
		getEventListeners().remove(listener);
	}

	public void addProgramListener(AmstradPcProgramListener listener) {
		getProgramListeners().add(listener);
	}

	public void removeProgramListener(AmstradPcProgramListener listener) {
		getProgramListeners().remove(listener);
	}

	protected void fireStartedEvent() {
		for (AmstradPcStateListener listener : getStateListenersFixedList())
			listener.amstradPcStarted(this);
	}

	protected void firePausingEvent() {
		for (AmstradPcStateListener listener : getStateListeners())
			listener.amstradPcPausing(this);
	}

	protected void fireResumingEvent() {
		for (AmstradPcStateListener listener : getStateListeners())
			listener.amstradPcResuming(this);
	}

	protected void fireRebootingEvent() {
		for (AmstradPcStateListener listener : getStateListenersFixedList())
			listener.amstradPcRebooting(this);
	}

	protected void fireTerminatedEvent() {
		for (AmstradPcStateListener listener : getStateListenersFixedList())
			listener.amstradPcTerminated(this);
	}

	protected void fireMonitorModeChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcMonitorModeChanged(this);
	}

	protected void fireMonitorEffectChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcMonitorEffectChanged(this);
	}

	protected void fireMonitorScanLinesEffectChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcMonitorScanLinesEffectChanged(this);
	}

	protected void fireMonitorBilinearEffectChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcMonitorBilinearEffectChanged(this);
	}

	protected void fireWindowFullscreenChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcWindowFullscreenChanged(this);
	}

	protected void fireWindowAlwaysOnTopChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcWindowAlwaysOnTopChanged(this);
	}

	protected void fireWindowTitleDynamicChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcWindowTitleDynamicChanged(this);
	}

	protected void fireDisplaySourceChangedEvent() {
		for (AmstradPcMonitorListener listener : getMonitorListeners())
			listener.amstradPcDisplaySourceChanged(this);
	}

	protected void fireEvent(AmstradPcEvent event) {
		for (AmstradPcEventListener listener : getEventListeners())
			listener.amstradPcEventDispatched(event);
	}

	protected void fireProgramLoaded() {
		for (AmstradPcProgramListener listener : getProgramListenersFixedList())
			listener.amstradProgramLoaded(this);
	}

	protected void fireDoubleEscapeKey() {
		for (AmstradPcProgramListener listener : getProgramListenersFixedList())
			listener.doubleEscapeKey(this);
	}

	public AmstradPcFrame getFrame() {
		return frame;
	}

	private void setFrame(AmstradPcFrame frame) {
		this.frame = frame;
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

	private List<AmstradPcStateListener> getStateListenersFixedList() {
		return new Vector<AmstradPcStateListener>(getStateListeners());
	}

	protected List<AmstradPcStateListener> getStateListeners() {
		return stateListeners;
	}

	protected List<AmstradPcMonitorListener> getMonitorListeners() {
		return monitorListeners;
	}

	protected List<AmstradPcEventListener> getEventListeners() {
		return eventListeners;
	}

	private List<AmstradPcProgramListener> getProgramListenersFixedList() {
		return new Vector<AmstradPcProgramListener>(getProgramListeners());
	}

	protected List<AmstradPcProgramListener> getProgramListeners() {
		return programListeners;
	}

	private class MemoryTrap {

		private int memoryAddress;

		private byte memoryValueOff;

		private AmstradPcMemoryTrapHandler handler;

		public MemoryTrap(int memoryAddress, byte memoryValueOff, AmstradPcMemoryTrapHandler handler) {
			this.memoryAddress = memoryAddress;
			this.memoryValueOff = memoryValueOff;
			this.handler = handler;
		}

		public boolean isOn() {
			return getMemoryValue() != getMemoryValueOff();
		}

		public void reset() {
			getBasicRuntime().poke(getMemoryAddress(), getMemoryValueOff());
		}

		public int getMemoryAddress() {
			return memoryAddress;
		}

		public byte getMemoryValueOff() {
			return memoryValueOff;
		}

		public byte getMemoryValue() {
			return getBasicRuntime().peek(getMemoryAddress());
		}

		public AmstradPcMemoryTrapHandler getHandler() {
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
			while (!stop && isStarted() && !isTerminated() && hasMemoryTraps()) {
				List<MemoryTrap> traps = getMemoryTrapsToTrack();
				traps.clear();
				synchronized (AmstradPc.this) {
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
					memoryTrap.getHandler().handleMemoryTrap(AmstradPc.this, memoryTrap.getMemoryAddress(), value);
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