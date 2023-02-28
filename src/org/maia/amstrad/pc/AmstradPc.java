package org.maia.amstrad.pc;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.load.AmstradProgramLoader;
import org.maia.amstrad.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.swing.dialog.ActionableDialog;

public abstract class AmstradPc {

	private AmstradPcFrame frame;

	private List<AmstradPcStateListener> stateListeners;

	protected AmstradPc() {
		this.stateListeners = new Vector<AmstradPcStateListener>();
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

	public abstract void pause();

	public abstract void resume();

	public abstract void terminate();

	public abstract AmstradKeyboard getKeyboard();

	public abstract AmstradMemory getMemory();

	public abstract AmstradMonitor getMonitor();

	public abstract BasicRuntime getBasicRuntime();

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

	protected void fireProgramLoaded() {
		for (AmstradPcStateListener listener : getStateListenersFixedList())
			listener.amstradPcProgramLoaded(this);
	}

	public AmstradPcFrame getFrame() {
		return frame;
	}

	private void setFrame(AmstradPcFrame frame) {
		this.frame = frame;
	}

	private List<AmstradPcStateListener> getStateListenersFixedList() {
		return new Vector<AmstradPcStateListener>(getStateListeners());
	}

	protected List<AmstradPcStateListener> getStateListeners() {
		return stateListeners;
	}

}