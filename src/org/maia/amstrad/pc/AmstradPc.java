package org.maia.amstrad.pc;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
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
import org.maia.swing.dialog.ActionableDialog;

public abstract class AmstradPc {

	private AmstradPcFrame frame;

	private List<AmstradPcStateListener> stateListeners;

	private List<AmstradPcMonitorListener> monitorListeners;

	private List<AmstradPcEventListener> eventListeners;

	private List<AmstradPcProgramListener> programListeners;

	protected AmstradPc() {
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

}