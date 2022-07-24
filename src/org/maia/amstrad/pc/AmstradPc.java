package org.maia.amstrad.pc;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.basic.BasicRuntime;
import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;

public abstract class AmstradPc {

	private List<AmstradPcStateListener> stateListeners;

	protected AmstradPc() {
		this.stateListeners = new Vector<AmstradPcStateListener>();
	}

	public AmstradPcFrame displayInFrame(boolean exitOnClose) {
		checkNotTerminated();
		AmstradPcFrame frame = new AmstradPcFrame(this, exitOnClose);
		if (isStarted()) {
			frame.amstradPcStarted(this);
		}
		return frame;
	}

	public boolean isBasicSourceFile(File file) {
		return file.getName().toLowerCase().endsWith(".bas");
	}

	public boolean isBasicByteCodeFile(File file) {
		return file.getName().toLowerCase().endsWith(".bin");
	}

	public abstract boolean isSnapshotFile(File file);

	public abstract boolean isStarted();

	public abstract boolean isPaused();

	public abstract boolean isTerminated();

	public abstract void launch(File file) throws Exception;

	public abstract void saveSnapshot(File file) throws IOException;

	public abstract void start(boolean waitUntilReady);

	public abstract void reboot(boolean waitUntilReady);

	public abstract void pause();

	public abstract void resume();

	public abstract void terminate();

	public abstract BasicRuntime getBasicRuntime();

	public abstract Component getDisplayPane();

	public AmstradMonitorMode getMonitorMode() {
		return AmstradFactory.getInstance().getAmstradContext().getUserSettings().getMonitorMode();
	}

	public abstract void setMonitorMode(AmstradMonitorMode mode);

	public abstract void setMonitorEffect(boolean monitorEffect);

	public abstract void setMonitorScanLinesEffect(boolean scanLinesEffect);

	public abstract void setMonitorBilinearEffect(boolean bilinearEffect);

	public abstract boolean isFullscreen();

	public abstract void toggleFullscreen();

	public abstract void setAlwaysOnTop(boolean alwaysOnTop);

	public abstract BufferedImage makeScreenshot(boolean monitorEffect);

	public abstract void swapDisplaySource(AmstradAlternativeDisplaySource displaySource);

	public abstract void resetDisplaySource();

	protected void checkStarted() {
		if (!isStarted())
			throw new IllegalStateException("This Amstrad PC has not been started");
	}

	protected void checkNotStarted() {
		if (isStarted())
			throw new IllegalStateException("This Amstrad PC is already started");
	}

	protected void checkPaused() {
		if (!isPaused())
			throw new IllegalStateException("This Amstrad PC is not paused");
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

	protected void fireDisplaySourceChangedEvent(AmstradAlternativeDisplaySource alternativeDisplaySource) {
		for (AmstradPcStateListener listener : getStateListeners())
			listener.amstradPcDisplaySourceChanged(this, alternativeDisplaySource);
	}

	protected List<AmstradPcStateListener> getStateListeners() {
		return stateListeners;
	}

}