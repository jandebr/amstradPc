package org.maia.amstrad.pc.impl.jemu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.io.AmstradFileType;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.AmstradPcSnapshotFile;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;
import org.maia.amstrad.util.AmstradUtils;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialog.ActionableDialogButton;
import org.maia.swing.dialog.ActionableDialogListener;

import jemu.core.device.Computer;
import jemu.core.device.ComputerKeyboardListener;
import jemu.settings.Settings;
import jemu.ui.Autotype;
import jemu.ui.Display;
import jemu.ui.Display.PrimaryDisplaySourceListener;
import jemu.ui.JEMU;
import jemu.ui.JEMU.PauseListener;
import jemu.ui.SecondaryDisplaySource;
import jemu.ui.Switches;

public class JemuAmstradPc extends AmstradPc implements PauseListener, PrimaryDisplaySourceListener {

	private JEMU jemuInstance;

	private AmstradKeyboard keyboard;

	private AmstradMemory memory;

	private AmstradMonitor monitor;

	private BasicRuntime basicRuntime;

	private AmstradGraphicsContextImpl graphicsContext;

	private AlternativeDisplaySourceRenderer alternativeDisplaySourceRenderer;

	private boolean started;

	private boolean terminated;

	private static boolean instanceRunning; // maximum 1 running Jemu instance in JVM

	private static final int SNAPSHOT_HEADER_SIZE = 256; // in bytes

	public JemuAmstradPc() {
		this.jemuInstance = new JEMU(new JemuFrameBridge());
		this.jemuInstance.setStandalone(true);
		this.jemuInstance.setControlKeysEnabled(false);
		this.jemuInstance.setMouseClickActionsEnabled(false);
		this.keyboard = new JemuKeyboardImpl();
		this.memory = new JemuMemoryImpl();
		this.monitor = new JemuMonitorImpl();
		this.basicRuntime = new JemuBasicRuntimeImpl();
		this.graphicsContext = new AmstradGraphicsContextImpl();
	}

	@Override
	public AmstradPcFrame displayInFrame(boolean exitOnClose) {
		AmstradPcFrame frame = super.displayInFrame(exitOnClose);
		getFrameBridge().setFrame(frame);
		getJemuInstance().alwaysOnTopCheck();
		return frame;
	}

	@Override
	public void showActionableDialog(ActionableDialog dialog) {
		dialog.addListener(new ActionableDialogHandler());
		super.showActionableDialog(dialog);
	}

	@Override
	public void load(AmstradPcSnapshotFile snapshotFile) {
		checkStarted();
		checkNotTerminated();
		File file = snapshotFile.getFile();
		getJemuInstance().doAutoOpen(file);
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Loaded snapshot from " + file.getPath());
		fireProgramLoaded();
	}

	@Override
	public void save(AmstradPcSnapshotFile snapshotFile) {
		checkStarted();
		checkNotTerminated();
		File file = snapshotFile.getFile();
		Settings.set(Settings.SNAPSHOT_FILE, file.getAbsolutePath());
		Switches.uncompressed = AmstradFileType.JAVACPC_SNAPSHOT_FILE_UNCOMPRESSED.matches(file);
		Switches.save64 = true; // 64k RAM memory dump
		waitUntilSnapshotReady(file);
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		System.out.println("Saved snapshot to " + file.getPath());
	}

	@Override
	public synchronized void start(boolean waitUntilReady, boolean silent) {
		checkNoInstanceRunning();
		checkNotStarted();
		checkNotTerminated();
		boolean floppySound = Switches.FloppySound;
		if (silent)
			Switches.FloppySound = false;
		JEMU jemu = getJemuInstance();
		jemu.init();
		jemu.start();
		jemu.addPauseListener(this);
		jemu.getDisplay().addPrimaryDisplaySourceListener(this);
		getGraphicsContext().setPrimaryDisplaySourceResolution(
				new Dimension(jemu.getDisplay().getImageWidth(), jemu.getDisplay().getImageHeight()));
		getFrameBridge().pack();
		setStarted(true);
		setInstanceRunning(true);
		fireStartedEvent();
		if (waitUntilReady)
			waitUntilReady();
		if (silent)
			Switches.FloppySound = floppySound;
		if (getMonitor().isWindowFullscreen()) {
			// forcing the display to nicely align in the middle
			getMonitor().toggleWindowFullscreen();
			getMonitor().toggleWindowFullscreen();
		}
	}

	@Override
	public synchronized void reboot(boolean waitUntilReady, boolean silent) {
		checkStarted();
		checkNotTerminated();
		boolean floppySound = Switches.FloppySound;
		if (silent)
			Switches.FloppySound = false;
		getJemuInstance().reBoot();
		fireRebootingEvent();
		if (waitUntilReady)
			waitUntilReady();
		if (silent)
			Switches.FloppySound = floppySound;
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
		checkStarted();
		checkNotTerminated();
		if (isPaused()) {
			getJemuInstance().pauseToggle();
		}
	}

	@Override
	public void pauseStateChanged(JEMU jemuInstance, boolean paused) {
		handleAlternativeDisplaySourceRendering();
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
				if (getMonitor().isAlternativeDisplaySourceShowing()) {
					getMonitor().resetDisplaySource();
				}
				Autotype.clearText();
				getJemuInstance().quit();
			}
			setTerminated(true);
			setInstanceRunning(false);
			fireTerminatedEvent();
		}
	}

	@Override
	public AmstradKeyboard getKeyboard() {
		return keyboard;
	}

	@Override
	public AmstradMemory getMemory() {
		return memory;
	}

	@Override
	public AmstradMonitor getMonitor() {
		return monitor;
	}

	@Override
	public BasicRuntime getBasicRuntime() {
		return basicRuntime;
	}

	private void waitUntilReady() {
		waitUntilReady(500L, 4000L);
	}

	private void waitUntilReady(long minWaitTimeMs, long maxWaitTimeMs) {
		System.out.println("Wait until Basic runtime is Ready");
		long timeout = System.currentTimeMillis() + maxWaitTimeMs;
		AmstradUtils.sleep(minWaitTimeMs);
		Display display = getJemuInstance().getDisplay();
		BufferedImage image = display.getRawPrimaryImage();
		double sx = image.getWidth() / 384.0;
		double sy = image.getHeight() / 272.0;
		int cursorX = (int) Math.round(sx * (32 + 4));
		int cursorY = (int) Math.round(sy * (40 + 68));
		Color color = new Color(image.getRGB(cursorX, cursorY));
		while (color.getGreen() < 100 && System.currentTimeMillis() < timeout) {
			AmstradUtils.sleep(100L);
			System.out.println("Checking if Basic runtime is Ready");
			image = display.getRawPrimaryImage();
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
			AmstradUtils.sleep(100L);
		}
	}

	private synchronized void handleAlternativeDisplaySourceRendering() {
		if (getMonitor().isAlternativeDisplaySourceShowing() && isPaused()) {
			// When computer is paused, there is no vSync and we need to render ourselves
			if (getAlternativeDisplaySourceRenderer() == null || getAlternativeDisplaySourceRenderer().isStopped()) {
				AlternativeDisplaySourceRenderer renderer = new AlternativeDisplaySourceRenderer();
				setAlternativeDisplaySourceRenderer(renderer);
				renderer.start();
			}
		} else {
			// Stop our own rendering
			if (getAlternativeDisplaySourceRenderer() != null) {
				getAlternativeDisplaySourceRenderer().stopRendering();
				setAlternativeDisplaySourceRenderer(null);
			}
		}
	}

	@Override
	public void primaryDisplaySourceResolutionChanged(Display display, Dimension resolution) {
		getGraphicsContext().setPrimaryDisplaySourceResolution(resolution);
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

	private AmstradGraphicsContextImpl getGraphicsContext() {
		return graphicsContext;
	}

	private AlternativeDisplaySourceRenderer getAlternativeDisplaySourceRenderer() {
		return alternativeDisplaySourceRenderer;
	}

	private void setAlternativeDisplaySourceRenderer(AlternativeDisplaySourceRenderer renderer) {
		this.alternativeDisplaySourceRenderer = renderer;
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

	private static boolean isInstanceRunning() {
		return instanceRunning;
	}

	private static void setInstanceRunning(boolean instanceRunning) {
		JemuAmstradPc.instanceRunning = instanceRunning;
	}

	private class JemuKeyboardImpl extends AmstradKeyboard
			implements KeyListener, ComputerKeyboardListener, AmstradPcStateListener {

		private AmstradKeyboardController controller;

		private int escapeKeyCounter;

		private boolean autotyping;

		public JemuKeyboardImpl() {
			super(JemuAmstradPc.this);
			this.controller = new AmstradKeyboardControllerImpl(this);
			getAmstradPc().addStateListener(this);
		}

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			getJemuInstance().getDisplay().addKeyListener(this);
			getJemuInstance().addComputerKeyboardListener(this);
			resetEscapeKeyCounter();
		}

		@Override
		public void amstradPcPausing(AmstradPc amstradPc) {
			// no action
		}

		@Override
		public void amstradPcResuming(AmstradPc amstradPc) {
			// no action
		}

		@Override
		public void amstradPcRebooting(AmstradPc amstradPc) {
			resetEscapeKeyCounter();
		}

		@Override
		public void amstradPcTerminated(AmstradPc amstradPc) {
			resetEscapeKeyCounter();
		}

		@Override
		public void amstradPcProgramLoaded(AmstradPc amstradPc) {
			// no action
		}

		@Override
		public void keyPressed(KeyEvent e) {
			fireKeyboardEventDispatched(new AmstradKeyboardEvent(this, e));
		}

		@Override
		public void keyReleased(KeyEvent e) {
			fireKeyboardEventDispatched(new AmstradKeyboardEvent(this, e));
		}

		@Override
		public void keyTyped(KeyEvent e) {
			fireKeyboardEventDispatched(new AmstradKeyboardEvent(this, e));
		}

		@Override
		public void computerPressEscapeKey(Computer computer) {
			if (++escapeKeyCounter == 2) {
				fireDoubleEscapeKeyPressed();
			}
		}

		@Override
		public void computerSuppressEscapeKey(Computer computer) {
			resetEscapeKeyCounter();
		}

		@Override
		public void computerAutotypeStarted(Computer computer) {
			System.out.println("Autotype started");
		}

		@Override
		public synchronized void computerAutotypeEnded(Computer computer) {
			System.out.println("Autotype ended");
			setAutotyping(false);
			notifyAll();
		}

		@Override
		public synchronized void type(CharSequence text, boolean waitUntilTyped) {
			checkStarted();
			checkNotTerminated();
			Autotype.typeText(text);
			resetEscapeKeyCounter();
			if (waitUntilTyped) {
				waitUntilAutotypeEnded();
				AmstradUtils.sleep(100L);
			}
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

		private void resetEscapeKeyCounter() {
			escapeKeyCounter = 0;
		}

		private boolean isAutotyping() {
			return autotyping;
		}

		private void setAutotyping(boolean autotyping) {
			this.autotyping = autotyping;
		}

		@Override
		public AmstradKeyboardController getController() {
			return controller;
		}

	}

	private class JemuMemoryImpl extends AmstradMemory {

		private boolean jemuRunningAtStartOfMMO;

		public JemuMemoryImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		public void startThreadExclusiveSession() {
			super.startThreadExclusiveSession();
			if (!isNestedThreadExclusiveSession()) {
				// Pause computer when running
				synchronized (JemuAmstradPc.this) {
					jemuRunningAtStartOfMMO = getJemuInstance().isRunning();
					if (jemuRunningAtStartOfMMO) {
						getJemuInstance().pauseComputer();
					}
				}
			}
		}

		@Override
		public void endThreadExclusiveSession() {
			if (!isNestedThreadExclusiveSession()) {
				// Resume computer when paused
				if (jemuRunningAtStartOfMMO) {
					synchronized (JemuAmstradPc.this) {
						getJemuInstance().goComputer();
					}
				}
			}
			super.endThreadExclusiveSession();
		}

		@Override
		public byte read(int memoryAddress) {
			return getJemuInstance().readMemory(memoryAddress);
		}

		@Override
		public byte[] readRange(int memoryOffset, int memoryLength) {
			return getJemuInstance().readMemoryRange(memoryOffset, memoryLength);
		}

		@Override
		public void write(int memoryAddress, byte value) {
			getJemuInstance().writeMemory(memoryAddress, value);
		}

		@Override
		public void writeRange(int memoryOffset, byte[] data, int dataOffset, int dataLength) {
			getJemuInstance().writeMemoryRange(memoryOffset, data, dataOffset, dataLength);
		}

	}

	private class JemuMonitorImpl extends AmstradMonitor {

		public JemuMonitorImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		public Component getDisplayPane() {
			return getJemuInstance();
		}

		@Override
		public void setMonitorMode(AmstradMonitorMode mode) {
			checkNotTerminated();
			if (mode != null && !mode.equals(getMonitorMode())) {
				if (AmstradMonitorMode.COLOR.equals(mode)) {
					getJemuInstance().changeMonitorModeToColour();
				} else if (AmstradMonitorMode.GREEN.equals(mode)) {
					getJemuInstance().changeMonitorModeToGreen();
				} else if (AmstradMonitorMode.GRAY.equals(mode)) {
					getJemuInstance().changeMonitorModeToGray();
				}
				fireMonitorModeChangedEvent();
			}
		}

		@Override
		public boolean isMonitorEffectOn() {
			return Settings.getBoolean(Settings.SCANEFFECT, true);
		}

		@Override
		public void setMonitorEffect(boolean monitorEffect) {
			checkNotTerminated();
			if (monitorEffect != isMonitorEffectOn()) {
				Settings.setBoolean(Settings.SCANEFFECT, monitorEffect);
				Display.scaneffect = monitorEffect;
				fireMonitorEffectChangedEvent();
			}
		}

		@Override
		public boolean isMonitorScanLinesEffectOn() {
			return Settings.getBoolean(Settings.SCANLINES, false);
		}

		@Override
		public void setMonitorScanLinesEffect(boolean scanLinesEffect) {
			checkNotTerminated();
			if (scanLinesEffect != isMonitorScanLinesEffectOn()) {
				Settings.setBoolean(Settings.SCANLINES, scanLinesEffect);
				Switches.ScanLines = scanLinesEffect;
				fireMonitorScanLinesEffectChangedEvent();
			}
		}

		@Override
		public boolean isMonitorBilinearEffectOn() {
			return Settings.getBoolean(Settings.BILINEAR, true);
		}

		@Override
		public void setMonitorBilinearEffect(boolean bilinearEffect) {
			checkNotTerminated();
			if (bilinearEffect != isMonitorBilinearEffectOn()) {
				Settings.setBoolean(Settings.BILINEAR, bilinearEffect);
				Switches.bilinear = bilinearEffect;
				fireMonitorBilinearEffectChangedEvent();
			}
		}

		@Override
		public boolean isWindowFullscreen() {
			return JEMU.fullscreen;
		}

		@Override
		public void toggleWindowFullscreen() {
			checkNotTerminated();
			getJemuInstance().FullSize();
			fireWindowFullscreenChangedEvent();
		}

		@Override
		public boolean isWindowAlwaysOnTop() {
			return Settings.getBoolean(Settings.ONTOP, false);
		}

		@Override
		public void setWindowAlwaysOnTop(boolean alwaysOnTop) {
			checkNotTerminated();
			if (alwaysOnTop != isWindowAlwaysOnTop()) {
				getJemuInstance().setAlwaysOnTop(alwaysOnTop);
				fireWindowAlwaysOnTopChangedEvent();
			}
		}

		@Override
		public boolean isWindowTitleDynamic() {
			return Settings.getBoolean(Settings.UPDATETITLE, true);
		}

		@Override
		public void setWindowTitleDynamic(boolean dynamicTitle) {
			checkNotTerminated();
			if (dynamicTitle != isWindowTitleDynamic()) {
				Settings.setBoolean(Settings.UPDATETITLE, dynamicTitle);
				fireWindowTitleDynamicChangedEvent();
			}
		}

		@Override
		public BufferedImage makeScreenshot(boolean monitorEffect) {
			BufferedImage image = null;
			synchronized (JemuAmstradPc.this) {
				checkStarted();
				checkNotTerminated();
				Display display = getJemuInstance().getDisplay();
				boolean masked = display.masked;
				boolean showeffect = display.showeffect;
				display.masked = monitorEffect;
				display.showeffect = monitorEffect;
				image = display.getImage();
				display.masked = masked;
				display.showeffect = showeffect;
			}
			return image;
		}

		@Override
		public void swapDisplaySource(AmstradAlternativeDisplaySource displaySource) {
			synchronized (JemuAmstradPc.this) {
				checkStarted();
				checkNotTerminated();
				if (displaySource != null) {
					getJemuInstance().getDisplay()
							.installSecondaryDisplaySource(new JemuSecondaryDisplaySourceBridge(displaySource));
					fireDisplaySourceChangedEvent();
					handleAlternativeDisplaySourceRendering();
				} else {
					resetDisplaySource();
				}
			}
		}

		@Override
		public void resetDisplaySource() {
			synchronized (JemuAmstradPc.this) {
				checkStarted();
				checkNotTerminated();
				getJemuInstance().getDisplay().uninstallSecondaryDisplaySource();
				fireDisplaySourceChangedEvent();
				handleAlternativeDisplaySourceRendering();
			}
		}

		@Override
		public AmstradAlternativeDisplaySource getCurrentAlternativeDisplaySource() {
			AmstradAlternativeDisplaySource altDisplaySource = null;
			synchronized (JemuAmstradPc.this) {
				if (isStarted()) {
					SecondaryDisplaySource sds = getJemuInstance().getDisplay().getSecondaryDisplaySource();
					if (sds != null && sds instanceof JemuSecondaryDisplaySourceBridge) {
						altDisplaySource = ((JemuSecondaryDisplaySourceBridge) sds).getSource();
					}
				}
			}
			return altDisplaySource;
		}

	}

	private class JemuBasicRuntimeImpl extends LocomotiveBasicRuntime {

		public JemuBasicRuntimeImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		protected void loadByteCode(BasicByteCode code) throws BasicException {
			super.loadByteCode(code);
			fireProgramLoaded();
		}

	}

	private class AmstradGraphicsContextImpl implements AmstradGraphicsContext {

		private Dimension displayCanvasSize;

		private Dimension primaryDisplaySourceResolution;

		private Font systemFont;

		public AmstradGraphicsContextImpl() {
			this(new Dimension(getBasicRuntime().getDisplayCanvasWidth(), getBasicRuntime().getDisplayCanvasHeight()));
		}

		public AmstradGraphicsContextImpl(Dimension displayCanvasSize) {
			this.displayCanvasSize = displayCanvasSize;
		}

		@Override
		public Font getSystemFont() {
			if (systemFont == null) {
				float size = (float) (getDisplayCanvasSize().getWidth() / getTextColumns());
				systemFont = getJemuInstance().getDisplay().getDisplayFont().deriveFont(size);
			}
			return systemFont;
		}

		@Override
		public AmstradSystemColors getSystemColors() {
			return AmstradSystemColors.getSystemColors(getMonitorMode());
		}

		@Override
		public AmstradMonitorMode getMonitorMode() {
			return getMonitor().getMonitorMode();
		}

		@Override
		public Dimension getDisplayCanvasSize() {
			return displayCanvasSize;
		}

		@Override
		public int getTextRows() {
			return getBasicRuntime().getDisplayTextRows();
		}

		@Override
		public int getTextColumns() {
			return getBasicRuntime().getDisplayTextColumns();
		}

		@Override
		public int getDefaultBorderColorIndex() {
			return getBasicRuntime().getDefaultBorderColorIndex();
		}

		@Override
		public int getDefaultPaperColorIndex() {
			return getBasicRuntime().getDefaultPaperColorIndex();
		}

		@Override
		public int getDefaultPenColorIndex() {
			return getBasicRuntime().getDefaultPenColorIndex();
		}

		@Override
		public Dimension getPrimaryDisplaySourceResolution() {
			return primaryDisplaySourceResolution;
		}

		public void setPrimaryDisplaySourceResolution(Dimension resolution) {
			primaryDisplaySourceResolution = resolution;
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
					if (getFrame().getJMenuBar() != null && !getAmstradPc().getMonitor().isWindowFullscreen()) {
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

	private class JemuSecondaryDisplaySourceBridge implements SecondaryDisplaySource {

		private AmstradAlternativeDisplaySource source;

		private AmstradMonitorMode rememberedMonitorMode;

		private boolean rememberedMonitorEffect;

		private boolean rememberedMonitorScanLinesEffect;

		private boolean rememberedMonitorBilinearEffect;

		public JemuSecondaryDisplaySourceBridge(AmstradAlternativeDisplaySource source) {
			this.source = source;
		}

		@Override
		public void init(JComponent displayComponent) {
			AmstradMonitor monitor = getMonitor();
			rememberedMonitorMode = monitor.getMonitorMode();
			rememberedMonitorEffect = monitor.isMonitorEffectOn();
			rememberedMonitorScanLinesEffect = monitor.isMonitorScanLinesEffectOn();
			rememberedMonitorBilinearEffect = monitor.isMonitorBilinearEffectOn();
			getSource().init(displayComponent, getGraphicsContext(), getKeyboard().getController());
		}

		@Override
		public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds) {
			getSource().renderOntoDisplay(display, displayBounds, getGraphicsContext());
		}

		@Override
		public void dispose(JComponent displayComponent) {
			getSource().dispose(displayComponent);
			if (getSource().isRestoreMonitorSettingsOnDispose()) {
				restoreMonitorSettings();
			}
		}

		private void restoreMonitorSettings() {
			AmstradMonitor monitor = getMonitor();
			monitor.setMonitorMode(rememberedMonitorMode);
			monitor.setMonitorEffect(rememberedMonitorEffect);
			monitor.setMonitorScanLinesEffect(rememberedMonitorScanLinesEffect);
			monitor.setMonitorBilinearEffect(rememberedMonitorBilinearEffect);
		}

		private AmstradAlternativeDisplaySource getSource() {
			return source;
		}

	}

	private class AmstradKeyboardControllerImpl extends AmstradKeyboardAdapter implements AmstradKeyboardController {

		private int lastKeyModifiers;

		private boolean blockKeyboardPending;

		public AmstradKeyboardControllerImpl(AmstradKeyboard keyboard) {
			keyboard.addKeyboardListener(this);
		}

		@Override
		public synchronized void sendKeyboardEventsToComputer(boolean sendToComputer) {
			if (sendToComputer) {
				Switches.blockKeyboard = false;
				blockKeyboardPending = false;
				resetKeyModifiers(); // essential to sync modifiers with JEMU's computer
			} else {
				if (lastKeyModifiers == 0) {
					Switches.blockKeyboard = true;
					blockKeyboardPending = false;
				} else {
					blockKeyboardPending = true;
				}
			}
		}

		@Override
		public synchronized void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
			lastKeyModifiers = event.getKey().getModifiersEx();
			if (blockKeyboardPending && lastKeyModifiers == 0) {
				Switches.blockKeyboard = true;
				blockKeyboardPending = false;
			}
		}

		public synchronized void resetKeyModifiers() {
			lastKeyModifiers = 0;
			getJemuInstance().resetKeyModifiers();
		}

	}

	private class ActionableDialogHandler implements ActionableDialogListener {

		public ActionableDialogHandler() {
		}

		@Override
		public void dialogButtonClicked(ActionableDialog dialog, ActionableDialogButton button) {
			if (button.isClosingDialog()) {
				resetKeyModifiers();
			}
		}

		@Override
		public void dialogCancelled(ActionableDialog dialog) {
		}

		@Override
		public void dialogConfirmed(ActionableDialog dialog) {
		}

		@Override
		public void dialogClosed(ActionableDialog dialog) {
			resetKeyModifiers();
		}

		private void resetKeyModifiers() {
			// A dialog catches key events when in focus. When the dialog is invoked by a key combination involving
			// modifiers, this may leave the JEMU instance and JEMU computer in an obsolete key modifier state causing
			// artefacts when resuming focus. To prevent this, we reset modifiers when a dialog is closed.
			((AmstradKeyboardControllerImpl) getKeyboard().getController()).resetKeyModifiers();
		}

	}

	private class AlternativeDisplaySourceRenderer extends Thread {

		private boolean stop;

		public AlternativeDisplaySourceRenderer() {
			setDaemon(true);
		}

		@Override
		public void run() {
			System.out.println("Alternative render thread started");
			final Display display = getJemuInstance().getDisplay();
			while (!isStopped()) {
				display.updateImage(true);
			}
			System.out.println("Alternative render thread stopped");
		}

		public void stopRendering() {
			stop = true;
		}

		public boolean isStopped() {
			return stop;
		}

	}

}