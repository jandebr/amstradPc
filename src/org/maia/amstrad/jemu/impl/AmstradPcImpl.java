package org.maia.amstrad.jemu.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.MenuBar;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import jemu.core.device.Computer;
import jemu.core.device.ComputerAutotypeListener;
import jemu.settings.Settings;
import jemu.ui.Autotype;
import jemu.ui.JEMU;
import jemu.ui.JEMU.PauseListener;
import jemu.ui.Switches;

import org.maia.amstrad.jemu.AmstradMonitorMode;
import org.maia.amstrad.jemu.AmstradPc;
import org.maia.amstrad.jemu.AmstradPcBasicRuntime;
import org.maia.amstrad.jemu.AmstradPcFrame;
import org.maia.amstrad.jemu.JemuFrameAdapter;

public class AmstradPcImpl extends AmstradPc implements ComputerAutotypeListener, PauseListener {

	private JEMU jemuInstance;

	private boolean started;

	private boolean terminated;

	private boolean autotyping;

	public AmstradPcImpl() {
		this.jemuInstance = new JEMU(new JemuFrameBridge());
		this.jemuInstance.setStandalone(true);
	}

	@Override
	public AmstradPcFrame displayInFrame(boolean exitOnClose) {
		AmstradPcFrame frame = super.displayInFrame(exitOnClose);
		getFrameBridge().setFrame(frame);
		return frame;
	}

	@Override
	public boolean isSnapshotFile(File file) {
		return isUncompressedSnapshotFile(file) || isCompressedSnapshotFile(file);
	}

	private boolean isUncompressedSnapshotFile(File file) {
		String fname = file.getName().toLowerCase();
		return fname.endsWith(".sna");
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
		Switches.uncompressed = isUncompressedSnapshotFile(file);
		Switches.save64 = true; // 64k RAM memory dump
	}

	@Override
	public synchronized void start(boolean waitUntilReady) {
		checkNotStarted();
		checkNotTerminated();
		getJemuInstance().init();
		getJemuInstance().start();
		getJemuInstance().addAutotypeListener(this);
		getJemuInstance().addPauseListener(this);
		getFrameBridge().pack();
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
	public synchronized void pause() {
		checkStarted();
		checkNotTerminated();
		if (!isPaused()) {
			getJemuInstance().pauseToggle();
		}
	}

	@Override
	public synchronized void resume() {
		checkPaused();
		getJemuInstance().pauseToggle();
	}

	@Override
	public void pauseStateChanged(JEMU jemuInstance, boolean paused) {
		if (paused) {
			firePausingEvent();
		} else {
			fireResumingEvent();
		}
	}

	@Override
	public synchronized void terminate() {
		checkNotTerminated();
		Autotype.clearText();
		getJemuInstance().quit();
		setTerminated(true);
		fireTerminatedEvent();
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
	public void setMonitorMode(AmstradMonitorMode mode) {
		checkNotTerminated();
		if (AmstradMonitorMode.COLOR.equals(mode)) {
			getJemuInstance().changeMonitorModeToColour();
		} else if (AmstradMonitorMode.GREEN.equals(mode)) {
			getJemuInstance().changeMonitorModeToGreen();
		} else if (AmstradMonitorMode.GRAY.equals(mode)) {
			getJemuInstance().changeMonitorModeToGray();
		}
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

	private JemuFrameBridge getFrameBridge() {
		return (JemuFrameBridge) getJemuInstance().getFrameAdapter();
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	private void setStarted(boolean started) {
		this.started = started;
	}

	@Override
	public boolean isPaused() {
		return getJemuInstance().isPaused();
	}

	@Override
	public boolean isTerminated() {
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

	private static class JemuFrameBridge extends JemuFrameAdapter {

		private JFrame frame;

		public JemuFrameBridge() {
			this(null);
		}

		public JemuFrameBridge(JFrame frame) {
			setFrame(frame);
		}

		@Override
		public void setTitle(String title) {
			if (!isFrameLess()) {
				getFrame().setTitle(title);
			}
		}

		@Override
		public void setMenuBar(MenuBar menuBar) {
			if (!isFrameLess()) {
				if (Settings.getBoolean(Settings.SHOWMENU, true)) {
					getFrame().setMenuBar(menuBar);
				}
			}
		}

		@Override
		public void removeMenuBar(MenuBar menuBar) {
			if (!isFrameLess()) {
				getFrame().remove(menuBar);
			}
		}

		@Override
		public void setLocation(int x, int y) {
			if (!isFrameLess()) {
				getFrame().setLocation(x, y);
			}
		}

		@Override
		public void setSize(int width, int height) {
			if (!isFrameLess()) {
				getFrame().setSize(width, height);
			}
		}

		@Override
		public void setResizable(boolean resizable) {
			if (!isFrameLess()) {
				getFrame().setResizable(resizable);
			}
		}

		@Override
		public void setAlwaysOnTop(boolean alwaysOnTop) {
			if (!isFrameLess()) {
				getFrame().setAlwaysOnTop(alwaysOnTop);
			}
		}

		@Override
		public void setVisible(boolean visible) {
			if (!isFrameLess()) {
				getFrame().setVisible(visible);
			}
		}

		@Override
		public void pack() {
			if (!isFrameLess()) {
				getFrame().pack();
			}
		}

		@Override
		public void dispose() {
			if (!isFrameLess()) {
				getFrame().dispose();
			}
		}

		@Override
		public void setUndecorated(boolean undecorated) {
			if (!isFrameLess()) {
				getFrame().setUndecorated(undecorated);
			}
		}

		@Override
		public int getX() {
			if (!isFrameLess()) {
				return getFrame().getX();
			} else {
				return 0;
			}
		}

		@Override
		public int getY() {
			if (!isFrameLess()) {
				return getFrame().getY();
			} else {
				return 0;
			}
		}

		@Override
		public Dimension getSize() {
			if (!isFrameLess()) {
				return getFrame().getSize();
			} else {
				return new Dimension();
			}
		}

		@Override
		public FileDialog createFileDialog(String title, int mode) {
			if (isFrameLess())
				throw new HeadlessException("Not backed by any frame");
			return new FileDialog(getFrame(), title, mode);
		}

		private boolean isFrameLess() {
			return getFrame() == null;
		}

		private JFrame getFrame() {
			return frame;
		}

		public void setFrame(JFrame frame) {
			this.frame = frame;
		}

	}

}