package org.maia.amstrad.pc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.pc.action.AmstradPcActions;
import org.maia.amstrad.pc.audio.AmstradAudio;
import org.maia.amstrad.pc.frame.AmstradPcFrame;
import org.maia.amstrad.pc.frame.AmstradPcFrameListener;
import org.maia.amstrad.pc.impl.joystick.AmstradJoystickDevice;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.joystick.AmstradJoystickMode;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.tape.AmstradTape;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.load.AmstradProgramLoader;
import org.maia.amstrad.program.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.program.load.AmstradProgramRuntime;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.util.GenericListenerList;

public abstract class AmstradPc {

	private AmstradPcFrame frame;

	private AmstradPcActions actions;

	private Map<AmstradJoystickID, AmstradJoystick> joysticks;

	private GenericListenerList<AmstradPcStateListener> stateListeners;

	private GenericListenerList<AmstradPcPerformanceListener> performanceListeners;

	protected AmstradPc() {
		this.actions = new AmstradPcActions(this);
		this.joysticks = new HashMap<AmstradJoystickID, AmstradJoystick>();
		this.stateListeners = new GenericListenerList<AmstradPcStateListener>();
		this.performanceListeners = new GenericListenerList<AmstradPcPerformanceListener>();
	}

	public AmstradPcFrame displayInFrame(boolean exitOnClose) {
		checkNotTerminated();
		AmstradPcFrame frame = createFrame(exitOnClose);
		setFrame(frame);
		if (isStarted()) {
			frame.amstradPcStarted(this);
		}
		return frame;
	}

	protected abstract AmstradPcFrame createFrame(boolean exitOnClose);

	public void showActionableDialog(ActionableDialog dialog) {
		dialog.setVisible(true);
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
			System.err.println("No loader found for program " + program.getProgramName());
		}
	}

	public abstract void load(AmstradPcSnapshotFile snapshotFile) throws IOException;

	public abstract void save(AmstradPcSnapshotFile snapshotFile) throws IOException;

	public abstract boolean isStarted();

	public boolean isRunning() {
		return isStarted() && !isTerminated() && !isPaused();
	}

	public abstract boolean isPaused();

	public abstract boolean isTerminated();

	public void start() {
		start(true, false);
	}

	public abstract void start(boolean waitUntilReady, boolean silent);

	public void reboot() {
		reboot(true, false);
	}

	public abstract void reboot(boolean waitUntilReady, boolean silent);

	/**
	 * Eventually pauses this Amstrad PC (non-blocking call)
	 * 
	 * @see #resume()
	 */
	public abstract void pause();

	/**
	 * Immediately pauses this Amstrad PC
	 * <p>
	 * Unlike {@link #pause()}, this method blocks the caller until the PC is in pause state
	 * </p>
	 */
	public abstract void pauseImmediately();

	/**
	 * Eventually resumes this Amstrad PC (non-blocking call)
	 * 
	 * @see #pause()
	 */
	public abstract void resume();

	public abstract void terminate();

	public abstract AmstradKeyboard getKeyboard();

	public abstract AmstradMemory getMemory();

	public abstract AmstradMonitor getMonitor();

	public abstract AmstradTape getTape();

	public abstract AmstradAudio getAudio();

	public abstract BasicRuntime getBasicRuntime();

	public AmstradJoystick getJoystick(AmstradJoystickID joystickId) {
		synchronized (getJoysticks()) {
			AmstradJoystick joystick = getJoysticks().get(joystickId);
			if (joystick == null) {
				joystick = createJoystick(joystickId);
				getJoysticks().put(joystickId, joystick);
			}
			return joystick;
		}
	}

	protected AmstradJoystick createJoystick(AmstradJoystickID joystickId) {
		AmstradJoystick joystick = new AmstradJoystickDevice(this, joystickId);
		JoystickModeManager modeManager = new JoystickModeManager(joystick);
		getMonitor().addMonitorListener(modeManager);
		getFrame().addFrameListener(modeManager);
		return joystick;
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

	protected void checkStartedNotTerminated() {
		checkStarted();
		checkNotTerminated();
	}

	public void addStateListener(AmstradPcStateListener listener) {
		getStateListeners().addListener(listener);
	}

	public void removeStateListener(AmstradPcStateListener listener) {
		getStateListeners().removeListener(listener);
	}

	public void addPerformanceListener(AmstradPcPerformanceListener listener) {
		getPerformanceListeners().addListener(listener);
	}

	public void removePerformanceListener(AmstradPcPerformanceListener listener) {
		getPerformanceListeners().removeListener(listener);
	}

	protected void fireStartedEvent() {
		for (AmstradPcStateListener listener : getStateListeners())
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
		for (AmstradPcStateListener listener : getStateListeners())
			listener.amstradPcRebooting(this);
	}

	protected void fireTerminatedEvent() {
		for (AmstradPcStateListener listener : getStateListeners())
			listener.amstradPcTerminated(this);
	}

	protected void fireProgramLoaded() {
		for (AmstradPcStateListener listener : getStateListeners())
			listener.amstradPcProgramLoaded(this);
	}

	public void fireDisplayPerformanceUpdate(long timeIntervalMillis, int framesPainted, int imagesUpdated) {
		for (AmstradPcPerformanceListener listener : getPerformanceListeners())
			listener.displayPerformanceUpdate(this, timeIntervalMillis, framesPainted, imagesUpdated);
	}

	protected void fireProcessorPerformanceUpdate(long timeIntervalMillis, int timerSyncs, int laggingSyncs,
			int throttledSyncs) {
		for (AmstradPcPerformanceListener listener : getPerformanceListeners())
			listener.processorPerformanceUpdate(this, timeIntervalMillis, timerSyncs, laggingSyncs, throttledSyncs);
	}

	public boolean hasFrame() {
		return getFrame() != null;
	}

	public AmstradPcFrame getFrame() {
		return frame;
	}

	private void setFrame(AmstradPcFrame frame) {
		this.frame = frame;
	}

	public AmstradPcActions getActions() {
		return actions;
	}

	private Map<AmstradJoystickID, AmstradJoystick> getJoysticks() {
		return joysticks;
	}

	protected GenericListenerList<AmstradPcStateListener> getStateListeners() {
		return stateListeners;
	}

	protected GenericListenerList<AmstradPcPerformanceListener> getPerformanceListeners() {
		return performanceListeners;
	}

	private class JoystickModeManager extends AmstradMonitorAdapter implements AmstradPcFrameListener {

		private AmstradJoystick joystick;

		public JoystickModeManager(AmstradJoystick joystick) {
			this.joystick = joystick;
			switchMode(getDefaultModeForMonitor(getMonitor()));
		}

		@Override
		public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
			switchMode(getDefaultModeForMonitor(monitor));
		}

		@Override
		public void popupMenuWillBecomeVisible(AmstradPcFrame frame) {
			switchMode(AmstradJoystickMode.MENU);
		}

		@Override
		public void popupMenuWillBecomeInvisible(AmstradPcFrame frame) {
			switchMode(getDefaultModeForMonitor(getMonitor()));
		}

		private void switchMode(AmstradJoystickMode mode) {
			getJoystick().switchMode(mode);
			getJoystick().switchAutoRepeatEnabled(isAutoRepeatEnabled(mode));
		}

		private AmstradJoystickMode getDefaultModeForMonitor(AmstradMonitor monitor) {
			if (monitor.isPrimaryDisplaySourceShowing()) {
				return AmstradJoystickMode.GAMING;
			} else {
				return AmstradJoystickMode.MENU;
			}
		}

		private boolean isAutoRepeatEnabled(AmstradJoystickMode mode) {
			return AmstradJoystickMode.MENU.equals(mode);
		}

		public AmstradJoystick getJoystick() {
			return joystick;
		}

	}

}