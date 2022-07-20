package org.maia.amstrad.pc.jemu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
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
import jemu.ui.Display;
import jemu.ui.JEMU;
import jemu.ui.JEMU.PauseListener;
import jemu.ui.Switches;

import org.maia.amstrad.pc.AmstradContext;
import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.basic.BasicCompilationException;
import org.maia.amstrad.pc.basic.BasicRuntime;

public class JemuAmstradPc extends AmstradPc implements ComputerAutotypeListener, PauseListener {

	private JEMU jemuInstance;

	private BasicRuntime basicRuntime;

	private boolean started;

	private boolean terminated;

	private boolean autotyping;

	private static boolean instanceRunning; // maximum 1 running Jemu instance in JVM

	private static final int SNAPSHOT_HEADER_SIZE = 256; // in bytes

	public JemuAmstradPc() {
		this.jemuInstance = new JEMU(new JemuFrameBridge());
		this.jemuInstance.setStandalone(true);
		this.basicRuntime = new JemuBasicRuntimeImpl();
	}

	@Override
	public AmstradPcFrame displayInFrame(boolean exitOnClose) {
		AmstradPcFrame frame = super.displayInFrame(exitOnClose);
		getFrameBridge().setFrame(frame);
		getJemuInstance().alwaysOnTopCheck();
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
	public void launch(File file) throws IOException, BasicCompilationException {
		checkNotTerminated();
		System.out.println("Launching from " + file.getPath());
		if (isBasicSourceFile(file) || isBasicByteCodeFile(file)) {
			if (!isStarted()) {
				start(true);
			} else {
				reboot(true);
			}
			if (isBasicSourceFile(file)) {
				getBasicRuntime().loadSourceCodeFromFile(file);
			} else {
				getBasicRuntime().loadByteCodeFromFile(file);
			}
			getBasicRuntime().run();
		} else if (isSnapshotFile(file)) {
			if (!isStarted()) {
				start(true);
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
		waitUntilSnapshotReady(file);
		System.out.println("Saved snapshot to " + file.getPath());
	}

	@Override
	public synchronized void start(boolean waitUntilReady) {
		checkNoInstanceRunning();
		checkNotStarted();
		checkNotTerminated();
		getJemuInstance().init();
		getJemuInstance().start();
		getJemuInstance().addAutotypeListener(this);
		getJemuInstance().addPauseListener(this);
		getFrameBridge().pack();
		setStarted(true);
		setInstanceRunning(true);
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
		if (!isTerminated()) {
			if (isStarted()) {
				Autotype.clearText();
				getJemuInstance().quit();
			}
			setTerminated(true);
			setInstanceRunning(false);
			fireTerminatedEvent();
		}
	}

	@Override
	public synchronized BasicRuntime getBasicRuntime() {
		checkStarted();
		checkNotTerminated();
		return basicRuntime;
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
	public boolean isFullscreen() {
		return JEMU.fullscreen;
	}

	@Override
	public void toggleFullscreen() {
		checkNotTerminated();
		getJemuInstance().FullSize();
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		checkNotTerminated();
		getJemuInstance().setAlwaysOnTop(alwaysOnTop);
	}

	@Override
	public synchronized BufferedImage makeScreenshot(boolean monitorEffect) {
		checkStarted();
		checkNotTerminated();
		Display display = getJemuInstance().getDisplay();
		boolean masked = display.masked;
		boolean showeffect = display.showeffect;
		display.masked = monitorEffect;
		display.showeffect = monitorEffect;
		BufferedImage image = display.getImage();
		display.masked = masked;
		display.showeffect = showeffect;
		return image;
	}

	private void waitUntilReady() {
		waitUntilReady(500L, 4000L);
	}

	private void waitUntilReady(long minWaitTimeMs, long maxWaitTimeMs) {
		System.out.println("Wait until Basic runtime is Ready");
		long timeout = System.currentTimeMillis() + maxWaitTimeMs;
		AmstradContext.sleep(minWaitTimeMs);
		BufferedImage image = makeScreenshot(false);
		double s = (image.getWidth() + 28.0) / 384.0;
		int cursorX = (int) Math.round(s * 34 + (s - 1) * 2);
		int cursorY = (int) Math.round(s * 108 + (s - 1) * 6);
		Color color = new Color(image.getRGB(cursorX, cursorY));
		while (color.getGreen() < 100 && System.currentTimeMillis() < timeout) {
			AmstradContext.sleep(100L);
			System.out.println("Checking if Basic runtime is Ready");
			image = makeScreenshot(false);
			color = new Color(image.getRGB(cursorX, cursorY));
		}
		if (System.currentTimeMillis() >= timeout) {
			System.out.println("Timeout for Basic runtime is Ready");
		}
		System.out.println("Basic runtime is Ready");
	}

	private void waitUntilSnapshotReady(File snapshotFile) {
		waitUntilSnapshotReady(snapshotFile, 1000L);
	}

	private void waitUntilSnapshotReady(File snapshotFile, long maxWaitTimeMs) {
		long timeout = System.currentTimeMillis() + maxWaitTimeMs;
		while (snapshotFile.length() < 65536L + SNAPSHOT_HEADER_SIZE && System.currentTimeMillis() < timeout) {
			AmstradContext.sleep(100L);
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

	private static void checkNoInstanceRunning() {
		if (isInstanceRunning())
			throw new IllegalStateException("There can only be a single JEMU Amstrad PC running");
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

	private static boolean isInstanceRunning() {
		return instanceRunning;
	}

	private static void setInstanceRunning(boolean instanceRunning) {
		JemuAmstradPc.instanceRunning = instanceRunning;
	}

	private class JemuBasicRuntimeImpl extends BasicRuntime {

		public JemuBasicRuntimeImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		public void keyboardType(CharSequence text, boolean waitUntilTyped) {
			synchronized (getAmstradPc()) {
				Autotype.typeText(text);
				if (waitUntilTyped) {
					waitUntilAutotypeEnded();
					AmstradContext.sleep(100L);
				}
			}
		}

		@Override
		protected void loadFittedByteCode(byte[] byteCode) {
			synchronized (getAmstradPc()) {
				JEMU jemu = getJemuInstance();
				// Pause
				boolean running = jemu.isRunning();
				if (running) {
					jemu.pauseComputer();
				}
				// Load byte code
				jemu.writeMemory(byteCode, MEMORY_ADDRESS_START_OF_PROGRAM);
				// Marking end of byte code
				int addr = MEMORY_ADDRESS_START_OF_PROGRAM + byteCode.length;
				byte w0 = (byte) (addr % 256);
				byte w1 = (byte) (addr / 256);
				byte[] data = new byte[8];
				for (int i = 0; i < 4; i++) {
					data[i * 2] = w0;
					data[i * 2 + 1] = w1;
				}
				jemu.writeMemory(data, MEMORY_POINTER_END_OF_PROGRAM);
				// Resume
				if (running) {
					jemu.goComputer();
				}
			}
		}

		@Override
		protected byte[] exportFittedByteCode() {
			synchronized (getAmstradPc()) {
				int len = MEMORY_POINTER_END_OF_PROGRAM - MEMORY_ADDRESS_START_OF_PROGRAM;
				byte[] mem = getJemuInstance().readMemory(MEMORY_ADDRESS_START_OF_PROGRAM, len);
				return fitByteCode(mem);
			}
		}

	}

	private class JemuFrameBridge extends JemuFrameAdapter {

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
				if (Settings.getBoolean(Settings.UPDATETITLE, true)) {
					getFrame().setTitle(title);
				}
			}
		}

		@Override
		public void setMenuBar(MenuBar menuBar) {
			if (!isFrameLess()) {
				if (Settings.getBoolean(Settings.SHOWMENU, true)) {
					getFrame().setMenuBar(menuBar);
				} else {
					if (getFrame().getJMenuBar() != null && !getAmstradPc().isFullscreen()) {
						getFrame().getJMenuBar().setVisible(true);
					}
				}
			}
		}

		@Override
		public void removeMenuBar(MenuBar menuBar) {
			if (!isFrameLess()) {
				if (Settings.getBoolean(Settings.SHOWMENU, true)) {
					getFrame().remove(menuBar);
				} else {
					if (getFrame().getJMenuBar() != null) {
						getFrame().getJMenuBar().setVisible(false);
					}
				}
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

		private AmstradPc getAmstradPc() {
			return JemuAmstradPc.this;
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