package org.maia.amstrad.pc.action;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class AmstradPcMenuMaker {

	private AmstradPcActions actions;

	private MenuFlavor menuFlavor;

	public AmstradPcMenuMaker(AmstradPcActions actions) {
		this(actions, MenuFlavor.FULL_MENU);
	}

	public AmstradPcMenuMaker(AmstradPcActions actions, MenuFlavor menuFlavor) {
		this.actions = actions;
		this.menuFlavor = menuFlavor;
	}

	public JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu());
		menubar.add(createEmulatorMenu());
		menubar.add(createMonitorMenu());
		menubar.add(createWindowMenu());
		return menubar;
	}

	public JPopupMenu createPopupMenu() {
		if (isKioskFlavor()) {
			return createKioskPopupMenu();
		} else {
			return createFullPopupMenu();
		}
	}

	private JPopupMenu createFullPopupMenu() {
		JPopupMenu popup = new JPopupMenu("Amstrad Menu");
		popup.add(createFileMenu());
		popup.add(createEmulatorMenu());
		popup.add(createMonitorMenu());
		popup.add(createWindowMenu());
		return popup;
	}

	private JPopupMenu createKioskPopupMenu() {
		JPopupMenu popup = new JPopupMenu("Amstrad Menu");
		popup.add(createProgramBrowserMenuItem());
		popup.add(createProgramBrowserSetupMenuItem());
		popup.add(createProgramInfoMenuItem());
		popup.add(createPauseResumeMenuItem());
		popup.add(new JSeparator());
		popup.add(createScreenshotMenuItem());
		popup.add(createScreenshotWithMonitorEffectMenuItem());
		popup.add(createMonitorModeMenu());
		popup.add(createMonitorEffectsMenu());
		popup.add(new JSeparator());
		popup.add(createQuitMenuItem());
		return popup;
	}

	private JMenu createFileMenu() {
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
		menu.add(createQuitMenuItem());
		return (JMenu) updateLookAndFeel(menu);
	}

	private JMenuItem createProgramBrowserMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getProgramBrowserAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
		return updateLookAndFeel(item);
	}

	private JMenuItem createProgramBrowserSetupMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getProgramBrowserSetupAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateLookAndFeel(item);
	}

	private JMenuItem createProgramInfoMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getProgramInfoAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		return updateLookAndFeel(item);
	}

	private JMenuItem createLoadBasicSourceFileMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLoadBasicSourceFileAction()));
	}

	private JMenuItem createLoadBasicBinaryFileMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLoadBasicBinaryFileAction()));
	}

	private JMenuItem createLoadSnapshotFileMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLoadSnapshotFileAction()));
	}

	private JMenuItem createSaveBasicSourceFileMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getSaveBasicSourceFileAction()));
	}

	private JMenuItem createSaveBasicBinaryFileMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getSaveBasicBinaryFileAction()));
	}

	private JMenuItem createSaveSnapshotFileMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getSaveSnapshotFileAction()));
	}

	private JMenuItem createQuitMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getQuitAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateLookAndFeel(item);
	}

	private JMenu createEmulatorMenu() {
		JMenu menu = new JMenu("Emulator");
		menu.add(createAutoTypeFileMenuItem());
		menu.add(createBasicMenu());
		menu.add(new JSeparator());
		menu.add(createPauseResumeMenuItem());
		menu.add(createRebootMenuItem());
		return (JMenu) updateLookAndFeel(menu);
	}

	private JMenuItem createAutoTypeFileMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getAutoTypeFileAction()));
	}

	private JMenuItem createPauseResumeMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getPauseResumeAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0));
		return updateLookAndFeel(item, UIResources.pauseIcon);
	}

	private JMenuItem createRebootMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getRebootAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateLookAndFeel(item);
	}

	private JMenu createBasicMenu() {
		JMenu menu = new JMenu("Basic");
		menu.add(createLocomotiveBasicBreakEscapeMenuItem());
		menu.add(new JSeparator());
		menu.add(createLocomotiveBasicNewMenuItem());
		menu.add(createLocomotiveBasicRunMenuItem());
		menu.add(createLocomotiveBasicListMenuItem());
		menu.add(createLocomotiveBasicClsMenuItem());
		menu.add(createLocomotiveBasicClearMenuItem());
		return (JMenu) updateLookAndFeel(menu);
	}

	private JMenuItem createLocomotiveBasicBreakEscapeMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLocomotiveBasicBreakEscapeAction()));
	}

	private JMenuItem createLocomotiveBasicNewMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLocomotiveBasicNewAction()));
	}

	private JMenuItem createLocomotiveBasicRunMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLocomotiveBasicRunAction()));
	}

	private JMenuItem createLocomotiveBasicListMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLocomotiveBasicListAction()));
	}

	private JMenuItem createLocomotiveBasicClsMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLocomotiveBasicClsAction()));
	}

	private JMenuItem createLocomotiveBasicClearMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getLocomotiveBasicClearAction()));
	}

	private JMenu createMonitorMenu() {
		JMenu menu = new JMenu("Monitor");
		menu.add(createDisplaySystemColorsMenuItem());
		menu.add(createMonitorModeMenu());
		menu.add(createMonitorEffectsMenu());
		menu.add(new JSeparator());
		menu.add(createScreenshotMenuItem());
		menu.add(createScreenshotWithMonitorEffectMenuItem());
		return (JMenu) updateLookAndFeel(menu);
	}

	private JMenuItem createDisplaySystemColorsMenuItem() {
		return updateLookAndFeel(new JMenuItem(getActions().getDisplaySystemColorsAction()));
	}

	private JMenuItem createScreenshotMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getScreenshotAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		return updateLookAndFeel(item);
	}

	private JMenuItem createScreenshotWithMonitorEffectMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getScreenshotWithMonitorEffectAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		return updateLookAndFeel(item);
	}

	private JMenu createMonitorModeMenu() {
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
		return (JMenu) updateLookAndFeel(menu);
	}

	private JRadioButtonMenuItem createMonitorModeColorMenuItem() {
		return (JRadioButtonMenuItem) updateLookAndFeel(
				new JRadioButtonMenuItem(getActions().getMonitorModeColorAction()));
	}

	private JRadioButtonMenuItem createMonitorModeGreenMenuItem() {
		return (JRadioButtonMenuItem) updateLookAndFeel(
				new JRadioButtonMenuItem(getActions().getMonitorModeGreenAction()));
	}

	private JRadioButtonMenuItem createMonitorModeGrayMenuItem() {
		return (JRadioButtonMenuItem) updateLookAndFeel(
				new JRadioButtonMenuItem(getActions().getMonitorModeGrayAction()));
	}

	private JMenu createMonitorEffectsMenu() {
		JMenu menu = new JMenu("Effects");
		menu.add(new MonitorEffectMenuHelper(createMonitorEffectMenuItem(), getMonitor()).getCheckbox());
		menu.add(new MonitorScanLinesEffectMenuHelper(createMonitorScanLinesEffectMenuItem(), getMonitor())
				.getCheckbox());
		menu.add(
				new MonitorBilinearEffectMenuHelper(createMonitorBilinearEffectMenuItem(), getMonitor()).getCheckbox());
		return (JMenu) updateLookAndFeel(menu);
	}

	private JCheckBoxMenuItem createMonitorEffectMenuItem() {
		return (JCheckBoxMenuItem) updateLookAndFeel(new JCheckBoxMenuItem(getActions().getMonitorEffectAction()));
	}

	private JCheckBoxMenuItem createMonitorScanLinesEffectMenuItem() {
		return (JCheckBoxMenuItem) updateLookAndFeel(
				new JCheckBoxMenuItem(getActions().getMonitorScanLinesEffectAction()));
	}

	private JCheckBoxMenuItem createMonitorBilinearEffectMenuItem() {
		return (JCheckBoxMenuItem) updateLookAndFeel(
				new JCheckBoxMenuItem(getActions().getMonitorBilinearEffectAction()));
	}

	private JMenu createWindowMenu() {
		JMenu menu = new JMenu("Window");
		menu.add(new WindowTitleAutoUpdateMenuHelper(createWindowDynamicTitleMenuItem(), getMonitor()).getCheckbox());
		menu.add(new WindowAlwaysOnTopMenuHelper(createWindowAlwaysOnTopMenuItem(), getMonitor()).getCheckbox());
		menu.add(new JSeparator());
		menu.add(createMonitorFullscreenMenuItem());
		return (JMenu) updateLookAndFeel(menu);
	}

	private JCheckBoxMenuItem createWindowDynamicTitleMenuItem() {
		return (JCheckBoxMenuItem) updateLookAndFeel(new JCheckBoxMenuItem(getActions().getWindowDynamicTitleAction()));
	}

	private JCheckBoxMenuItem createWindowAlwaysOnTopMenuItem() {
		return (JCheckBoxMenuItem) updateLookAndFeel(new JCheckBoxMenuItem(getActions().getWindowAlwaysOnTopAction()));
	}

	private JMenuItem createMonitorFullscreenMenuItem() {
		JMenuItem item = new JMenuItem(getActions().getMonitorFullscreenAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		return updateLookAndFeel(item);
	}

	private JMenuItem updateLookAndFeel(JMenuItem item) {
		return updateLookAndFeel(item, null);
	}

	private JMenuItem updateLookAndFeel(JMenuItem item, Icon icon) {
		if (isKioskFlavor()) {
			item.setFont(item.getFont().deriveFont(MenuFlavor.KIOSK_MENU.getFontSize()));
		}
		if (icon != null) {
			item.setIcon(icon);
		}
		return item;
	}

	private boolean isKioskFlavor() {
		return MenuFlavor.KIOSK_MENU.equals(getMenuFlavor());
	}

	private AmstradMonitor getMonitor() {
		return getActions().getAmstradPc().getMonitor();
	}

	public AmstradPcActions getActions() {
		return actions;
	}

	public MenuFlavor getMenuFlavor() {
		return menuFlavor;
	}

	public static enum MenuFlavor {

		FULL_MENU(12f),

		KIOSK_MENU(24f);

		private float fontSize;

		private MenuFlavor(float fontSize) {
			this.fontSize = fontSize;
		}

		private float getFontSize() {
			return fontSize;
		}

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
			AmstradMonitorMode monitorMode = monitor.getMonitorMode();
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
			return monitor.isMonitorScanLinesEffectOn();
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
			return monitor.isMonitorBilinearEffectOn();
		}

	}

	private static class WindowTitleAutoUpdateMenuHelper extends MonitorCheckboxMenuHelper {

		public WindowTitleAutoUpdateMenuHelper(JCheckBoxMenuItem checkbox, AmstradMonitor monitor) {
			super(checkbox, monitor);
		}

		@Override
		public void amstradWindowTitleDynamicChanged(AmstradMonitor monitor) {
			syncMenu(monitor);
		}

		@Override
		protected boolean getState(AmstradMonitor monitor) {
			return monitor.isWindowTitleDynamic();
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

}