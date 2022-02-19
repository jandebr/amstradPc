package org.maia.amstrad.jemu.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import jemu.core.device.Computer;
import jemu.core.device.ComputerAutotypeListener;
import jemu.settings.Settings;
import jemu.ui.Autotype;
import jemu.ui.JEMU;
import jemu.ui.Switches;

import org.maia.amstrad.jemu.AmstradPc;
import org.maia.amstrad.jemu.AmstradPcBasicRuntime;
import org.maia.amstrad.jemu.JemuFrameAdapter;

public class AmstradPcImpl extends AmstradPc implements ComputerAutotypeListener {

	private JEMU jemuInstance;

	private boolean started;

	private boolean terminated;

	private boolean autotyping;

	public AmstradPcImpl(JemuFrameAdapter frameAdapter) {
		this.jemuInstance = new JEMU(frameAdapter);
		this.jemuInstance.setStandalone(true);
	}

	@Override
	public boolean isBasicSourceFile(File file) {
		String fname = file.getName().toLowerCase();
		return fname.endsWith(".bas") || fname.endsWith(".txt");
	}

	@Override
	public boolean isSnapshotFile(File file) {
		String fname = file.getName().toLowerCase();
		return fname.endsWith(".sna") || fname.endsWith(".snz");
	}

	private boolean isCompressedSnapshotFile(File file) {
		String fname = file.getName().toLowerCase();
		return fname.endsWith(".snz");
	}

	@Override
	public void launch(File file) throws IOException {
		checkNotTerminated();
		if (isBasicSourceFile(file)) {
			if (!isStarted()) {
				start(true);
			} else {
				reboot(true);
			}
			getBasicRuntime().run(file);
		} else if (isSnapshotFile(file)) {
			if (!isStarted()) {
				start(false);
			}
			getJemuInstance().doAutoOpen(file);
		} else {
			System.err.println("Unrecognized file format: " + file);
		}
	}

	@Override
	public void saveSnapshot(File file) throws IOException {
		checkStarted();
		checkNotTerminated();
		Settings.set(Settings.SNAPSHOT_FILE, file.getAbsolutePath());
		Switches.uncompressed = !isCompressedSnapshotFile(file);
		Switches.save64 = true; // 64k RAM memory dump
	}

	@Override
	public synchronized void start(boolean waitUntilReady) {
		checkNotStarted();
		checkNotTerminated();
		getJemuInstance().init();
		getJemuInstance().start();
		getJemuInstance().addAutotypeListener(this);
		getFrameAdapter().pack();
		setStarted(true);
		fireStartedEvent();
		if (waitUntilReady) {
			waitUntilReady();
		}
	}

	@Override
	public synchronized void reboot(boolean waitUntilReady) {
		checkStarted();
		checkNotTerminated();
		getJemuInstance().reBoot();
		fireRebootingEvent();
		if (waitUntilReady) {
			waitUntilReady();
		}
	}

	@Override
	public synchronized void terminate() {
		checkNotTerminated();
		Autotype.clearText();
		getJemuInstance().destroy();
		setTerminated(true);
	}

	@Override
	public synchronized AmstradPcBasicRuntime getBasicRuntime() {
		checkStarted();
		checkNotTerminated();
		return new AmstradPcBasicRuntimeImpl();
	}

	@Override
	public Component getDisplayPane() {
		return getJemuInstance();
	}

	@Override
	public BufferedImage makeScreenshot() {
		checkStarted();
		checkNotTerminated();
		Component comp = getDisplayPane();
		BufferedImage image = new BufferedImage(comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		comp.paintAll(graphics);
		graphics.dispose();
		return image;
	}

	private void checkStarted() {
		if (!isStarted())
			throw new IllegalStateException("This Amstrad PC has not been started");
	}

	private void checkNotStarted() {
		if (isStarted())
			throw new IllegalStateException("This Amstrad PC is already started");
	}

	private void checkNotTerminated() {
		if (isTerminated())
			throw new IllegalStateException("This Amstrad PC was terminated");
	}

	private void waitUntilReady() {
		waitUntilReady(500L, 4000L);
	}

	private void waitUntilReady(long minWaitTimeMs, long maxWaitTimeMs) {
		System.out.println("Wait until Basic runtime is Ready");
		long timeout = System.currentTimeMillis() + maxWaitTimeMs;
		sleep(minWaitTimeMs);
		BufferedImage image = makeScreenshot();
		double s = (image.getWidth() + 28.0) / 384.0;
		int cursorX = (int) Math.round(s * 34 + (s - 1) * 2);
		int cursorY = (int) Math.round(s * 108 + (s - 1) * 6);
		Color color = new Color(image.getRGB(cursorX, cursorY));
		while (color.getGreen() < 100 && System.currentTimeMillis() < timeout) {
			sleep(100L);
			System.out.println("Checking if Basic runtime is Ready");
			image = makeScreenshot();
			color = new Color(image.getRGB(cursorX, cursorY));
		}
		if (System.currentTimeMillis() >= timeout) {
			System.out.println("Timeout for Basic runtime is Ready");
		}
		System.out.println("Basic runtime is Ready");
	}

	private void sleep(long milliseconds) {
		if (milliseconds > 0L) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void autotypeStarted(Computer computer) {
		System.out.println("Autotype started");
	}

	@Override
	public synchronized void autotypeEnded(Computer computer) {
		System.out.println("Autotype ended");
		setAutotyping(false);
		notifyAll();
	}

	private synchronized void waitUntilAutotypeEnded() {
		setAutotyping(true);
		while (isAutotyping()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	private JEMU getJemuInstance() {
		return jemuInstance;
	}

	public JemuFrameAdapter getFrameAdapter() {
		return getJemuInstance().getFrameAdapter();
	}

	private boolean isStarted() {
		return started;
	}

	private void setStarted(boolean started) {
		this.started = started;
	}

	private boolean isTerminated() {
		return terminated;
	}

	private void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	private boolean isAutotyping() {
		return autotyping;
	}

	private void setAutotyping(boolean autotyping) {
		this.autotyping = autotyping;
	}

	private class AmstradPcBasicRuntimeImpl extends AmstradPcBasicRuntime {

		public AmstradPcBasicRuntimeImpl() {
		}

		@Override
		public void keyboardType(CharSequence text, boolean waitUntilTyped) {
			synchronized (AmstradPcImpl.this) {
				Autotype.typeText(text);
				if (waitUntilTyped) {
					waitUntilAutotypeEnded();
					sleep(100L);
				}
			}
		}

	}

}