package org.maia.amstrad.pc.impl.jemu;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;

import javax.swing.SwingUtilities;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradFileType;
import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicMemoryFullException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcPerformanceAdapter;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.impl.MemoryTrapProcessor;
import org.maia.amstrad.pc.impl.MemoryTrapTask;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.memory.AmstradMemoryTrap;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.util.SystemUtils;

import jemu.core.device.Computer;
import jemu.core.device.ComputerPerformanceListener;
import jemu.core.device.memory.MemoryWriteObserver;
import jemu.settings.Settings;
import jemu.system.cpc.CPC;
import jemu.ui.Autotype;
import jemu.ui.Display;
import jemu.ui.Display.PrimaryDisplaySourceListener;
import jemu.ui.DisplayPerformanceListener;
import jemu.ui.Switches;

public abstract class JemuAmstradPc extends AmstradPc
		implements PrimaryDisplaySourceListener, ComputerPerformanceListener, DisplayPerformanceListener {

	private boolean started;

	private boolean terminated;

	private boolean paused;

	private JemuKeyboard keyboard;

	private JemuMemory memory;

	private JemuMonitor monitor;

	private JemuTape tape;

	private JemuAudio audio;

	private JemuBasicRuntime basicRuntime;

	private MemoryTrapProcessor memoryTrapProcessor;

	private JemuGraphicsContext graphicsContext;

	private AdaptiveDisplayPerformanceBooster displayPerformanceBooster;

	private static boolean instanceRunning; // maximum 1 running Jemu in JVM (static Switches etc.)

	private static final String SETTING_DISPLAY_BOOSTER = "display.booster";

	protected JemuAmstradPc() {
		this.keyboard = createKeyboard();
		this.memory = createMemory();
		this.monitor = createMonitor();
		this.tape = createTape();
		this.audio = createAudio();
		this.basicRuntime = createBasicRuntime();
		this.memoryTrapProcessor = createMemoryTrapProcessor();
		this.graphicsContext = createGraphicsContext();
		this.displayPerformanceBooster = new AdaptiveDisplayPerformanceBooster();
	}

	protected abstract JemuKeyboard createKeyboard();

	protected abstract JemuMemory createMemory();

	protected abstract JemuMonitor createMonitor();

	protected JemuTape createTape() {
		return new JemuTape(this);
	}

	protected JemuAudio createAudio() {
		return new JemuAudio(this);
	}

	protected JemuBasicRuntime createBasicRuntime() {
		return new JemuBasicRuntime();
	}

	protected MemoryTrapProcessor createMemoryTrapProcessor() {
		return new MemoryTrapProcessor();
	}

	protected JemuGraphicsContext createGraphicsContext() {
		return new JemuGraphicsContext();
	}

	@Override
	public final void start(boolean waitUntilReady, boolean silent) {
		boolean floppySound = false;
		synchronized (this) {
			checkNoInstanceRunning();
			checkNotStarted();
			checkNotTerminated();
			floppySound = Switches.FloppySound;
			if (silent)
				Switches.FloppySound = false;
			doStart();
			getJoystick(AmstradJoystickID.JOYSTICK0); // init
			getJoystick(AmstradJoystickID.JOYSTICK1); // init
			getMemoryTrapProcessor().start();
			if (Settings.getBoolean(SETTING_DISPLAY_BOOSTER, true))
				getDisplayPerformanceBooster().start();
			setStarted(true);
			JemuAmstradPc.setInstanceRunning(true);
			fireStartedEvent();
		}
		if (waitUntilReady)
			waitUntilBasicRuntimeReady();
		if (silent)
			Switches.FloppySound = floppySound;
	}

	protected abstract void doStart();

	@Override
	public synchronized final void reboot(boolean waitUntilReady, boolean silent) {
		checkStartedNotTerminated();
		boolean floppySound = Switches.FloppySound;
		if (silent)
			Switches.FloppySound = false;
		doReboot();
		fireRebootingEvent();
		fireTurboModeChanged();
		if (waitUntilReady)
			waitUntilBasicRuntimeReady();
		if (silent)
			Switches.FloppySound = floppySound;
	}

	protected abstract void doReboot();

	@Override
	public synchronized final void terminate() {
		if (!isTerminated()) {
			if (isStarted()) {
				Autotype.clearText();
				doTerminate();
				getMemoryTrapProcessor().stopProcessing();
				if (getMonitor().isAlternativeDisplaySourceShowing()) {
					getMonitor().resetDisplaySource();
				}
			}
			setTerminated(true);
			setInstanceRunning(false);
			fireTerminatedEvent();
		}
	}

	protected abstract void doTerminate();

	private void waitUntilBasicRuntimeReady() {
		System.out.println("Waiting until Basic runtime is Ready");
		SystemUtils.sleep(1000L); // making sure "ready" turns false first
		getBasicRuntime().waitUntilReady(8000L);
		System.out.println("Basic runtime is Ready");
	}

	@Override
	public synchronized final void pause() {
		checkStartedNotTerminated();
		if (!isPaused()) {
			setPaused(true);
			getMonitor().getDisplayComponent().repaint(); // making sure pause overlay is shown
			// Disconnect from the AWT event dispatch thread, as this may lead to deadlock!
			SystemUtils.runOutsideAwtEventDispatchThread(new Runnable() {

				@Override
				public void run() {
					if (!isTerminated()) {
						doPause();
						if (!selfManagesPauseStateEvents())
							firePausingEvent();
					}
				}
			});
		}
	}

	protected abstract void doPause();

	/**
	 * Immediately pauses this Amstrad PC
	 * <p>
	 * This method MUST NOT be called from the <em>AWT Event Dispatch thread</em> as this may lead to deadlock!
	 * </p>
	 * 
	 * @see SwingUtilities#isEventDispatchThread()
	 */
	@Override
	public synchronized final void pauseImmediately() {
		checkStartedNotTerminated();
		if (!isPaused()) {
			setPaused(true);
			doPauseImmediately();
			if (!selfManagesPauseStateEvents())
				firePausingEvent();
		}
	}

	protected abstract void doPauseImmediately();

	@Override
	public synchronized final void resume() {
		checkStartedNotTerminated();
		if (isPaused()) {
			setPaused(false);
			// Disconnect from the AWT event dispatch thread, as this may lead to deadlock!
			SystemUtils.runOutsideAwtEventDispatchThread(new Runnable() {

				@Override
				public void run() {
					if (!isTerminated()) {
						doResume();
						if (!selfManagesPauseStateEvents())
							fireResumingEvent();
					}
				}
			});
		}
	}

	protected abstract void doResume();

	protected boolean selfManagesPauseStateEvents() {
		return false;
	}

	@Override
	public final void load(AmstradPcSnapshotFile snapshotFile) throws IOException {
		checkStartedNotTerminated();
		File file = snapshotFile.getFile();
		System.out.println("Loading snapshot from " + file.getPath());
		doLoad(file);
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
		fireProgramLoaded();
	}

	protected abstract void doLoad(File snapshotFile) throws IOException;

	@Override
	public void save(AmstradPcSnapshotFile snapshotFile) {
		checkStartedNotTerminated();
		File file = snapshotFile.getFile();
		System.out.println("Saving snapshot to " + file.getPath());
		Settings.set(Settings.SNAPSHOT_FILE, file.getAbsolutePath());
		Switches.uncompressed = AmstradFileType.JAVACPC_SNAPSHOT_FILE_UNCOMPRESSED.matches(file);
		Switches.save64 = true; // 64k RAM memory dump
		AmstradFactory.getInstance().getAmstradContext().setCurrentDirectory(file.getParentFile());
	}

	@Override
	public void primaryDisplaySourceResolutionChanged(Display display, Dimension resolution) {
		getGraphicsContext().setPrimaryDisplaySourceResolution(resolution);
	}

	@Override
	public void displayPerformanceUpdate(Display display, long timeIntervalMillis, int framesPainted,
			int imagesUpdated) {
		fireDisplayPerformanceUpdate(timeIntervalMillis, framesPainted, imagesUpdated);
	}

	@Override
	public void processorPerformanceUpdate(Computer computer, long timeIntervalMillis, int timerSyncs, int laggingSyncs,
			int throttledSyncs) {
		fireProcessorPerformanceUpdate(timeIntervalMillis, timerSyncs, laggingSyncs, throttledSyncs);
	}

	protected static void checkNoInstanceRunning() {
		if (isInstanceRunning())
			throw new IllegalStateException("There can only be a single JEMU Amstrad PC running");
	}

	protected static boolean isInstanceRunning() {
		return JemuAmstradPc.instanceRunning;
	}

	protected static void setInstanceRunning(boolean instanceRunning) {
		JemuAmstradPc.instanceRunning = instanceRunning;
	}

	@Override
	public boolean isStarted() {
		return started;
	}

	protected void setStarted(boolean started) {
		this.started = started;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	protected void setPaused(boolean paused) {
		this.paused = paused;
	}

	@Override
	public boolean isTerminated() {
		return terminated;
	}

	protected void setTerminated(boolean terminated) {
		this.terminated = terminated;
	}

	@Override
	public JemuKeyboard getKeyboard() {
		return keyboard;
	}

	@Override
	public JemuMemory getMemory() {
		return memory;
	}

	@Override
	public JemuMonitor getMonitor() {
		return monitor;
	}

	@Override
	public JemuTape getTape() {
		return tape;
	}

	@Override
	public JemuAudio getAudio() {
		return audio;
	}

	@Override
	public JemuBasicRuntime getBasicRuntime() {
		return basicRuntime;
	}

	protected MemoryTrapProcessor getMemoryTrapProcessor() {
		return memoryTrapProcessor;
	}

	protected JemuGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

	protected abstract Font getJemuDisplayFont();

	private AdaptiveDisplayPerformanceBooster getDisplayPerformanceBooster() {
		return displayPerformanceBooster;
	}

	protected abstract class JemuBaseMonitor extends JemuMonitor {

		protected JemuBaseMonitor() {
			super(JemuAmstradPc.this);
		}

		@Override
		public JemuGraphicsContext getGraphicsContext() {
			return JemuAmstradPc.this.getGraphicsContext();
		}

	}

	protected class JemuBasicRuntime extends LocomotiveBasicRuntime {

		public JemuBasicRuntime() {
			super(JemuAmstradPc.this);
		}

		@Override
		public boolean isReady() {
			return isDirectModus() && !getKeyboardForBasic().isTyping();
		}

		@Override
		public boolean isDirectModus() {
			return getKeyboardForBasic().isOnBasicPrompt() && getKeyboardForBasic().isInBasicInterpretModus();
		}

		@Override
		protected void loadByteCode(BasicByteCode code) throws BasicMemoryFullException {
			super.loadByteCode(code);
			fireProgramLoaded();
		}

		private JemuKeyboard getKeyboardForBasic() {
			return (JemuKeyboard) getKeyboard();
		}

	}

	protected class JemuMemoryTrapBridge implements MemoryWriteObserver {

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
				getMemoryTrapProcessor().addTask(task);
			}
		}

		private AmstradMemoryTrap getMemoryTrap() {
			return memoryTrap;
		}

	}

	protected class JemuGraphicsContext implements AmstradGraphicsContext {

		private Dimension displayCanvasSize;

		private Dimension primaryDisplaySourceResolution;

		private Font systemFont;

		public JemuGraphicsContext() {
			this(new Dimension(getBasicRuntime().getDisplayCanvasWidth(), getBasicRuntime().getDisplayCanvasHeight()));
		}

		public JemuGraphicsContext(Dimension displayCanvasSize) {
			this.displayCanvasSize = displayCanvasSize;
		}

		@Override
		public Font getSystemFont() {
			if (systemFont == null) {
				float size = (float) (getDisplayCanvasSize().getWidth() / getTextColumns());
				systemFont = getJemuDisplayFont().deriveFont(size);
			}
			return systemFont;
		}

		@Override
		public AmstradSystemColors getSystemColors() {
			return AmstradSystemColors.getSystemColors(getMonitorMode());
		}

		@Override
		public AmstradMonitorMode getMonitorMode() {
			return getMonitor().getMode();
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

	/**
	 * Adaptive Display performance booster
	 * 
	 * <p>
	 * On some systems it was observed that the <em>vSync</em> (image update) rate suddenly degraded for unknown
	 * reasons. It sometimes recovers but in other cases not or at least not so quickly. It was also observed that a
	 * simple pause-resume of the <code>Computer</code> restores the vSync performance instantly. This helper class
	 * automates this trick.
	 * </p>
	 * 
	 * @see CPC#vSync()
	 * @see Display#updateImage(boolean)
	 */
	private class AdaptiveDisplayPerformanceBooster extends AmstradPcPerformanceAdapter
			implements AmstradPcStateListener {

		private int[] monitorData = new int[10]; // vSyncs per second

		private int monitorDataIndex; // slot for the next data value

		private int monitorDataPoints; // available historic data values

		private int maximumDataValue; // all-times observed maximum

		private int upperboundDataValue = 50; // outliers above this value

		private float degradationThresholdFactor;

		private long lastBoostTime = 0L;

		private int minimumSecondsToNextBoost = 0;

		private int minimumSecondsBetweenBoosts = 10;

		private int maximumSecondsBetweenBoosts = 120;

		private float secondsMultiplierBetweenBoosts = 1.5f;

		public AdaptiveDisplayPerformanceBooster() {
			this(0.8f);
		}

		public AdaptiveDisplayPerformanceBooster(float degradationThresholdFactor) {
			this.degradationThresholdFactor = degradationThresholdFactor;
		}

		public void start() {
			System.out.println("Starting Display performance booster");
			resetMonitoring();
			addPerformanceListener(this);
			addStateListener(this);
		}

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			resetMonitoring();
		}

		@Override
		public void amstradPcPausing(AmstradPc amstradPc) {
			resetMonitoringData();
		}

		@Override
		public void amstradPcResuming(AmstradPc amstradPc) {
			resetMonitoringData();
		}

		@Override
		public void amstradPcRebooting(AmstradPc amstradPc) {
			resetMonitoring();
		}

		@Override
		public void amstradPcTerminated(AmstradPc amstradPc) {
			resetMonitoring();
		}

		@Override
		public void amstradPcProgramLoaded(AmstradPc amstradPc) {
			resetMonitoring();
		}

		@Override
		public void displayPerformanceUpdate(AmstradPc amstradPc, long timeIntervalMillis, int framesPainted,
				int imagesUpdated) {
			if (!amstradPc.isPaused()) {
				int syncsPerSecond = Math.round(imagesUpdated / (timeIntervalMillis / 1000f));
				syncsPerSecond = Math.min(syncsPerSecond, upperboundDataValue);
				addMonitoringDataPoint(syncsPerSecond);
			}
		}

		private synchronized void resetMonitoring() {
			resetMonitoringData();
			lastBoostTime = 0L;
			minimumSecondsToNextBoost = 0;
		}

		private synchronized void resetMonitoringData() {
			monitorDataIndex = 0;
			monitorDataPoints = 0;
		}

		private synchronized void addMonitoringDataPoint(int syncsPerSecond) {
			monitorData[monitorDataIndex] = syncsPerSecond;
			monitorDataIndex = (monitorDataIndex + 1) % monitorData.length;
			monitorDataPoints = Math.min(monitorDataPoints + 1, monitorData.length);
			maximumDataValue = Math.max(maximumDataValue, syncsPerSecond);
			if (isEligibleForBoost()) {
				boostAsync();
			}
		}

		private void boostAsync() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					boost();
				}
			}).start();
		}

		private synchronized void boost() {
			if (!isPaused()) {
				System.out.println(
						"Display BOOST (avg=" + getAverageDataValue() + ", max=" + getMaximumDataValue() + ")");
				getMemory().pauseComputerInstantly();
				getMemory().resumeComputerInstantly();
				updateMinimumSecondsToNextBoost();
				lastBoostTime = System.currentTimeMillis();
			}
		}

		private void updateMinimumSecondsToNextBoost() {
			if (minimumSecondsToNextBoost == 0) {
				minimumSecondsToNextBoost = minimumSecondsBetweenBoosts;
			} else {
				minimumSecondsToNextBoost = Math.min(
						Math.round(minimumSecondsToNextBoost * secondsMultiplierBetweenBoosts),
						maximumSecondsBetweenBoosts);
			}
			System.out.println("Next Display boost no earlier than " + minimumSecondsToNextBoost + " seconds");
		}

		private boolean isEligibleForBoost() {
			long now = System.currentTimeMillis();
			if (now < lastBoostTime + minimumSecondsToNextBoost * 1000L)
				return false;
			return isDegradedPerformance();
		}

		private boolean isDegradedPerformance() {
			if (!isCompleteMonitoringData()) {
				return false; // too early to judge
			} else {
				return getAverageDataValue() < getDegradationThresholdFactor() * getMaximumDataValue();
			}
		}

		private boolean isCompleteMonitoringData() {
			return monitorDataPoints == monitorData.length;
		}

		private int getAverageDataValue() {
			if (monitorDataPoints == 0)
				return 0;
			int sum = 0;
			for (int i = 0; i < monitorDataPoints; i++) {
				sum += monitorData[(monitorDataIndex - 1 - i + monitorData.length) % monitorData.length];
			}
			return sum / monitorDataPoints;
		}

		private int getMaximumDataValue() {
			return maximumDataValue;
		}

		private float getDegradationThresholdFactor() {
			return degradationThresholdFactor;
		}

	}

}