package org.maia.amstrad.pc.action;

import java.util.HashMap;
import java.util.Map;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class AmstradPcActions {

	private AmstradPc amstradPc;

	/* File actions */

	private ProgramBrowserAction programBrowserAction;

	private ProgramBrowserSetupAction programBrowserSetupAction;

	private ProgramBrowserResetAction programBrowserResetAction;

	private ProgramInfoAction programInfoAction;

	private LoadBasicSourceFileAction loadBasicSourceFileAction;

	private LoadBasicBinaryFileAction loadBasicBinaryFileAction;

	private LoadSnapshotFileAction loadSnapshotFileAction;

	private SaveBasicSourceFileAction saveBasicSourceFileAction;

	private SaveBasicBinaryFileAction saveBasicBinaryFileAction;

	private SaveSnapshotFileAction saveSnapshotFileAction;

	private PowerOffAction powerOffAction;

	/* Emulator actions */

	private AmstradSystemColorsDisplayAction amstradSystemColorsDisplayAction;

	private BasicMemoryDisplayAction basicMemoryDisplayAction;

	private ShowSystemLogsAction showSystemLogsAction;

	private Map<AmstradJoystickID, JoystickSetupAction> joystickSetupActions;

	private Map<AmstradJoystickID, JoystickActivationAction> joystickActivationActions;

	private VirtualKeyboardAction virtualKeyboardAction;

	private AutoTypeFileAction autoTypeFileAction;

	private BreakEscapeAction breakEscapeAction;

	private AudioAction audioAction;

	private TurboAction turboAction;

	private PauseResumeAction pauseResumeAction;

	private RebootAction rebootAction;

	/* Monitor actions */

	private ScreenshotAction screenshotAction;

	private ScreenshotWithoutBorderAction screenshotWithoutBorderAction;

	private ScreenshotWithMonitorEffectAction screenshotWithMonitorEffectAction;

	private MonitorModeAction monitorModeColorAction;

	private MonitorModeAction monitorModeGreenAction;

	private MonitorModeAction monitorModeGrayAction;

	private MonitorEffectAction monitorEffectAction;

	private MonitorScanLinesEffectAction monitorScanLinesEffectAction;

	private MonitorBilinearEffectAction monitorBilinearEffectAction;

	private MonitorGateArrayAction monitorGateArrayAction;

	private MonitorAutoHideCursorAction monitorAutoHideCursorAction;

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
			programBrowserSetupAction = new ProgramBrowserSetupAction(getAmstradPc());
		}
		return programBrowserSetupAction;
	}

	public ProgramBrowserResetAction getProgramBrowserResetAction() {
		if (programBrowserResetAction == null) {
			programBrowserResetAction = new ProgramBrowserResetAction(getAmstradPc());
		}
		return programBrowserResetAction;
	}

	public ProgramInfoAction getProgramInfoAction() {
		if (programInfoAction == null) {
			programInfoAction = new ProgramInfoAction(getAmstradPc());
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

	public PowerOffAction getPowerOffAction() {
		if (powerOffAction == null) {
			powerOffAction = new PowerOffAction(getAmstradPc());
		}
		return powerOffAction;
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

	public TurboAction getTurboAction() {
		if (turboAction == null) {
			turboAction = new TurboAction(getAmstradPc());
		}
		return turboAction;
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

	public ScreenshotWithoutBorderAction getScreenshotWithoutBorderAction() {
		if (screenshotWithoutBorderAction == null) {
			screenshotWithoutBorderAction = new ScreenshotWithoutBorderAction(getAmstradPc());
		}
		return screenshotWithoutBorderAction;
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

	public MonitorAutoHideCursorAction getMonitorAutoHideCursorAction() {
		if (monitorAutoHideCursorAction == null) {
			monitorAutoHideCursorAction = new MonitorAutoHideCursorAction(getAmstradPc());
		}
		return monitorAutoHideCursorAction;
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

	public ShowSystemLogsAction getShowSystemLogsAction() {
		if (showSystemLogsAction == null) {
			showSystemLogsAction = new ShowSystemLogsAction(getAmstradPc());
		}
		return showSystemLogsAction;
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

	public VirtualKeyboardAction getVirtualKeyboardAction() {
		if (virtualKeyboardAction == null) {
			virtualKeyboardAction = new VirtualKeyboardAction(getAmstradPc());
		}
		return virtualKeyboardAction;
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