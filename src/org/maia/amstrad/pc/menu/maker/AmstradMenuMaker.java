package org.maia.amstrad.pc.menu.maker;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcActions;
import org.maia.amstrad.pc.action.MonitorModeAction;
import org.maia.amstrad.pc.action.MonitorSizeAction;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.joystick.AmstradJoystickStateAdapter;
import org.maia.amstrad.pc.menu.AmstradMenu;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public abstract class AmstradMenuMaker {

	private AmstradPc amstradPc;

	private AmstradMenuLookAndFeel lookAndFeel;

	protected AmstradMenuMaker(AmstradPc amstradPc, AmstradMenuLookAndFeel lookAndFeel) {
		this.amstradPc = amstradPc;
		this.lookAndFeel = lookAndFeel;
	}

	public AmstradMenu createMenu() {
		getLookAndFeel().applySystemWide();
		return doCreateMenu();
	}

	protected abstract AmstradMenu doCreateMenu();

	protected JMenu createFileMenu() {
		JMenu menu = new JMenu("File");
		menu.add(createProgramBrowserMenuItem());
		menu.add(createProgramBrowserSetupMenuItem());
		menu.add(createProgramInfoMenuItem());
		menu.add(new JSeparator());
		menu.add(createLoadBasicSourceFileMenuItem());
		menu.add(createLoadBasicBinaryFileMenuItem());
		menu.add(createLoadSnapshotFileMenuItem());
		menu.add(new JSeparator());
		menu.add(createSaveBasicSourceFileMenuItem());
		menu.add(createSaveBasicBinaryFileMenuItem());
		menu.add(createSaveSnapshotFileMenuItem());
		menu.add(new JSeparator());
		menu.add(createPowerOffMenuItem());
		return updateMenuLookAndFeel(menu);
	}

	protected JMenuItem createProgramBrowserMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getProgramBrowserAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
		return updateMenuItemLookAndFeel(item, UIResources.basicOrBrowserIcon);
	}

	protected JMenuItem createProgramBrowserSetupMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getProgramBrowserSetupAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateMenuItemLookAndFeel(item, UIResources.browserSetupIcon);
	}

	protected JMenuItem createProgramInfoMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getProgramInfoAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK));
		return updateMenuItemLookAndFeel(item, UIResources.infoIcon);
	}

	protected JMenuItem createLoadBasicSourceFileMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getLoadBasicSourceFileAction()));
	}

	protected JMenuItem createLoadBasicBinaryFileMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getLoadBasicBinaryFileAction()));
	}

	protected JMenuItem createLoadSnapshotFileMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getLoadSnapshotFileAction()));
	}

	protected JMenuItem createSaveBasicSourceFileMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getSaveBasicSourceFileAction()));
	}

	protected JMenuItem createSaveBasicBinaryFileMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getSaveBasicBinaryFileAction()));
	}

	protected JMenuItem createSaveSnapshotFileMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getSaveSnapshotFileAction()));
	}

	protected JMenuItem createPowerOffMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getPowerOffAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateMenuItemLookAndFeel(item, UIResources.powerOffIcon);
	}

	protected JMenu createEmulatorMenu() {
		JMenu menu = new JMenu("Emulator");
		menu.add(createAmstradSystemColorsDisplayMenuItem());
		menu.add(createBasicMemoryDisplayMenuItem());
		menu.add(createShowSystemLogsMenuItem());
		menu.add(new JSeparator());
		menu.add(createAudioMenuItem());
		menu.add(createVirtualKeyboardMenuItem());
		menu.add(createJoystickMenu());
		menu.add(new JSeparator());
		menu.add(createAutoTypeFileMenuItem());
		menu.add(createBreakEscapeMenuItem());
		menu.add(new JSeparator());
		menu.add(createPauseResumeMenuItem());
		menu.add(createRebootMenuItem());
		return updateMenuLookAndFeel(menu);
	}

	protected JMenuItem createAmstradSystemColorsDisplayMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getAmstradSystemColorsDisplayAction()));
	}

	protected JMenuItem createBasicMemoryDisplayMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getBasicMemoryDisplayAction()));
	}

	protected JMenuItem createShowSystemLogsMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getShowSystemLogsAction()));
	}

	protected JMenuItem createAutoTypeFileMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getAutoTypeFileAction()));
	}

	protected JMenuItem createBreakEscapeMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getBreakEscapeAction()));
	}

	protected JMenuItem createAudioMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getAudioAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
		return updateMenuItemLookAndFeel(item, UIResources.audioIcon);
	}

	protected JMenuItem createVirtualKeyboardMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getVirtualKeyboardAction()),
				UIResources.virtualKeyboardIcon);
	}

	protected JMenuItem createPauseResumeMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getPauseResumeAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0));
		return updateMenuItemLookAndFeel(item, UIResources.pauseResumeIcon);
	}

	protected JMenuItem createRebootMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getRebootAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateMenuItemLookAndFeel(item);
	}

	protected JMenu createJoystickMenu() {
		JMenu menu = new JMenu("Joysticks");
		menu.add(createJoystickActivationMenuItem(AmstradJoystickID.JOYSTICK0));
		menu.add(createJoystickSetupMenuItem(AmstradJoystickID.JOYSTICK0));
		menu.add(new JSeparator());
		menu.add(createJoystickActivationMenuItem(AmstradJoystickID.JOYSTICK1));
		menu.add(createJoystickSetupMenuItem(AmstradJoystickID.JOYSTICK1));
		return updateMenuLookAndFeel(menu, UIResources.joystickIcon);
	}

	protected JMenuItem createJoystickSetupMenuItem(AmstradJoystickID joystickId) {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getJoystickSetupAction(joystickId)));
	}

	protected JCheckBoxMenuItem createJoystickActivationMenuItem(AmstradJoystickID joystickId) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getJoystickActivationAction(joystickId));
		updateMenuItemLookAndFeel(item);
		return new JoystickActivationMenuHelper(item, getAmstradPc().getJoystick(joystickId)).getCheckbox();
	}

	protected JMenu createMonitorMenu() {
		JMenu menu = new JMenu("Monitor");
		menu.add(createMonitorModeMenu());
		menu.add(createMonitorEffectsMenu());
		menu.add(new JSeparator());
		menu.add(createMonitorSizeMenu());
		menu.add(createMonitorFullscreenMenuItem());
		menu.add(new JSeparator());
		menu.add(createScreenshotMenuItem());
		menu.add(createScreenshotWithMonitorEffectMenuItem());
		return updateMenuLookAndFeel(menu);
	}

	protected JMenuItem createScreenshotMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getScreenshotAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		return updateMenuItemLookAndFeel(item, UIResources.screenshotIcon);
	}

	protected JMenuItem createScreenshotWithMonitorEffectMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getScreenshotWithMonitorEffectAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateMenuItemLookAndFeel(item, UIResources.screenshotWithMonitorIcon);
	}

	protected JMenu createMonitorModeMenu() {
		JMenu menu = new JMenu("Monitor type");
		JRadioButtonMenuItem colorMode = createMonitorModeColorMenuItem();
		JRadioButtonMenuItem greenMode = createMonitorModeGreenMenuItem();
		JRadioButtonMenuItem grayMode = createMonitorModeGrayMenuItem();
		menu.add(colorMode);
		menu.add(greenMode);
		menu.add(grayMode);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(colorMode);
		buttonGroup.add(greenMode);
		buttonGroup.add(grayMode);
		MonitorModeMenuHelper helper = new MonitorModeMenuHelper(buttonGroup, getMonitor());
		return updateMenuLookAndFeel(menu, UIResources.monitorModeIcon);
	}

	protected JRadioButtonMenuItem createMonitorModeColorMenuItem() {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(getActions().getMonitorModeColorAction());
		return (JRadioButtonMenuItem) updateMenuItemLookAndFeel(item);
	}

	protected JRadioButtonMenuItem createMonitorModeGreenMenuItem() {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(getActions().getMonitorModeGreenAction());
		return (JRadioButtonMenuItem) updateMenuItemLookAndFeel(item);
	}

	protected JRadioButtonMenuItem createMonitorModeGrayMenuItem() {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(getActions().getMonitorModeGrayAction());
		return (JRadioButtonMenuItem) updateMenuItemLookAndFeel(item);
	}

	protected JMenu createMonitorEffectsMenu() {
		JMenu menu = new JMenu("Monitor effects");
		menu.add(createMonitorEffectMenuItem());
		menu.add(createMonitorScanLinesEffectMenuItem());
		menu.add(createMonitorBilinearEffectMenuItem());
		menu.add(createMonitorGateArrayMenuItem());
		menu.add(new JSeparator());
		menu.add(createMonitorAutoHideCursorMenuItem());
		menu.add(createMonitorShowSystemStatsMenuItem());
		return updateMenuLookAndFeel(menu, UIResources.monitorEffectIcon);
	}

	protected JCheckBoxMenuItem createMonitorEffectMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getMonitorEffectAction());
		updateMenuItemLookAndFeel(item);
		return new MonitorEffectMenuHelper(item, getMonitor()).getCheckbox();
	}

	protected JCheckBoxMenuItem createMonitorScanLinesEffectMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getMonitorScanLinesEffectAction());
		updateMenuItemLookAndFeel(item);
		return new MonitorScanLinesEffectMenuHelper(item, getMonitor()).getCheckbox();
	}

	protected JCheckBoxMenuItem createMonitorBilinearEffectMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getMonitorBilinearEffectAction());
		updateMenuItemLookAndFeel(item);
		return new MonitorBilinearEffectMenuHelper(item, getMonitor()).getCheckbox();
	}

	protected JCheckBoxMenuItem createMonitorGateArrayMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getMonitorGateArrayAction());
		updateMenuItemLookAndFeel(item);
		return new MonitorGateArrayMenuHelper(item, getMonitor()).getCheckbox();
	}

	protected JCheckBoxMenuItem createMonitorAutoHideCursorMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getMonitorAutoHideCursorAction());
		updateMenuItemLookAndFeel(item);
		return new MonitorAutoHideCursorMenuHelper(item, getMonitor()).getCheckbox();
	}

	protected JCheckBoxMenuItem createMonitorShowSystemStatsMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getMonitorShowSystemStatsAction());
		updateMenuItemLookAndFeel(item);
		return new MonitorShowSystemStatsMenuHelper(item, getMonitor()).getCheckbox();
	}

	protected JMenu createMonitorSizeMenu() {
		JMenu menu = new JMenu("Monitor size");
		JRadioButtonMenuItem singleSize = createMonitorSingleSizeMenuItem();
		JRadioButtonMenuItem doubleSize = createMonitorDoubleSizeMenuItem();
		JRadioButtonMenuItem tripleSize = createMonitorTripleSizeMenuItem();
		menu.add(singleSize);
		menu.add(doubleSize);
		menu.add(tripleSize);
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(singleSize);
		buttonGroup.add(doubleSize);
		buttonGroup.add(tripleSize);
		MonitorSizeMenuHelper helper = new MonitorSizeMenuHelper(buttonGroup, getMonitor());
		return updateMenuLookAndFeel(menu, UIResources.windowedIcon);
	}

	protected JRadioButtonMenuItem createMonitorSingleSizeMenuItem() {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(getActions().getMonitorSingleSizeAction());
		return (JRadioButtonMenuItem) updateMenuItemLookAndFeel(item);
	}

	protected JRadioButtonMenuItem createMonitorDoubleSizeMenuItem() {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(getActions().getMonitorDoubleSizeAction());
		return (JRadioButtonMenuItem) updateMenuItemLookAndFeel(item);
	}

	protected JRadioButtonMenuItem createMonitorTripleSizeMenuItem() {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(getActions().getMonitorTripleSizeAction());
		return (JRadioButtonMenuItem) updateMenuItemLookAndFeel(item);
	}

	protected JMenuItem createMonitorFullscreenMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getMonitorFullscreenAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		return updateMenuItemLookAndFeel(item, UIResources.windowIcon);
	}

	protected JMenu createWindowMenu() {
		JMenu menu = new JMenu("Window");
		menu.add(createWindowAlwaysOnTopMenuItem());
		menu.add(createWindowCenterOnScreenMenuItem());
		menu.add(new JSeparator());
		menu.add(createAboutMenuItem());
		return updateMenuLookAndFeel(menu);
	}

	protected JCheckBoxMenuItem createWindowAlwaysOnTopMenuItem() {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(getActions().getWindowAlwaysOnTopAction());
		updateMenuItemLookAndFeel(item);
		return new WindowAlwaysOnTopMenuHelper(item, getMonitor()).getCheckbox();
	}

	protected JMenuItem createWindowCenterOnScreenMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getWindowCenterOnScreenAction()));
	}

	protected JMenuItem createAboutMenuItem() {
		return updateMenuItemLookAndFeel(new JMenuItem(getActions().getAboutAction()));
	}

	protected JMenu updateMenuLookAndFeel(JMenu menu) {
		getLookAndFeel().applyToMenu(menu);
		return menu;
	}

	protected JMenu updateMenuLookAndFeel(JMenu menu, Icon icon) {
		getLookAndFeel().applyToMenu(menu, icon);
		return menu;
	}

	protected JMenuItem updateMenuItemLookAndFeel(JMenuItem item) {
		getLookAndFeel().applyToMenuItem(item);
		return item;
	}

	protected JMenuItem updateMenuItemLookAndFeel(JMenuItem item, Icon icon) {
		getLookAndFeel().applyToMenuItem(item, icon);
		return item;
	}

	protected AmstradMonitor getMonitor() {
		return getAmstradPc().getMonitor();
	}

	protected AmstradPcActions getActions() {
		return getAmstradPc().getActions();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public AmstradMenuLookAndFeel getLookAndFeel() {
		return lookAndFeel;
	}

	private static abstract class MonitorMenuHelper extends AmstradMonitorAdapter {

		protected MonitorMenuHelper() {
		}

		protected abstract void syncMenu(AmstradMonitor monitor);

	}

	private static class MonitorModeMenuHelper extends MonitorMenuHelper {

		private ButtonGroup buttonGroup;

		public MonitorModeMenuHelper(ButtonGroup buttonGroup, AmstradMonitor monitor) {
			this.buttonGroup = buttonGroup;
			syncMenu(monitor);
			monitor.addMonitorListener(this);
		}

		@Override
		public void amstradMonitorModeChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected void syncMenu(AmstradMonitor monitor) {
			AmstradMonitorMode monitorMode = monitor.getMode();
			for (Enumeration<AbstractButton> en = getButtonGroup().getElements(); en.hasMoreElements();) {
				AbstractButton button = en.nextElement();
				if (((MonitorModeAction) button.getAction()).getMode().equals(monitorMode)) {
					button.setSelected(true);
				}
			}
		}

		private ButtonGroup getButtonGroup() {
			return buttonGroup;
		}

	}

	private static class MonitorSizeMenuHelper extends MonitorMenuHelper {

		private ButtonGroup buttonGroup;

		public MonitorSizeMenuHelper(ButtonGroup buttonGroup, AmstradMonitor monitor) {
			this.buttonGroup = buttonGroup;
			syncMenu(monitor);
			monitor.addMonitorListener(this);
		}

		@Override
		public void amstradMonitorSizeChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected void syncMenu(AmstradMonitor monitor) {
			for (Enumeration<AbstractButton> en = getButtonGroup().getElements(); en.hasMoreElements();) {
				AbstractButton button = en.nextElement();
				int sizeFactor = ((MonitorSizeAction) button.getAction()).getSizeFactor();
				if (sizeFactor == 1 && monitor.isSingleSize()) {
					button.setSelected(true);
				} else if (sizeFactor == 2 && monitor.isDoubleSize()) {
					button.setSelected(true);
				} else if (sizeFactor == 3 && monitor.isTripleSize()) {
					button.setSelected(true);
				}
			}
		}

		private ButtonGroup getButtonGroup() {
			return buttonGroup;
		}

	}

	private static abstract class MonitorCheckboxMenuHelper extends MonitorMenuHelper {

		private JCheckBoxMenuItem checkbox;

		protected MonitorCheckboxMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			this.checkbox = checkbox;
			syncMenu(monitor);
			monitor.addMonitorListener(this);
		}

		@Override
		protected final void syncMenu(AmstradMonitor monitor) {
			getCheckbox().setSelected(getState(monitor));
		}

		protected abstract boolean getState(AmstradMonitor monitor);

		public JCheckBoxMenuItem getCheckbox() {
			return checkbox;
		}

	}

	private static class MonitorEffectMenuHelper extends MonitorCheckboxMenuHelper {

		public MonitorEffectMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradMonitorEffectChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isMonitorEffectOn();
		}

	}

	private static class MonitorScanLinesEffectMenuHelper extends MonitorCheckboxMenuHelper {

		public MonitorScanLinesEffectMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradMonitorScanLinesEffectChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isScanLinesEffectOn();
		}

	}

	private static class MonitorBilinearEffectMenuHelper extends MonitorCheckboxMenuHelper {

		public MonitorBilinearEffectMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradMonitorBilinearEffectChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isBilinearEffectOn();
		}

	}

	private static class MonitorGateArrayMenuHelper extends MonitorCheckboxMenuHelper {

		public MonitorGateArrayMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradMonitorGateArraySizeChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isFullGateArray();
		}

	}

	private static class MonitorAutoHideCursorMenuHelper extends MonitorCheckboxMenuHelper {

		public MonitorAutoHideCursorMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradMonitorAutoHideCursorChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isAutoHideCursor();
		}

	}

	private static class MonitorShowSystemStatsMenuHelper extends MonitorCheckboxMenuHelper {

		public MonitorShowSystemStatsMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradShowSystemStatsChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isShowSystemStats();
		}

	}

	private static class WindowAlwaysOnTopMenuHelper extends MonitorCheckboxMenuHelper {

		public WindowAlwaysOnTopMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradWindowAlwaysOnTopChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isWindowAlwaysOnTop();
		}

	}

	private static abstract class JoystickMenuHelper extends AmstradJoystickStateAdapter {

		protected JoystickMenuHelper() {
		}

		protected abstract void syncMenu(AmstradJoystick joystick);

	}

	private static class JoystickActivationMenuHelper extends JoystickMenuHelper {

		private JCheckBoxMenuItem checkbox;

		public JoystickActivationMenuHelper(JCheckBoxMenuItem checkbox, AmstradJoystick joystick) {
			this.checkbox = checkbox;
			syncMenu(joystick);
			joystick.addJoystickStateListener(this);
		}

		@Override
		public void amstradJoystickActivated(AmstradJoystick joystick) {
			syncMenu(joystick);
		}

		@Override
		public void amstradJoystickDeactivated(AmstradJoystick joystick) {
			syncMenu(joystick);
		}

		@Override
		protected void syncMenu(AmstradJoystick joystick) {
			getCheckbox().setSelected(joystick.isActive());
		}

		public JCheckBoxMenuItem getCheckbox() {
			return checkbox;
		}

	}

}