package org.maia.amstrad.pc.action;

import org.maia.amstrad.gui.browser.action.ProgramBrowserAction;
import org.maia.amstrad.gui.browser.action.ProgramBrowserSetupAction;
import org.maia.amstrad.gui.browser.action.ProgramInfoAction;
import org.maia.amstrad.gui.colors.AmstradSystemColorsDisplayAction;
import org.maia.amstrad.gui.memory.BasicMemoryDisplayAction;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class AmstradPcActions {

	/* File actions */

	private AmstradPc amstradPc;

	private ProgramBrowserAction programBrowserAction;

	private ProgramBrowserSetupAction programBrowserSetupAction;

	private ProgramInfoAction programInfoAction;

	private LoadBasicSourceFileAction loadBasicSourceFileAction;

	private LoadBasicBinaryFileAction loadBasicBinaryFileAction;

	private LoadSnapshotFileAction loadSnapshotFileAction;

	private SaveBasicSourceFileAction saveBasicSourceFileAction;

	private SaveBasicBinaryFileAction saveBasicBinaryFileAction;

	private SaveSnapshotFileAction saveSnapshotFileAction;

	private QuitAction quitAction;

	/* Emulator actions */

	private AutoTypeFileAction autoTypeFileAction;

	private AudioAction audioAction;

	private PauseResumeAction pauseResumeAction;

	private RebootAction rebootAction;

	private BasicMemoryDisplayAction basicMemoryDisplayAction;

	private BreakEscapeAction breakEscapeAction;

	/* Monitor actions */

	private AmstradSystemColorsDisplayAction amstradSystemColorsDisplayAction;

	private ScreenshotAction screenshotAction;

	private ScreenshotWithMonitorEffectAction screenshotWithMonitorEffectAction;

	private MonitorModeAction monitorModeColorAction;

	private MonitorModeAction monitorModeGreenAction;

	private MonitorModeAction monitorModeGrayAction;

	private MonitorEffectAction monitorEffectAction;

	private MonitorScanLinesEffectAction monitorScanLinesEffectAction;

	private MonitorBilinearEffectAction monitorBilinearEffectAction;

	/* Window actions */

	private WindowDynamicTitleAction windowDynamicTitleAction;

	private WindowAlwaysOnTopAction windowAlwaysOnTopAction;

	private MonitorFullscreenAction monitorFullscreenAction;

	public AmstradPcActions(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	public ProgramBrowserAction getProgramBrowserAction() {
		if (programBrowserAction == null) {
			programBrowserAction = new ProgramBrowserAction(getAmstradPc());
		}
		return programBrowserAction;
	}

	public ProgramBrowserSetupAction getProgramBrowserSetupAction() {
		if (programBrowserSetupAction == null) {
			programBrowserSetupAction = new ProgramBrowserSetupAction(getProgramBrowserAction());
		}
		return programBrowserSetupAction;
	}

	public ProgramInfoAction getProgramInfoAction() {
		if (programInfoAction == null) {
			programInfoAction = new ProgramInfoAction(getProgramBrowserAction());
		}
		return programInfoAction;
	}

	public LoadBasicSourceFileAction getLoadBasicSourceFileAction() {
		if (loadBasicSourceFileAction == null) {
			loadBasicSourceFileAction = new LoadBasicSourceFileAction(getAmstradPc());
		}
		return loadBasicSourceFileAction;
	}

	public LoadBasicBinaryFileAction getLoadBasicBinaryFileAction() {
		if (loadBasicBinaryFileAction == null) {
			loadBasicBinaryFileAction = new LoadBasicBinaryFileAction(getAmstradPc());
		}
		return loadBasicBinaryFileAction;
	}

	public LoadSnapshotFileAction getLoadSnapshotFileAction() {
		if (loadSnapshotFileAction == null) {
			loadSnapshotFileAction = new LoadSnapshotFileAction(getAmstradPc());
		}
		return loadSnapshotFileAction;
	}

	public SaveBasicSourceFileAction getSaveBasicSourceFileAction() {
		if (saveBasicSourceFileAction == null) {
			saveBasicSourceFileAction = new SaveBasicSourceFileAction(getAmstradPc());
		}
		return saveBasicSourceFileAction;
	}

	public SaveBasicBinaryFileAction getSaveBasicBinaryFileAction() {
		if (saveBasicBinaryFileAction == null) {
			saveBasicBinaryFileAction = new SaveBasicBinaryFileAction(getAmstradPc());
		}
		return saveBasicBinaryFileAction;
	}

	public SaveSnapshotFileAction getSaveSnapshotFileAction() {
		if (saveSnapshotFileAction == null) {
			saveSnapshotFileAction = new SaveSnapshotFileAction(getAmstradPc());
		}
		return saveSnapshotFileAction;
	}

	public QuitAction getQuitAction() {
		if (quitAction == null) {
			quitAction = new QuitAction(getAmstradPc());
		}
		return quitAction;
	}

	public AutoTypeFileAction getAutoTypeFileAction() {
		if (autoTypeFileAction == null) {
			autoTypeFileAction = new AutoTypeFileAction(getAmstradPc());
		}
		return autoTypeFileAction;
	}

	public AudioAction getAudioAction() {
		if (audioAction == null) {
			audioAction = new AudioAction(getAmstradPc());
		}
		return audioAction;
	}

	public PauseResumeAction getPauseResumeAction() {
		if (pauseResumeAction == null) {
			pauseResumeAction = new PauseResumeAction(getAmstradPc());
		}
		return pauseResumeAction;
	}

	public RebootAction getRebootAction() {
		if (rebootAction == null) {
			rebootAction = new RebootAction(getAmstradPc());
		}
		return rebootAction;
	}

	public BasicMemoryDisplayAction getBasicMemoryDisplayAction() {
		if (basicMemoryDisplayAction == null) {
			basicMemoryDisplayAction = new BasicMemoryDisplayAction(getAmstradPc());
		}
		return basicMemoryDisplayAction;
	}

	public BreakEscapeAction getBreakEscapeAction() {
		if (breakEscapeAction == null) {
			breakEscapeAction = new BreakEscapeAction(getAmstradPc());
		}
		return breakEscapeAction;
	}

	public AmstradSystemColorsDisplayAction getAmstradSystemColorsDisplayAction() {
		if (amstradSystemColorsDisplayAction == null) {
			amstradSystemColorsDisplayAction = new AmstradSystemColorsDisplayAction(getAmstradPc());
		}
		return amstradSystemColorsDisplayAction;
	}

	public ScreenshotAction getScreenshotAction() {
		if (screenshotAction == null) {
			screenshotAction = new ScreenshotAction(getAmstradPc());
		}
		return screenshotAction;
	}

	public ScreenshotWithMonitorEffectAction getScreenshotWithMonitorEffectAction() {
		if (screenshotWithMonitorEffectAction == null) {
			screenshotWithMonitorEffectAction = new ScreenshotWithMonitorEffectAction(getAmstradPc());
		}
		return screenshotWithMonitorEffectAction;
	}

	public MonitorModeAction getMonitorModeColorAction() {
		if (monitorModeColorAction == null) {
			monitorModeColorAction = new MonitorModeAction(AmstradMonitorMode.COLOR, getAmstradPc(),
					"Color monitor (CTM640)");
		}
		return monitorModeColorAction;
	}

	public MonitorModeAction getMonitorModeGreenAction() {
		if (monitorModeGreenAction == null) {
			monitorModeGreenAction = new MonitorModeAction(AmstradMonitorMode.GREEN, getAmstradPc(),
					"Green monitor (GT64)");
		}
		return monitorModeGreenAction;
	}

	public MonitorModeAction getMonitorModeGrayAction() {
		if (monitorModeGrayAction == null) {
			monitorModeGrayAction = new MonitorModeAction(AmstradMonitorMode.GRAY, getAmstradPc(), "Gray monitor");
		}
		return monitorModeGrayAction;
	}

	public MonitorEffectAction getMonitorEffectAction() {
		if (monitorEffectAction == null) {
			monitorEffectAction = new MonitorEffectAction(getAmstradPc());
		}
		return monitorEffectAction;
	}

	public MonitorScanLinesEffectAction getMonitorScanLinesEffectAction() {
		if (monitorScanLinesEffectAction == null) {
			monitorScanLinesEffectAction = new MonitorScanLinesEffectAction(getAmstradPc());
		}
		return monitorScanLinesEffectAction;
	}

	public MonitorBilinearEffectAction getMonitorBilinearEffectAction() {
		if (monitorBilinearEffectAction == null) {
			monitorBilinearEffectAction = new MonitorBilinearEffectAction(getAmstradPc());
		}
		return monitorBilinearEffectAction;
	}

	public WindowDynamicTitleAction getWindowDynamicTitleAction() {
		if (windowDynamicTitleAction == null) {
			windowDynamicTitleAction = new WindowDynamicTitleAction(getAmstradPc());
		}
		return windowDynamicTitleAction;
	}

	public WindowAlwaysOnTopAction getWindowAlwaysOnTopAction() {
		if (windowAlwaysOnTopAction == null) {
			windowAlwaysOnTopAction = new WindowAlwaysOnTopAction(getAmstradPc());
		}
		return windowAlwaysOnTopAction;
	}

	public MonitorFullscreenAction getMonitorFullscreenAction() {
		if (monitorFullscreenAction == null) {
			monitorFullscreenAction = new MonitorFullscreenAction(getAmstradPc());
		}
		return monitorFullscreenAction;
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}