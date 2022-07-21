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
import org.maia.amstrad.pc.menu.AlternativeDisplaySourceAction;
import org.maia.amstrad.pc.menu.AutoTypeFileAction;
import org.maia.amstrad.pc.menu.FullscreenAction;
import org.maia.amstrad.pc.menu.LoadBasicBinaryFileAction;
import org.maia.amstrad.pc.menu.LoadBasicSourceFileAction;
import org.maia.amstrad.pc.menu.LoadSnapshotFileAction;
import org.maia.amstrad.pc.menu.MonitorModeAction;
import org.maia.amstrad.pc.menu.PauseResumeAction;
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

	public JMenuBar createSimpleMenuBar(AmstradPc amstradPc) {
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
		JRadioButtonMenuItem monitor1 = new JRadioButtonMenuItem(new MonitorModeAction(AmstradMonitorMode.COLOR,
				amstradPc, "Color monitor"));
		JRadioButtonMenuItem monitor2 = new JRadioButtonMenuItem(new MonitorModeAction(AmstradMonitorMode.GREEN,
				amstradPc, "Green monitor"));
		JRadioButtonMenuItem monitor3 = new JRadioButtonMenuItem(new MonitorModeAction(AmstradMonitorMode.GRAY,
				amstradPc, "Gray monitor"));
		menu.add(monitor1);
		menu.add(monitor2);
		menu.add(monitor3);
		ButtonGroup monitorGroup = new ButtonGroup();
		monitorGroup.add(monitor1);
		monitorGroup.add(monitor2);
		monitorGroup.add(monitor3);
		AmstradMonitorMode monitorMode = amstradPc.getMonitorMode();
		for (Enumeration<AbstractButton> en = monitorGroup.getElements(); en.hasMoreElements();) {
			AbstractButton button = en.nextElement();
			if (((MonitorModeAction) button.getAction()).getMode().equals(monitorMode))
				button.setSelected(true);
		}
		return menu;
	}

	private JMenu createWindowMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Window");
		// Update title
		JCheckBoxMenuItem checkItem = new JCheckBoxMenuItem(new UpdateWindowTitleAction(amstradPc));
		checkItem.setState(Settings.getBoolean(Settings.UPDATETITLE, true));
		menu.add(checkItem);
		// Always on top
		checkItem = new JCheckBoxMenuItem(new WindowAlwaysOnTopAction(amstradPc));
		checkItem.setState(Settings.getBoolean(Settings.ONTOP, false));
		menu.add(checkItem);
		// Alternative display
		checkItem = new JCheckBoxMenuItem(new AlternativeDisplaySourceAction(amstradPc));
		checkItem.setState(false);
		menu.add(checkItem);
		// Fullscreen
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(new FullscreenAction(amstradPc));
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

}