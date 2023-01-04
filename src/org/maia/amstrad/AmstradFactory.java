package org.maia.amstrad;

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

import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicClsAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicListAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicNewAction;
import org.maia.amstrad.basic.locomotive.action.LocomotiveBasicRunAction;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
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
import org.maia.amstrad.pc.impl.jemu.JemuAmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.browser.action.ProgramBrowserAction;
import org.maia.amstrad.program.browser.action.ProgramBrowserSetupAction;
import org.maia.amstrad.program.browser.action.ProgramInfoAction;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.facet.FacetedAmstradProgramRepository;
import org.maia.amstrad.program.repo.file.BasicProgramFileRepository;
import org.maia.amstrad.program.repo.rename.RenamingAmstradProgramRepository;
import org.maia.amstrad.program.repo.search.SearchingAmstradProgramRepository;

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
		menubar.add(createMonitorMenu(amstradPc));
		menubar.add(createWindowMenu(amstradPc));
		return menubar;
	}

	private JMenu createFileMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("File");
		ProgramBrowserAction browserAction = new ProgramBrowserAction(amstradPc);
		JMenuItem item = new JMenuItem(browserAction);
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		item = new JMenuItem(new ProgramBrowserSetupAction(browserAction));
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		item = new JMenuItem(new ProgramInfoAction(browserAction));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menu.add(item);
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new LoadBasicSourceFileAction(amstradPc)));
		menu.add(new JMenuItem(new LoadBasicBinaryFileAction(amstradPc)));
		menu.add(new JMenuItem(new LoadSnapshotFileAction(amstradPc)));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(new SaveBasicSourceFileAction(amstradPc)));
		menu.add(new JMenuItem(new SaveBasicBinaryFileAction(amstradPc)));
		menu.add(new JMenuItem(new SaveSnapshotFileAction(amstradPc)));
		menu.add(new JSeparator());
		item = new JMenuItem(new QuitAction(amstradPc));
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		return menu;
	}

	private JMenu createEmulatorMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Emulator");
		menu.add(new JMenuItem(new AutoTypeFileAction(amstradPc)));
		menu.add(createBasicMenu(amstradPc));
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(new PauseResumeAction(amstradPc));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0));
		menu.add(item);
		item = new JMenuItem(new RebootAction(amstradPc));
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		return menu;
	}

	private JMenu createBasicMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Basic");
		menu.add(new JMenuItem(new LocomotiveBasicNewAction(amstradPc)));
		menu.add(new JMenuItem(new LocomotiveBasicRunAction(amstradPc)));
		menu.add(new JMenuItem(new LocomotiveBasicListAction(amstradPc)));
		menu.add(new JMenuItem(new LocomotiveBasicClsAction(amstradPc)));
		return menu;
	}

	private JMenu createMonitorMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Monitor");
		menu.add(new JMenuItem(new DisplaySystemColorsAction(amstradPc)));
		menu.add(createMonitorModeMenu(amstradPc));
		menu.add(createMonitorEffectsMenu(amstradPc));
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(new ScreenshotAction(amstradPc));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		item = new JMenuItem(new ScreenshotWithMonitorEffectAction(amstradPc));
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		return menu;
	}

	private JMenu createMonitorModeMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Monitor type");
		MonitorModeMenuHelper.addModesToMenu(menu, amstradPc);
		return menu;
	}

	private JMenu createMonitorEffectsMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Effects");
		MonitorEffectMenuHelper.addToMenu(menu, amstradPc);
		MonitorScanLinesEffectMenuHelper.addToMenu(menu, amstradPc);
		MonitorBilinearEffectMenuHelper.addToMenu(menu, amstradPc);
		return menu;
	}

	private JMenu createWindowMenu(AmstradPc amstradPc) {
		JMenu menu = new JMenu("Window");
		WindowTitleAutoUpdateMenuHelper.addToMenu(menu, amstradPc);
		WindowAlwaysOnTopMenuHelper.addToMenu(menu, amstradPc);
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(new MonitorFullscreenAction(amstradPc));
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		menu.add(item);
		return menu;
	}

	public AmstradProgramRepository createProgramRepository() {
		AmstradProgramRepositoryConfiguration config = getAmstradContext().getProgramRepositoryConfiguration();
		AmstradProgramRepository repository = new BasicProgramFileRepository(config.getRootFolder());
		if (config.isHideSequenceNumbers()) {
			repository = RenamingAmstradProgramRepository.withSequenceNumbersHidden(repository);
		}
		if (config.isSearchByProgramName()) {
			repository = SearchingAmstradProgramRepository.withSearchByProgramName(repository,
					config.getSearchString());
		}
		if (config.isFaceted()) {
			repository = new FacetedAmstradProgramRepository(repository, config.getFacets());
		}
		return repository;
	}

	public ProgramBrowserDisplaySource createProgramRepositoryBrowser(AmstradPc amstradPc) {
		AmstradProgramRepository repository = createProgramRepository();
		return ProgramBrowserDisplaySource.createProgramRepositoryBrowser(amstradPc, repository);
	}

	public ProgramBrowserDisplaySource createProgramInfo(AmstradPc amstradPc, AmstradProgram program) {
		return ProgramBrowserDisplaySource.createProgramInfo(amstradPc, program);
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

	private static abstract class MonitorMenuHelper extends AmstradMonitorAdapter {

		protected MonitorMenuHelper() {
		}

		protected abstract void syncMenu(AmstradMonitor monitor);

	}

	private static class MonitorModeMenuHelper extends MonitorMenuHelper {

		private ButtonGroup buttonGroup;

		private MonitorModeMenuHelper(ButtonGroup buttonGroup) {
			this.buttonGroup = buttonGroup;
		}

		public static void addModesToMenu(JMenu menu, AmstradPc amstradPc) {
			JRadioButtonMenuItem colorMode = new JRadioButtonMenuItem(
					new MonitorModeAction(AmstradMonitorMode.COLOR, amstradPc, "Color monitor (CTM640)"));
			JRadioButtonMenuItem greenMode = new JRadioButtonMenuItem(
					new MonitorModeAction(AmstradMonitorMode.GREEN, amstradPc, "Green monitor (GT64)"));
			JRadioButtonMenuItem grayMode = new JRadioButtonMenuItem(
					new MonitorModeAction(AmstradMonitorMode.GRAY, amstradPc, "Gray monitor"));
			menu.add(colorMode);
			menu.add(greenMode);
			menu.add(grayMode);
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(colorMode);
			buttonGroup.add(greenMode);
			buttonGroup.add(grayMode);
			MonitorModeMenuHelper helper = new MonitorModeMenuHelper(buttonGroup);
			helper.syncMenu(amstradPc.getMonitor());
			amstradPc.getMonitor().addMonitorListener(helper);
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

		protected MonitorCheckboxMenuHelper(AmstradPcAction action) {
			this.checkbox = new JCheckBoxMenuItem(action);
			syncMenu(action.getAmstradPc().getMonitor());
			action.getAmstradPc().getMonitor().addMonitorListener(this);
		}

		@Override
		protected final void syncMenu(AmstradMonitor monitor) {
			getCheckbox().setSelected(getState(monitor));
		}

		protected abstract boolean getState(AmstradMonitor monitor);

		protected JCheckBoxMenuItem getCheckbox() {
			return checkbox;
		}

	}

	private static class MonitorEffectMenuHelper extends MonitorCheckboxMenuHelper {

		private MonitorEffectMenuHelper(AmstradPc amstradPc) {
			super(new MonitorEffectAction(amstradPc));
		}

		public static void addToMenu(JMenu menu, AmstradPc amstradPc) {
			MonitorEffectMenuHelper helper = new MonitorEffectMenuHelper(amstradPc);
			menu.add(helper.getCheckbox());
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

		private MonitorScanLinesEffectMenuHelper(AmstradPc amstradPc) {
			super(new MonitorScanLinesEffectAction(amstradPc));
		}

		public static void addToMenu(JMenu menu, AmstradPc amstradPc) {
			MonitorScanLinesEffectMenuHelper helper = new MonitorScanLinesEffectMenuHelper(amstradPc);
			menu.add(helper.getCheckbox());
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

		private MonitorBilinearEffectMenuHelper(AmstradPc amstradPc) {
			super(new MonitorBilinearEffectAction(amstradPc));
		}

		public static void addToMenu(JMenu menu, AmstradPc amstradPc) {
			MonitorBilinearEffectMenuHelper helper = new MonitorBilinearEffectMenuHelper(amstradPc);
			menu.add(helper.getCheckbox());
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

		private WindowTitleAutoUpdateMenuHelper(AmstradPc amstradPc) {
			super(new WindowDynamicTitleAction(amstradPc));
		}

		public static void addToMenu(JMenu menu, AmstradPc amstradPc) {
			WindowTitleAutoUpdateMenuHelper helper = new WindowTitleAutoUpdateMenuHelper(amstradPc);
			menu.add(helper.getCheckbox());
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

		private WindowAlwaysOnTopMenuHelper(AmstradPc amstradPc) {
			super(new WindowAlwaysOnTopAction(amstradPc));
		}

		public static void addToMenu(JMenu menu, AmstradPc amstradPc) {
			WindowAlwaysOnTopMenuHelper helper = new WindowAlwaysOnTopMenuHelper(amstradPc);
			menu.add(helper.getCheckbox());
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