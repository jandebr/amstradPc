package org.maia.amstrad.pc.impl.jemu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradFileType;
import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicMemoryFullException;
import org.maia.amstrad.basic.BasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardController;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.pc.memory.AmstradMemoryTrap;
import org.maia.amstrad.pc.memory.AmstradMemoryTrapHandler;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;
import org.maia.amstrad.pc.monitor.display.overlay.AmstradDisplayOverlay;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.pc.tape.AmstradTape;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.util.AmstradUtils;
import org.maia.swing.dialog.ActionableDialog;
import org.maia.swing.dialog.ActionableDialog.ActionableDialogButton;
import org.maia.swing.dialog.ActionableDialogListener;

import jemu.core.device.BasicKeyboardPromptModus;
import jemu.core.device.Computer;
import jemu.core.device.ComputerKeyboardListener;
import jemu.core.device.memory.MemoryWriteObserver;
import jemu.settings.Settings;
import jemu.ui.Autotype;
import jemu.ui.Display;
import jemu.ui.Display.PrimaryDisplaySourceListener;
import jemu.ui.DisplayOverlay;
import jemu.ui.JEMU;
import jemu.ui.JEMU.PauseListener;
import jemu.ui.SecondaryDisplaySource;
import jemu.ui.Switches;

public class JemuAmstradPc extends AmstradPc implements PauseListener, PrimaryDisplaySourceListener {

	private JEMU jemuInstance;

	private AmstradKeyboard keyboard;

	private AmstradMemory memory;

	private AmstradMonitor monitor;

	private AmstradTape tape;

	private BasicRuntime basicRuntime;

	private AmstradGraphicsContextImpl graphicsContext;

	private AutonomousDisplayRenderer autonomousDisplayRenderer;

	private MemoryTrapProcessor memoryTrapProcessor;

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
		this.tape = new JemuTapeImpl();
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
		setMemoryTrapProcessor(new MemoryTrapProcessor());
		getMemoryTrapProcessor().start();
		setStarted(true);
		setInstanceRunning(true);
		fireStartedEvent();
		if (waitUntilReady)
			waitUntilReady();
		if (silent)
			Switches.FloppySound = floppySound;
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
		handleAutonomousDisplayRendering();
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
				getMemoryTrapProcessor().stopProcessing();
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
	public AmstradTape getTape() {
		return tape;
	}

	@Override
	public BasicRuntime getBasicRuntime() {
		return basicRuntime;
	}

	private void waitUntilReady() {
		System.out.println("Waiting until Basic runtime is Ready");
		AmstradUtils.sleep(1000L); // making sure "ready" turns false first
		getBasicRuntime().waitUntilReady(8000L);
		System.out.println("Basic runtime is Ready");
	}

	private synchronized void handleAutonomousDisplayRendering() {
		if (getMonitor().isAlternativeDisplaySourceShowing() && isPaused()) {
			// When computer is paused, there is no vSync and we need to render ourselves
			if (getAutonomousDisplayRenderer() == null || getAutonomousDisplayRenderer().isStopped()) {
				AutonomousDisplayRenderer renderer = new AutonomousDisplayRenderer();
				setAutonomousDisplayRenderer(renderer);
				renderer.start();
			}
		} else {
			// Stop our own rendering
			if (getAutonomousDisplayRenderer() != null) {
				getAutonomousDisplayRenderer().stopRendering();
				setAutonomousDisplayRenderer(null);
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

	private AutonomousDisplayRenderer getAutonomousDisplayRenderer() {
		return autonomousDisplayRenderer;
	}

	private void setAutonomousDisplayRenderer(AutonomousDisplayRenderer renderer) {
		this.autonomousDisplayRenderer = renderer;
	}

	private MemoryTrapProcessor getMemoryTrapProcessor() {
		return memoryTrapProcessor;
	}

	private void setMemoryTrapProcessor(MemoryTrapProcessor processor) {
		this.memoryTrapProcessor = processor;
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

		private boolean onBasicPrompt;

		private long onBasicPromptSince;

		private boolean inBasicInterpretModus;

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
		public boolean isTyping() {
			if (isAutotyping())
				return true;
			if (getOnBasicPromptSince() >= System.currentTimeMillis() - 100L)
				return true;
			return false;
		}

		@Override
		public synchronized void type(CharSequence text, boolean waitUntilTyped) {
			checkStarted();
			checkNotTerminated();
			setAutotyping(true);
			Autotype.typeText(text);
			resetEscapeKeyCounter();
			if (waitUntilTyped) {
				waitUntilAutotypeEnded();
			}
		}

		public void breakEscape() {
			checkStarted();
			checkNotTerminated();
			getJemuInstance().breakEscape();
		}

		private synchronized void waitUntilAutotypeEnded() {
			while (isAutotyping()) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
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
				fireKeyboardBreakEscaped();
			}
		}

		@Override
		public void computerSuppressEscapeKey(Computer computer) {
			resetEscapeKeyCounter();
		}

		@Override
		public void computerAutotypeStarted(Computer computer) {
			System.out.println("Autotype started");
			setAutotyping(true);
		}

		@Override
		public synchronized void computerAutotypeEnded(Computer computer) {
			System.out.println("Autotype ended");
			setAutotyping(false);
			notifyAll();
		}

		@Override
		public void computerEnterBasicKeyboardPrompt(Computer computer, BasicKeyboardPromptModus modus) {
			setOnBasicPrompt(true);
			setOnBasicPromptSince(System.currentTimeMillis());
			setInBasicInterpretModus(BasicKeyboardPromptModus.INTERPRET.equals(modus));
		}

		@Override
		public void computerExitBasicKeyboardPrompt(Computer computer, BasicKeyboardPromptModus modus) {
			setOnBasicPrompt(false);
			setInBasicInterpretModus(BasicKeyboardPromptModus.INTERPRET.equals(modus));
		}

		@Override
		public AmstradKeyboardController getController() {
			return controller;
		}

		private void resetEscapeKeyCounter() {
			escapeKeyCounter = 0;
		}

		@Override
		public boolean isAutotyping() {
			return autotyping;
		}

		private void setAutotyping(boolean autotyping) {
			this.autotyping = autotyping;
		}

		public boolean isOnBasicPrompt() {
			return onBasicPrompt;
		}

		private void setOnBasicPrompt(boolean onBasicPrompt) {
			this.onBasicPrompt = onBasicPrompt;
		}

		private long getOnBasicPromptSince() {
			return onBasicPromptSince;
		}

		private void setOnBasicPromptSince(long since) {
			this.onBasicPromptSince = since;
		}

		public boolean isInBasicInterpretModus() {
			return inBasicInterpretModus;
		}

		private void setInBasicInterpretModus(boolean interpretModus) {
			this.inBasicInterpretModus = interpretModus;
		}

	}

	private class JemuMemoryImpl extends AmstradMemory {

		private boolean jemuRunningAtStartOfTES;

		public JemuMemoryImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		public void startThreadExclusiveSession() {
			super.startThreadExclusiveSession();
			if (!isNestedThreadExclusiveSession()) {
				// Pause computer when running
				synchronized (JemuAmstradPc.this) {
					jemuRunningAtStartOfTES = getJemuInstance().isRunning();
					if (jemuRunningAtStartOfTES) {
						getJemuInstance().pauseComputer();
					}
				}
			}
		}

		@Override
		public void endThreadExclusiveSession() {
			if (!isNestedThreadExclusiveSession()) {
				// Resume computer when paused
				if (jemuRunningAtStartOfTES) {
					synchronized (JemuAmstradPc.this) {
						getJemuInstance().goComputer();
					}
				}
			}
			super.endThreadExclusiveSession();
		}

		@Override
		public byte readByte(int memoryAddress) {
			return getJemuInstance().readByteFromUnmappedMemory(memoryAddress);
		}

		@Override
		public byte[] readBytes(int memoryOffset, int memoryLength) {
			return getJemuInstance().readBytesFromUnmappedMemory(memoryOffset, memoryLength);
		}

		@Override
		public void writeByte(int memoryAddress, byte value) {
			getJemuInstance().writeByteToUnmappedMemory(memoryAddress, value);
		}

		@Override
		public void writeBytes(int memoryOffset, byte[] data, int dataOffset, int dataLength) {
			getJemuInstance().writeBytesToUnmappedMemory(memoryOffset, data, dataOffset, dataLength);
		}

		@Override
		protected synchronized void addMemoryTrap(AmstradMemoryTrap memoryTrap) {
			getJemuInstance().addMemoryWriteObserver(new JemuMemoryTrapBridge(memoryTrap));
		}

		@Override
		public synchronized void removeMemoryTrapsAt(int memoryAddress) {
			List<MemoryWriteObserver> observers = new Vector<MemoryWriteObserver>(
					getJemuInstance().getMemoryWriteObservers());
			for (MemoryWriteObserver observer : observers) {
				if (observer.getObservedMemoryAddress() == memoryAddress) {
					getJemuInstance().removeMemoryWriteObserver(observer);
				}
			}
		}

		@Override
		public synchronized void removeAllMemoryTraps() {
			getJemuInstance().removeAllMemoryWriteObservers();
		}

	}

	private class JemuMemoryTrapBridge implements MemoryWriteObserver {

		private AmstradMemoryTrap memoryTrap;

		public JemuMemoryTrapBridge(AmstradMemoryTrap memoryTrap) {
			this.memoryTrap = memoryTrap;
		}

		@Override
		public int getObservedMemoryAddress() {
			return getMemoryTrap().getMemoryAddress();
		}

		@Override
		public void notifyWrite(int memoryAddress, byte memoryValue) {
			if (memoryValue != getMemoryTrap().getMemoryValueOff()) {
				MemoryTrapTask task = new MemoryTrapTask(getMemoryTrap(), memoryValue);
				getMemoryTrapProcessor().processTaskDeferred(task);
			}
		}

		private AmstradMemoryTrap getMemoryTrap() {
			return memoryTrap;
		}

	}

	private class JemuMonitorImpl extends AmstradMonitor {

		public JemuMonitorImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		public AmstradGraphicsContext getGraphicsContext() {
			return JemuAmstradPc.this.getGraphicsContext();
		}

		@Override
		public Component getDisplayPane() {
			return getJemuInstance();
		}

		@Override
		public JComponent getDisplayComponent() {
			return getJemuInstance().getDisplay();
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
			checkStarted();
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
				boolean scaneffect = Display.scaneffect;
				boolean masked = display.masked;
				boolean showeffect = display.showeffect;
				Display.scaneffect = monitorEffect;
				display.masked = monitorEffect;
				display.showeffect = monitorEffect;
				image = display.getImage();
				Display.scaneffect = scaneffect;
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
					handleAutonomousDisplayRendering();
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
				handleAutonomousDisplayRendering();
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

		@Override
		public boolean isAlternativeDisplaySourceShowing() {
			return getJemuInstance().getDisplay().getSecondaryDisplaySource() != null;
		}

		@Override
		public void setCustomDisplayOverlay(AmstradDisplayOverlay overlay) {
			synchronized (JemuAmstradPc.this) {
				if (overlay != null) {
					getJemuInstance().getDisplay().installCustomDisplayOverlay(new JemuDisplayOverlayBridge(overlay));
				} else {
					resetCustomDisplayOverlay();
				}
			}
		}

		@Override
		public void resetCustomDisplayOverlay() {
			synchronized (JemuAmstradPc.this) {
				getJemuInstance().getDisplay().uninstallCustomDisplayOverlay();
			}
		}

	}

	private class JemuTapeImpl extends AmstradTape {

		public JemuTapeImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		public void load(AmstradPcSnapshotFile snapshotFile) {
			checkStarted();
			checkNotTerminated();
			File file = snapshotFile.getFile();
			try {
				notifyTapeReading(file.getName());
				getJemuInstance().doAutoOpen(file);
				AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
				System.out.println("Loaded snapshot from " + file.getPath());
				fireProgramLoaded();
			} finally {
				notifyTapeStoppedReading();
			}
		}

		@Override
		public void save(AmstradPcSnapshotFile snapshotFile) {
			checkStarted();
			checkNotTerminated();
			File file = snapshotFile.getFile();
			try {
				notifyTapeWriting(file.getName());
				Settings.set(Settings.SNAPSHOT_FILE, file.getAbsolutePath());
				Switches.uncompressed = AmstradFileType.JAVACPC_SNAPSHOT_FILE_UNCOMPRESSED.matches(file);
				Switches.save64 = true; // 64k RAM memory dump
				waitUntilSnapshotReady(file);
				AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
				System.out.println("Saved snapshot to " + file.getPath());
			} finally {
				notifyTapeStoppedWriting();
			}
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

	}

	private class JemuBasicRuntimeImpl extends LocomotiveBasicRuntime {

		public JemuBasicRuntimeImpl() {
			super(JemuAmstradPc.this);
		}

		@Override
		public boolean isReady() {
			JemuKeyboardImpl keyboard = getKeyboardForBasic();
			if (keyboard.isTyping())
				return false;
			if (!keyboard.isOnBasicPrompt())
				return false;
			return keyboard.isInBasicInterpretModus();
		}

		@Override
		public void breakEscape() {
			getKeyboardForBasic().breakEscape();
		}

		@Override
		protected void loadByteCode(BasicByteCode code) throws BasicMemoryFullException {
			super.loadByteCode(code);
			fireProgramLoaded();
		}

		private JemuKeyboardImpl getKeyboardForBasic() {
			return (JemuKeyboardImpl) getKeyboard();
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
		public Insets getBorderInsetsForDisplaySize(Dimension size) {
			double sy = size.height / 272.0;
			double sx = size.width / 384.0;
			int top = (int) Math.floor(sy * 40.0);
			int left = (int) Math.floor(sx * 32.0);
			int bottom = size.height - top - (int) Math.ceil(sy * 200.0);
			int right = size.width - left - (int) Math.ceil(sx * 320.0);
			return new Insets(top, left, bottom, right);
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

	private class JemuDisplayOverlayBridge implements DisplayOverlay {

		private AmstradDisplayOverlay source;

		public JemuDisplayOverlayBridge(AmstradDisplayOverlay source) {
			this.source = source;
		}

		@Override
		public void init(JComponent displayComponent) {
			getSource().init(displayComponent, getGraphicsContext());
		}

		@Override
		public void renderOntoDisplay(Graphics2D display, Rectangle displayBounds) {
			getSource().renderOntoDisplay(display, displayBounds, getGraphicsContext());
		}

		@Override
		public void dispose(JComponent displayComponent) {
			getSource().dispose(displayComponent);
		}

		private AmstradDisplayOverlay getSource() {
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

	private class AutonomousDisplayRenderer extends Thread {

		private boolean stop;

		public AutonomousDisplayRenderer() {
			super("AutonomousDisplayRenderer");
			setDaemon(true);
		}

		@Override
		public void run() {
			System.out.println("Autonomous render thread started");
			final Display display = getJemuInstance().getDisplay();
			while (!isStopped()) {
				display.updateImage(true);
			}
			System.out.println("Autonomous render thread stopped");
		}

		public void stopRendering() {
			stop = true;
		}

		public boolean isStopped() {
			return stop;
		}

	}

	private class MemoryTrapProcessor extends Thread {

		private boolean stop;

		private Queue<MemoryTrapTask> taskQueue;

		public MemoryTrapProcessor() {
			super("MemoryTrapProcessor");
			setDaemon(true);
			this.taskQueue = new LinkedList<MemoryTrapTask>();
		}

		@Override
		public synchronized void run() {
			System.out.println("Memorytrap processor thread started");
			while (!isStopped()) {
				MemoryTrapTask task = null;
				synchronized (getTaskQueue()) {
					task = getTaskQueue().peek();
				}
				boolean shouldWait = task == null;
				if (task != null) {
					processTask(task);
					synchronized (getTaskQueue()) {
						getTaskQueue().remove(task);
						shouldWait = getTaskQueue().isEmpty();
					}
				}
				if (shouldWait) {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
			}
			System.out.println("Memorytrap processor thread stopped");
		}

		public void processTaskDeferred(MemoryTrapTask task) {
			boolean shouldNotify = false;
			synchronized (getTaskQueue()) {
				if (getTaskQueue().offer(task)) {
					if (getTaskQueue().size() == 1) {
						shouldNotify = true;
					}
				}
			}
			if (shouldNotify) {
				synchronized (this) {
					notify();
				}
			}
		}

		private void processTask(MemoryTrapTask task) {
			AmstradMemoryTrap memoryTrap = task.getMemoryTrap();
			memoryTrap.reset();
			AmstradMemoryTrapHandler handler = memoryTrap.getHandler();
			handler.handleMemoryTrap(memoryTrap.getMemory(), memoryTrap.getMemoryAddress(), task.getMemoryValue());
		}

		public void stopProcessing() {
			stop = true;
		}

		public boolean isStopped() {
			return stop;
		}

		private Queue<MemoryTrapTask> getTaskQueue() {
			return taskQueue;
		}

	}

	private class MemoryTrapTask {

		private AmstradMemoryTrap memoryTrap;

		private byte memoryValue;

		public MemoryTrapTask(AmstradMemoryTrap memoryTrap, byte memoryValue) {
			this.memoryTrap = memoryTrap;
			this.memoryValue = memoryValue;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("MemoryTrapTask [memoryAddress=");
			builder.append(getMemoryTrap().getMemoryAddress());
			builder.append(", memoryValue=");
			builder.append(getMemoryValue());
			builder.append("]");
			return builder.toString();
		}

		public AmstradMemoryTrap getMemoryTrap() {
			return memoryTrap;
		}

		public byte getMemoryValue() {
			return memoryValue;
		}

	}

}