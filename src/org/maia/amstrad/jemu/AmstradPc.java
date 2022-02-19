package org.maia.amstrad.jemu;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public abstract class AmstradPc {

	private List<AmstradPcStateListener> stateListeners;

	protected AmstradPc() {
		this.stateListeners = new Vector<AmstradPcStateListener>();
	}

	public abstract boolean isBasicSourceFile(File file);

	public abstract boolean isSnapshotFile(File file);

	public abstract void launch(File file) throws IOException;

	public abstract void saveSnapshot(File file) throws IOException;

	public abstract void start(boolean waitUntilReady);

	public abstract void reboot(boolean waitUntilReady);

	public abstract void terminate();

	public abstract AmstradPcBasicRuntime getBasicRuntime();

	public abstract Component getDisplayPane();

	public abstract BufferedImage makeScreenshot();

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

	protected void fireRebootingEvent() {
		for (AmstradPcStateListener listener : getStateListeners())
			listener.amstradPcRebooting(this);
	}

	protected List<AmstradPcStateListener> getStateListeners() {
		return stateListeners;
	}

}