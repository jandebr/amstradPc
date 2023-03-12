package org.maia.amstrad;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.gui.browser.action.ProgramBrowserAction;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.AmstradPcAction;
import org.maia.amstrad.pc.action.MonitorModeAction;
import org.maia.amstrad.pc.impl.jemu.JemuAmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradPcSnapshotFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramBuilder;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.config.AmstradProgramRepositoryConfiguration;
import org.maia.amstrad.program.repo.facet.FacetedAmstradProgramRepository;
import org.maia.amstrad.program.repo.file.BasicProgramFileRepository;
import org.maia.amstrad.program.repo.rename.RenamingAmstradProgramRepository;
import org.maia.amstrad.program.repo.search.SearchingAmstradProgramRepository;

public class AmstradFactory {

	private static AmstradFactory instance;

	private AmstradContext context;

	private Map<AmstradPc, AmstradActions> actionMap;

	private AmstradFactory() {
		this.actionMap = new HashMap<AmstradPc, AmstradActions>();
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
		AmstradActions actions = getActionsFor(amstradPc);
		JMenuBar menubar = new JMenuBar();
		menubar.add(createFileMenu(actions));
		menubar.add(createEmulatorMenu(actions));
		menubar.add(createMonitorMenu(actions));
		menubar.add(createWindowMenu(actions));
		return menubar;
	}

	public JPopupMenu createPopupMenu(AmstradPc amstradPc) {
		AmstradActions actions = getActionsFor(amstradPc);
		JPopupMenu popup = new JPopupMenu("Amstrad Menu");
		popup.add(createFileMenu(actions));
		popup.add(createEmulatorMenu(actions));
		popup.add(createMonitorMenu(actions));
		popup.add(createWindowMenu(actions));
		return popup;
	}

	private JMenu createFileMenu(AmstradActions actions) {
		JMenu menu = new JMenu("File");
		JMenuItem item = new JMenuItem(actions.getProgramBrowserAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		item = new JMenuItem(actions.getProgramBrowserSetupAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		item = new JMenuItem(actions.getProgramInfoAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		menu.add(item);
		menu.add(new JSeparator());
		menu.add(new JMenuItem(actions.getLoadBasicSourceFileAction()));
		menu.add(new JMenuItem(actions.getLoadBasicBinaryFileAction()));
		menu.add(new JMenuItem(actions.getLoadSnapshotFileAction()));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(actions.getSaveBasicSourceFileAction()));
		menu.add(new JMenuItem(actions.getSaveBasicBinaryFileAction()));
		menu.add(new JMenuItem(actions.getSaveSnapshotFileAction()));
		menu.add(new JSeparator());
		item = new JMenuItem(actions.getQuitAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		return menu;
	}

	private JMenu createEmulatorMenu(AmstradActions actions) {
		JMenu menu = new JMenu("Emulator");
		menu.add(new JMenuItem(actions.getAutoTypeFileAction()));
		menu.add(createBasicMenu(actions));
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(actions.getPauseResumeAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PAUSE, 0));
		menu.add(item);
		item = new JMenuItem(actions.getRebootAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		return menu;
	}

	private JMenu createBasicMenu(AmstradActions actions) {
		JMenu menu = new JMenu("Basic");
		menu.add(new JMenuItem(actions.getLocomotiveBasicBreakEscapeAction()));
		menu.add(new JSeparator());
		menu.add(new JMenuItem(actions.getLocomotiveBasicNewAction()));
		menu.add(new JMenuItem(actions.getLocomotiveBasicRunAction()));
		menu.add(new JMenuItem(actions.getLocomotiveBasicListAction()));
		menu.add(new JMenuItem(actions.getLocomotiveBasicClsAction()));
		menu.add(new JMenuItem(actions.getLocomotiveBasicClearAction()));
		return menu;
	}

	private JMenu createMonitorMenu(AmstradActions actions) {
		JMenu menu = new JMenu("Monitor");
		menu.add(new JMenuItem(actions.getDisplaySystemColorsAction()));
		menu.add(createMonitorModeMenu(actions));
		menu.add(createMonitorEffectsMenu(actions));
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(actions.getScreenshotAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		item = new JMenuItem(actions.getScreenshotWithMonitorEffectAction());
		item.setAccelerator(
				KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		menu.add(item);
		return menu;
	}

	private JMenu createMonitorModeMenu(AmstradActions actions) {
		JMenu menu = new JMenu("Monitor type");
		MonitorModeMenuHelper.addModesToMenu(menu, actions);
		return menu;
	}

	private JMenu createMonitorEffectsMenu(AmstradActions actions) {
		JMenu menu = new JMenu("Effects");
		MonitorEffectMenuHelper.addToMenu(menu, actions);
		MonitorScanLinesEffectMenuHelper.addToMenu(menu, actions);
		MonitorBilinearEffectMenuHelper.addToMenu(menu, actions);
		return menu;
	}

	private JMenu createWindowMenu(AmstradActions actions) {
		JMenu menu = new JMenu("Window");
		WindowTitleAutoUpdateMenuHelper.addToMenu(menu, actions);
		WindowAlwaysOnTopMenuHelper.addToMenu(menu, actions);
		menu.add(new JSeparator());
		JMenuItem item = new JMenuItem(actions.getMonitorFullscreenAction());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
		menu.add(item);
		// Prevent user to change window options when in kiosk mode
		if (getAmstradContext().isKioskMode()) {
			menu.setEnabled(false);
		}
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

	public AmstradPcSnapshotFile createCpcSnapshotProgram(File snapshotFile) {
		return createCpcSnapshotProgram(snapshotFile.getName(), snapshotFile);
	}

	public AmstradPcSnapshotFile createCpcSnapshotProgram(String programName, File snapshotFile) {
		return new AmstradPcSnapshotFile(programName, snapshotFile);
	}

	public AmstradProgram createBasicProgram(File basicFile) {
		return createBasicProgram(basicFile.getName(), basicFile);
	}

	public AmstradProgram createBasicProgram(String programName, File basicFile) {
		return new AmstradBasicProgramFile(programName, basicFile);
	}

	public AmstradProgram createBasicDescribedProgram(File basicFile, File metadataFile)
			throws AmstradProgramException {
		return createBasicDescribedProgram(basicFile.getName(), basicFile, metadataFile);
	}

	public AmstradProgram createBasicDescribedProgram(String programName, File basicFile, File metadataFile)
			throws AmstradProgramException {
		AmstradProgram program = createBasicProgram(programName, basicFile);
		AmstradProgramBuilder builder = AmstradProgramBuilder.createFor(program);
		try {
			builder.loadAmstradMetaData(metadataFile);
		} catch (IOException e) {
			throw new AmstradProgramException(program, e);
		}
		return builder.build();
	}

	private AmstradActions getActionsFor(AmstradPc amstradPc) {
		AmstradActions actions = getActionMap().get(amstradPc);
		if (actions == null) {
			actions = new AmstradActions(amstradPc);
			getActionMap().put(amstradPc, actions);
		}
		return actions;
	}

	private Map<AmstradPc, AmstradActions> getActionMap() {
		return actionMap;
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

	private class AmstradContextImpl extends AmstradContext {

		private AmstradSettings userSettings;

		private PrintStream consoleOutputStream;

		private PrintStream consoleErrorStream;

		private Map<AmstradPc, Boolean> basicProtectiveModes;

		public AmstradContextImpl(AmstradSettings userSettings, PrintStream consoleOutputStream,
				PrintStream consoleErrorStream) {
			this.userSettings = userSettings;
			this.consoleOutputStream = consoleOutputStream;
			this.consoleErrorStream = consoleErrorStream;
			this.basicProtectiveModes = new HashMap<AmstradPc, Boolean>();
		}

		@Override
		public AmstradSettings getUserSettings() {
			return userSettings;
		}

		@Override
		public PrintStream getConsoleOutputStream() {
			return consoleOutputStream;
		}

		@Override
		public PrintStream getConsoleErrorStream() {
			return consoleErrorStream;
		}

		@Override
		public void showProgramBrowser(AmstradPc amstradPc) {
			ProgramBrowserAction browserAction = getActionsFor(amstradPc).getProgramBrowserAction();
			if (browserAction != null && !browserAction.isProgramBrowserShowing()) {
				browserAction.showProgramBrowser();
			}
		}

		@Override
		public boolean isBasicProtectiveMode(AmstradPc amstradPc) {
			Boolean protective = basicProtectiveModes.get(amstradPc);
			return protective != null && protective.booleanValue();
		}

		@Override
		public void setBasicProtectiveMode(AmstradPc amstradPc, boolean protective) {
			basicProtectiveModes.put(amstradPc, Boolean.valueOf(protective));
			AmstradActions actions = getActionsFor(amstradPc);
			actions.getSaveBasicSourceFileAction().setEnabled(!protective);
			actions.getSaveBasicBinaryFileAction().setEnabled(!protective);
			actions.getSaveSnapshotFileAction().setEnabled(!protective);
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

		public static void addModesToMenu(JMenu menu, AmstradActions actions) {
			JRadioButtonMenuItem colorMode = new JRadioButtonMenuItem(actions.getMonitorModeColorAction());
			JRadioButtonMenuItem greenMode = new JRadioButtonMenuItem(actions.getMonitorModeGreenAction());
			JRadioButtonMenuItem grayMode = new JRadioButtonMenuItem(actions.getMonitorModeGrayAction());
			menu.add(colorMode);
			menu.add(greenMode);
			menu.add(grayMode);
			ButtonGroup buttonGroup = new ButtonGroup();
			buttonGroup.add(colorMode);
			buttonGroup.add(greenMode);
			buttonGroup.add(grayMode);
			MonitorModeMenuHelper helper = new MonitorModeMenuHelper(buttonGroup);
			helper.syncMenu(actions.getAmstradPc().getMonitor());
			actions.getAmstradPc().getMonitor().addMonitorListener(helper);
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

		private MonitorEffectMenuHelper(AmstradPcAction action) {
			super(action);
		}

		public static void addToMenu(JMenu menu, AmstradActions actions) {
			MonitorEffectMenuHelper helper = new MonitorEffectMenuHelper(actions.getMonitorEffectAction());
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

		private MonitorScanLinesEffectMenuHelper(AmstradPcAction action) {
			super(action);
		}

		public static void addToMenu(JMenu menu, AmstradActions actions) {
			MonitorScanLinesEffectMenuHelper helper = new MonitorScanLinesEffectMenuHelper(
					actions.getMonitorScanLinesEffectAction());
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

		private MonitorBilinearEffectMenuHelper(AmstradPcAction action) {
			super(action);
		}

		public static void addToMenu(JMenu menu, AmstradActions actions) {
			MonitorBilinearEffectMenuHelper helper = new MonitorBilinearEffectMenuHelper(
					actions.getMonitorBilinearEffectAction());
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

		private WindowTitleAutoUpdateMenuHelper(AmstradPcAction action) {
			super(action);
		}

		public static void addToMenu(JMenu menu, AmstradActions actions) {
			WindowTitleAutoUpdateMenuHelper helper = new WindowTitleAutoUpdateMenuHelper(
					actions.getWindowDynamicTitleAction());
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

		private WindowAlwaysOnTopMenuHelper(AmstradPcAction action) {
			super(action);
		}

		public static void addToMenu(JMenu menu, AmstradActions actions) {
			WindowAlwaysOnTopMenuHelper helper = new WindowAlwaysOnTopMenuHelper(actions.getWindowAlwaysOnTopAction());
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