package org.maia.amstrad.pc;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import jemu.settings.Settings;

import org.maia.amstrad.pc.jemu.JemuAmstradPc;
import org.maia.amstrad.pc.menu.AmstradPcAction;
import org.maia.amstrad.pc.menu.AutoTypeFileAction;
import org.maia.amstrad.pc.menu.LoadBasicBinaryFileAction;
import org.maia.amstrad.pc.menu.LoadBasicSourceFileAction;
import org.maia.amstrad.pc.menu.LoadSnapshotFileAction;
import org.maia.amstrad.pc.menu.MonitorBilinearEffectAction;
import org.maia.amstrad.pc.menu.MonitorEffectAction;
import org.maia.amstrad.pc.menu.MonitorFullscreenAction;
import org.maia.amstrad.pc.menu.MonitorModeAction;
import org.maia.amstrad.pc.menu.MonitorScanLinesEffectAction;
import org.maia.amstrad.pc.menu.PauseResumeAction;
import org.maia.amstrad.pc.menu.ProgramBrowserAction;
import org.maia.amstrad.pc.menu.QuitAction;
import org.maia.amstrad.pc.menu.RebootAction;
import org.maia.amstrad.pc.menu.SaveBasicBinaryFileAction;
import org.maia.amstrad.pc.menu.SaveBasicSourceFileAction;
import org.maia.amstrad.pc.menu.SaveSnapshotFileAction;
import org.maia.amstrad.pc.menu.ScreenshotAction;
import org.maia.amstrad.pc.menu.ScreenshotWithMonitorEffectAction;
import org.maia.amstrad.pc.menu.UpdateWindowTitleAction;
import org.maia.amstrad.pc.menu.WindowAlwaysOnTopAction;

public class AmstradFactory {

	private static AmstradFactory instance;

	private AmstradContext context;

	private AmstradFactory() {
	}

	public AmstradContext getAmstradContext() {
		if (context == null) {
			AmstradSettings userSettings = createUserSettings();
			context = new AmstradContextImpl(userSettings, System.out, System.err);
		}
		return context;
	}

	private AmstradSettings createUserSettings() {
		return new AmstradSettingsImpl();
	}

	public AmstradPc createAmstradPc() {
		return new JemuAmstradPc();
	}

	public JMenuBar createMenuBar(AmstradPc amstradPc) {
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu(amstradPc));
		menubar.add(createEmulatorMenu(amstradPc));
		menubar.add(createCaptureMenu(amstradPc));
		menubar.add(createMonitorMenu(amstradPc));
		menubar.add(createWindowMenu(amstradPc));
		return menubar;
	}

	private JMenu createFileMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new ProgramBrowserAction(amstradPc)));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new LoadBasicSourceFileAction(amstradPc)));
		menu.add(new JMenuItem(new LoadBasicBinaryFileAction(amstradPc)));
		menu.add(new JMenuItem(new LoadSnapshotFileAction(amstradPc)));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new SaveBasicSourceFileAction(amstradPc)));
		menu.add(new JMenuItem(new SaveBasicBinaryFileAction(amstradPc)));
		menu.add(new JMenuItem(new SaveSnapshotFileAction(amstradPc)));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new QuitAction(amstradPc)));
		return menu;
	}

	private JMenu createEmulatorMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Emulator");
		menu.add(new JMenuItem(new AutoTypeFileAction(amstradPc)));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new PauseResumeAction(amstradPc)));
		menu.add(new JMenuItem(new RebootAction(amstradPc)));
		return menu;
	}

	private JMenu createCaptureMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Capture");
		menu.add(new JMenuItem(new ScreenshotAction(amstradPc)));
		menu.add(new JMenuItem(new ScreenshotWithMonitorEffectAction(amstradPc)));
		return menu;
	}

	private JMenu createMonitorMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Monitor");
		// Monitor modes
		MonitorModeMenuHelper.addModesToMenu(menu, amstradPc);
		// Effects
		menu.add(new JSeparator());
		JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem(new MonitorEffectAction(amstradPc));
		checkItem.setState(Settings.getBoolean(Settings.SCANEFFECT, true));
		menu.add(checkItem);
		checkItem = new JCheckBoxMenuItem(new MonitorScanLinesEffectAction(amstradPc));
		checkItem.setState(Settings.getBoolean(Settings.SCANLINES, false));
		menu.add(checkItem);
		checkItem = new JCheckBoxMenuItem(new MonitorBilinearEffectAction(amstradPc));
		checkItem.setState(Settings.getBoolean(Settings.BILINEAR, true));
		menu.add(checkItem);
		return menu;
	}

	private JMenu createWindowMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Window");
		// Update title
		JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem(new UpdateWindowTitleAction(amstradPc));
		checkItem.setState(Settings.getBoolean(Settings.UPDATETITLE, true));
		menu.add(checkItem);
		// Always on top
		WindowAlwaysOnTopMenuHelper.addToMenu(menu, amstradPc);
		// Fullscreen
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(new MonitorFullscreenAction(amstradPc));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK));
		menu.add(item);
		return menu;
	}

	public static AmstradFactory getInstance() {
		if (instance == null) {
			setInstance(new AmstradFactory());
		}
		return instance;
	}

	private static synchronized void setInstance(AmstradFactory factory) {
		if (instance == null) {
			instance = factory;
		}
	}

	private static abstract class MonitorMenuHelper extends AmstradPcMonitorAdapter {

		protected MonitorMenuHelper() {
		}

		protected abstract void syncMenu(AmstradPc amstradPc);

	}

	private static class MonitorModeMenuHelper extends MonitorMenuHelper {

		private ButtonGroup buttonGroup;

		private MonitorModeMenuHelper(ButtonGroup buttonGroup) {
			this.buttonGroup = buttonGroup;
		}

		public static void addModesToMenu(JMenu menu, AmstradPc amstradPc) {
			JRadioButtonMenuItem colorMode = new JRadioButtonMenuItem(new MonitorModeAction(AmstradMonitorMode.COLOR,
					amstradPc, "Color monitor"));
			JRadioButtonMenuItem greenMode = new JRadioButtonMenuItem(new MonitorModeAction(AmstradMonitorMode.GREEN,
					amstradPc, "Green monitor"));
			JRadioButtonMenuItem grayMode = new JRadioButtonMenuItem(new MonitorModeAction(AmstradMonitorMode.GRAY,
					amstradPc, "Gray monitor"));
			menu.add(colorMode);
			menu.add(greenMode);
			menu.add(grayMode);
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(colorMode);
			buttonGroup.add(greenMode);
			buttonGroup.add(grayMode);
			MonitorModeMenuHelper helper = new MonitorModeMenuHelper(buttonGroup);
			helper.syncMenu(amstradPc);
			amstradPc.addMonitorListener(helper);
		}

		@Override
		public void amstradPcMonitorModeChanged(AmstradPc amstradPc) {
			syncMenu(amstradPc);
		}

		@Override
		protected void syncMenu(AmstradPc amstradPc) {
			AmstradMonitorMode monitorMode = amstradPc.getMonitorMode();
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

	private static abstract class MonitorSwitchMenuHelper extends MonitorMenuHelper {

		private JCheckBoxMenuItem checkbox;

		protected MonitorSwitchMenuHelper(AmstradPcAction action) {
			this.checkbox = new JCheckBoxMenuItem(action);
			syncMenu(action.getAmstradPc());
			action.getAmstradPc().addMonitorListener(this);
		}

		protected abstract boolean getState(AmstradPc amstradPc);

		@Override
		protected void syncMenu(AmstradPc amstradPc) {
			getCheckbox().setSelected(getState(amstradPc));
		}

		protected JCheckBoxMenuItem getCheckbox() {
			return checkbox;
		}

	}

	private static class WindowAlwaysOnTopMenuHelper extends MonitorSwitchMenuHelper {

		private WindowAlwaysOnTopMenuHelper(AmstradPc amstradPc) {
			super(new WindowAlwaysOnTopAction(amstradPc));
		}

		public static void addToMenu(JMenu menu, AmstradPc amstradPc) {
			WindowAlwaysOnTopMenuHelper helper = new WindowAlwaysOnTopMenuHelper(amstradPc);
			menu.add(helper.getCheckbox());
		}

		@Override
		public void amstradPcWindowAlwaysOnTopChanged(AmstradPc amstradPc) {
			syncMenu(amstradPc);
		}

		@Override
		protected boolean getState(AmstradPc amstradPc) {
			return amstradPc.isAlwaysOnTop();
		}

	}

}