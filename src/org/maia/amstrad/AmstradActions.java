package org.maia.amstrad;

import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicBreakEscapeAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicClearAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicClsAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicListAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicNewAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicRunAction;
import org.maia.amstrad.gui.browser.action.ProgramBrowserAction;
import org.maia.amstrad.gui.browser.action.ProgramBrowserSetupAction;
import org.maia.amstrad.gui.browser.action.ProgramInfoAction;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AutoTypeFileAction;
import org.maia.amstrad.pc.action.DisplaySystemColorsAction;
import org.maia.amstrad.pc.action.LoadBasicBinaryFileAction;
import org.maia.amstrad.pc.action.LoadBasicSourceFileAction;
import org.maia.amstrad.pc.action.LoadSnapshotFileAction;
import org.maia.amstrad.pc.action.MonitorBilinearEffectAction;
import org.maia.amstrad.pc.action.MonitorEffectAction;
import org.maia.amstrad.pc.action.MonitorFullscreenAction;
import org.maia.amstrad.pc.action.MonitorModeAction;
import org.maia.amstrad.pc.action.MonitorScanLinesEffectAction;
import org.maia.amstrad.pc.action.PauseResumeAction;
import org.maia.amstrad.pc.action.QuitAction;
import org.maia.amstrad.pc.action.RebootAction;
import org.maia.amstrad.pc.action.SaveBasicBinaryFileAction;
import org.maia.amstrad.pc.action.SaveBasicSourceFileAction;
import org.maia.amstrad.pc.action.SaveSnapshotFileAction;
import org.maia.amstrad.pc.action.ScreenshotAction;
import org.maia.amstrad.pc.action.ScreenshotWithMonitorEffectAction;
import org.maia.amstrad.pc.action.WindowAlwaysOnTopAction;
import org.maia.amstrad.pc.action.WindowDynamicTitleAction;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class AmstradActions {

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

	private PauseResumeAction pauseResumeAction;

	private RebootAction rebootAction;

	private LocomotiveBasicBreakEscapeAction locomotiveBasicBreakEscapeAction;

	private LocomotiveBasicNewAction locomotiveBasicNewAction;

	private LocomotiveBasicRunAction locomotiveBasicRunAction;

	private LocomotiveBasicListAction locomotiveBasicListAction;

	private LocomotiveBasicClsAction locomotiveBasicClsAction;

	private LocomotiveBasicClearAction locomotiveBasicClearAction;

	/* Monitor actions */

	private DisplaySystemColorsAction displaySystemColorsAction;

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

	public AmstradActions(AmstradPc amstradPc) {
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

	public LocomotiveBasicBreakEscapeAction getLocomotiveBasicBreakEscapeAction() {
		if (locomotiveBasicBreakEscapeAction == null) {
			locomotiveBasicBreakEscapeAction = new LocomotiveBasicBreakEscapeAction(getAmstradPc());
		}
		return locomotiveBasicBreakEscapeAction;
	}

	public LocomotiveBasicNewAction getLocomotiveBasicNewAction() {
		if (locomotiveBasicNewAction == null) {
			locomotiveBasicNewAction = new LocomotiveBasicNewAction(getAmstradPc());
		}
		return locomotiveBasicNewAction;
	}

	public LocomotiveBasicRunAction getLocomotiveBasicRunAction() {
		if (locomotiveBasicRunAction == null) {
			locomotiveBasicRunAction = new LocomotiveBasicRunAction(getAmstradPc());
		}
		return locomotiveBasicRunAction;
	}

	public LocomotiveBasicListAction getLocomotiveBasicListAction() {
		if (locomotiveBasicListAction == null) {
			locomotiveBasicListAction = new LocomotiveBasicListAction(getAmstradPc());
		}
		return locomotiveBasicListAction;
	}

	public LocomotiveBasicClsAction getLocomotiveBasicClsAction() {
		if (locomotiveBasicClsAction == null) {
			locomotiveBasicClsAction = new LocomotiveBasicClsAction(getAmstradPc());
		}
		return locomotiveBasicClsAction;
	}

	public LocomotiveBasicClearAction getLocomotiveBasicClearAction() {
		if (locomotiveBasicClearAction == null) {
			locomotiveBasicClearAction = new LocomotiveBasicClearAction(getAmstradPc());
		}
		return locomotiveBasicClearAction;
	}

	public DisplaySystemColorsAction getDisplaySystemColorsAction() {
		if (displaySystemColorsAction == null) {
			displaySystemColorsAction = new DisplaySystemColorsAction(getAmstradPc());
		}
		return displaySystemColorsAction;
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