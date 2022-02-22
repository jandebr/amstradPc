package org.maia.amstrad.jemu;

import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import org.maia.amstrad.jemu.impl.AmstradContextImpl;
import org.maia.amstrad.jemu.impl.AmstradPcImpl;
import org.maia.amstrad.jemu.impl.AmstradSettingsImpl;
import org.maia.amstrad.jemu.menu.ScreenshotAction;
import org.maia.amstrad.jemu.menu.LoadBasicFileAction;
import org.maia.amstrad.jemu.menu.MonitorModeAction;
import org.maia.amstrad.jemu.menu.OpenSnapshotFileAction;
import org.maia.amstrad.jemu.menu.PauseResumeAction;
import org.maia.amstrad.jemu.menu.QuitAction;
import org.maia.amstrad.jemu.menu.RebootAction;
import org.maia.amstrad.jemu.menu.SaveSnapshotFileAction;

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
		return new AmstradPcImpl();
	}

	public JMenuBar createSimpleMenuBar(AmstradPc amstradPc) {
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu(amstradPc));
		menubar.add(createEmulatorMenu(amstradPc));
		menubar.add(createMonitorMenu(amstradPc));
		return menubar;
	}

	private JMenu createFileMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new LoadBasicFileAction(amstradPc)));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new OpenSnapshotFileAction(amstradPc)));
		menu.add(new JMenuItem(new SaveSnapshotFileAction(amstradPc)));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new QuitAction(amstradPc)));
		return menu;
	}

	private JMenu createEmulatorMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Emulator");
		menu.add(new JMenuItem(new PauseResumeAction(amstradPc)));
		menu.add(new JMenuItem(new RebootAction(amstradPc)));
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
		AmstradMonitorMode monitorMode = getAmstradContext().getUserSettings().getMonitorMode();
		for (Enumeration<AbstractButton> en = monitorGroup.getElements(); en.hasMoreElements();) {
			AbstractButton button = en.nextElement();
			if (((MonitorModeAction) button.getAction()).getMode().equals(monitorMode))
				button.setSelected(true);
		}
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new ScreenshotAction(amstradPc)));
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