package org.maia.amstrad.pc.action;

import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.gui.browser.action.ProgramBrowserAction;
import org.maia.amstrad.gui.browser.action.ProgramBrowserSetupAction;
import org.maia.amstrad.gui.browser.action.ProgramInfoAction;
import org.maia.amstrad.gui.colors.AmstradSystemColorsDisplayAction;
import org.maia.amstrad.gui.memory.BasicMemoryDisplayAction;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class AmstradPcActions {

	private AmstradPc amstradPc;

	/* File actions */

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

	private AmstradSystemColorsDisplayAction amstradSystemColorsDisplayAction;

	private BasicMemoryDisplayAction basicMemoryDisplayAction;

	private ShowJavaConsoleAction showJavaConsoleAction;

	private Map<AmstradJoystickID, JoystickSetupAction> joystickSetupActions;

	private Map<AmstradJoystickID, JoystickActivationAction> joystickActivationActions;

	private AutoTypeFileAction autoTypeFileAction;

	private BreakEscapeAction breakEscapeAction;

	private AudioAction audioAction;

	private PauseResumeAction pauseResumeAction;

	private RebootAction rebootAction;

	/* Monitor actions */

	private ScreenshotAction screenshotAction;

	private ScreenshotWithMonitorEffectAction screenshotWithMonitorEffectAction;

	private MonitorModeAction monitorModeColorAction;

	private MonitorModeAction monitorModeGreenAction;

	private MonitorModeAction monitorModeGrayAction;

	private MonitorEffectAction monitorEffectAction;

	private MonitorScanLinesEffectAction monitorScanLinesEffectAction;

	private MonitorBilinearEffectAction monitorBilinearEffectAction;

	private MonitorGateArrayAction monitorGateArrayAction;

	private MonitorSingleSizeAction monitorSingleSizeAction;

	private MonitorDoubleSizeAction monitorDoubleSizeAction;

	private MonitorTripleSizeAction monitorTripleSizeAction;

	private MonitorFullscreenAction monitorFullscreenAction;

	private MonitorShowSystemStatsAction monitorShowSystemStatsAction;

	/* Window actions */

	private WindowAlwaysOnTopAction windowAlwaysOnTopAction;

	private WindowCenterOnScreenAction windowCenterOnScreenAction;

	private AboutAction aboutAction;

	public AmstradPcActions(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
		this.joystickSetupActions = new HashMap<AmstradJoystickID, JoystickSetupAction>();
		this.joystickActivationActions = new HashMap<AmstradJoystickID, JoystickActivationAction>();
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
					"Color monitor (CTM644)");
		}
		return monitorModeColorAction;
	}

	public MonitorModeAction getMonitorModeGreenAction() {
		if (monitorModeGreenAction == null) {
			monitorModeGreenAction = new MonitorModeAction(AmstradMonitorMode.GREEN, getAmstradPc(),
					"Green monitor (GT65)");
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

	public MonitorGateArrayAction getMonitorGateArrayAction() {
		if (monitorGateArrayAction == null) {
			monitorGateArrayAction = new MonitorGateArrayAction(getAmstradPc());
		}
		return monitorGateArrayAction;
	}

	public MonitorSingleSizeAction getMonitorSingleSizeAction() {
		if (monitorSingleSizeAction == null) {
			monitorSingleSizeAction = new MonitorSingleSizeAction(getAmstradPc());
		}
		return monitorSingleSizeAction;
	}

	public MonitorDoubleSizeAction getMonitorDoubleSizeAction() {
		if (monitorDoubleSizeAction == null) {
			monitorDoubleSizeAction = new MonitorDoubleSizeAction(getAmstradPc());
		}
		return monitorDoubleSizeAction;
	}

	public MonitorTripleSizeAction getMonitorTripleSizeAction() {
		if (monitorTripleSizeAction == null) {
			monitorTripleSizeAction = new MonitorTripleSizeAction(getAmstradPc());
		}
		return monitorTripleSizeAction;
	}

	public MonitorFullscreenAction getMonitorFullscreenAction() {
		if (monitorFullscreenAction == null) {
			monitorFullscreenAction = new MonitorFullscreenAction(getAmstradPc());
		}
		return monitorFullscreenAction;
	}

	public MonitorShowSystemStatsAction getMonitorShowSystemStatsAction() {
		if (monitorShowSystemStatsAction == null) {
			monitorShowSystemStatsAction = new MonitorShowSystemStatsAction(getAmstradPc());
		}
		return monitorShowSystemStatsAction;
	}

	public ShowJavaConsoleAction getShowJavaConsoleAction() {
		if (showJavaConsoleAction == null) {
			showJavaConsoleAction = new ShowJavaConsoleAction(getAmstradPc());
		}
		return showJavaConsoleAction;
	}

	public JoystickSetupAction getJoystickSetupAction(AmstradJoystickID joystickId) {
		JoystickSetupAction action = joystickSetupActions.get(joystickId);
		if (action == null) {
			action = new JoystickSetupAction(getAmstradPc(), joystickId);
			joystickSetupActions.put(joystickId, action);
		}
		return action;
	}

	public JoystickActivationAction getJoystickActivationAction(AmstradJoystickID joystickId) {
		JoystickActivationAction action = joystickActivationActions.get(joystickId);
		if (action == null) {
			action = new JoystickActivationAction(getAmstradPc(), joystickId);
			joystickActivationActions.put(joystickId, action);
		}
		return action;
	}

	public WindowAlwaysOnTopAction getWindowAlwaysOnTopAction() {
		if (windowAlwaysOnTopAction == null) {
			windowAlwaysOnTopAction = new WindowAlwaysOnTopAction(getAmstradPc());
		}
		return windowAlwaysOnTopAction;
	}

	public WindowCenterOnScreenAction getWindowCenterOnScreenAction() {
		if (windowCenterOnScreenAction == null) {
			windowCenterOnScreenAction = new WindowCenterOnScreenAction(getAmstradPc());
		}
		return windowCenterOnScreenAction;
	}

	public AboutAction getAboutAction() {
		if (aboutAction == null) {
			aboutAction = new AboutAction(getAmstradPc());
		}
		return aboutAction;
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}