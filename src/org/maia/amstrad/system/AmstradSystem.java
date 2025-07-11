package org.maia.amstrad.system;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradException;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;
import org.maia.amstrad.program.browser.AmstradProgramBrowser;
import org.maia.amstrad.system.impl.logs.AmstradSystemJemuLogs;
import org.maia.amstrad.system.impl.screen.AmstradSystemScreenSetImpl;
import org.maia.amstrad.system.impl.terminate.AmstradSystemCoreTermination;

import jemu.settings.Settings;

public abstract class AmstradSystem {

	private AmstradPc amstradPc;

	private AmstradSystemSettings systemSettings;

	private AmstradSystemLogs systemLogs;

	private AmstradSystemScreenSet screenSet;

	private AmstradSystemScreen currentScreen;

	private boolean autoResumeOnReturnToNativeScreen;

	protected AmstradSystem() {
		this.systemLogs = createSystemLogs();
		this.amstradPc = createAmstradPc();
		this.systemSettings = createSystemSettings();
	}

	public void init() {
		setScreenSet(createScreenSet());
		setCurrentScreen(findCurrentScreen());
		// Subclasses may extend this method
	}

	public final void launch() throws AmstradException {
		launch(new String[0]);
	}

	public final void launch(String[] args) throws AmstradException {
		System.out.println("Launching Amstrad system " + getName());
		overrideUserSettingsBeforeLaunch();
		getAmstradPc().addStateListener(new AmstradPcPostStartupActionInvoker());
		getAmstradPc().getMonitor().addMonitorListener(new AmstradMonitorDisplaySourceTracker());
		doLaunch(getAmstradPc(), args);
	}

	public final void terminate() {
		System.out.println("Terminating Amstrad system");
		createTermination().terminate(this);
	}

	protected void overrideUserSettingsBeforeLaunch() {
		getUserSettings().setBool(Settings.FULLSCREEN, getSystemSettings().isLaunchInFullscreen());
		getUserSettings().setBool(Settings.SHOWMENU, getSystemSettings().isUsingOriginalJemuMenu());
	}

	protected abstract void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException;

	protected void doPostStartupActions(AmstradPc amstradPc) {
		notifyInitialScreen(getCurrentScreen());
		// Subclasses may extend this method
	}

	protected void notifyInitialScreen(AmstradSystemScreen currentScreen) {
		updateScreenMonitorVisibility(currentScreen);
		updateScreenMonitorResizability(currentScreen);
		updateScreenPopupMenu(null, currentScreen);
		// Subclasses may extend this method
	}

	protected void notifyScreenChange(AmstradSystemScreen previousScreen, AmstradSystemScreen currentScreen) {
		updateScreenMonitorVisibility(currentScreen);
		updateScreenMonitorResizability(currentScreen);
		updateScreenPopupMenu(previousScreen, currentScreen);
		handleAutoPauseResume(previousScreen, currentScreen);
		// Subclasses may extend this method
	}

	private void updateScreenMonitorVisibility(AmstradSystemScreen currentScreen) {
		boolean visible = currentScreen.isShowMonitor();
		getAmstradPc().getMonitor().setMonitorEffect(visible);
		getAmstradPc().getActions().getMonitorEffectAction().setEnabled(visible);
	}

	private void updateScreenMonitorResizability(AmstradSystemScreen currentScreen) {
		boolean resizable = currentScreen.isMonitorResizable();
		getAmstradPc().getActions().getMonitorFullscreenAction()
				.setEnabled(resizable && getSystemSettings().isFullscreenToggleEnabled());
		getAmstradPc().getActions().getMonitorSingleSizeAction().setEnabled(resizable);
		getAmstradPc().getActions().getMonitorDoubleSizeAction().setEnabled(resizable);
		getAmstradPc().getActions().getMonitorTripleSizeAction().setEnabled(resizable);
	}

	private void updateScreenPopupMenu(AmstradSystemScreen previousScreen, AmstradSystemScreen currentScreen) {
		if (previousScreen != null) {
			AmstradPopupMenu popupMenu = previousScreen.getPopupMenu();
			if (popupMenu != null && popupMenu.isPopupMenuInstalled()) {
				popupMenu.uninstall();
			}
		}
		if (currentScreen != null) {
			AmstradPopupMenu popupMenu = currentScreen.getPopupMenu();
			if (popupMenu != null) {
				popupMenu.install();
			}
		}
	}

	private void handleAutoPauseResume(AmstradSystemScreen previousScreen, AmstradSystemScreen currentScreen) {
		if (currentScreen.isNativeScreen()) {
			if (autoResumeOnReturnToNativeScreen && getAmstradPc().isPaused()) {
				System.out.println("Auto resume");
				getAmstradPc().resume();
			}
			autoResumeOnReturnToNativeScreen = false;
		} else if (!autoResumeOnReturnToNativeScreen) {
			AmstradAlternativeDisplaySource ds = getAmstradPc().getMonitor().getCurrentAlternativeDisplaySource();
			autoResumeOnReturnToNativeScreen = ds != null && ds.isAutoPauseResume() && !getAmstradPc().isPaused();
			if (autoResumeOnReturnToNativeScreen) {
				System.out.println("Auto pause");
				getAmstradPc().pause();
			}
		}
	}

	protected AmstradPc createAmstradPc() {
		return AmstradFactory.getInstance().createAmstradPc();
	}

	protected AmstradSystemTermination createTermination() {
		return new AmstradSystemCoreTermination();
	}

	protected abstract AmstradSystemSettings createSystemSettings();

	protected AmstradSystemLogs createSystemLogs() {
		return new AmstradSystemJemuLogs();
	}

	protected AmstradSystemScreenSet createScreenSet() {
		return new AmstradSystemScreenSetImpl(this);
	}

	private AmstradSystemScreen findCurrentScreen() {
		AmstradSystemScreen screen = null;
		AmstradMonitor monitor = getAmstradPc().getMonitor();
		if (monitor != null) {
			if (monitor.isPrimaryDisplaySourceShowing()) {
				screen = getScreenSet().getNativeScreen();
			} else if (monitor.isAlternativeDisplaySourceShowing()) {
				screen = findMatchingCustomScreen(monitor.getCurrentAlternativeDisplaySource());
			}
		}
		if (screen == null) {
			screen = getScreenSet().getUnknownScreen();
		}
		return screen;
	}

	private AmstradSystemScreen findMatchingCustomScreen(AmstradAlternativeDisplaySource displaySource) {
		for (AmstradSystemScreen screen : getScreenSet().getCustomScreens()) {
			if (isMatchingCustomScreen(screen, displaySource)) {
				return screen;
			}
		}
		return null;
	}

	protected boolean isMatchingCustomScreen(AmstradSystemScreen customScreen,
			AmstradAlternativeDisplaySource displaySource) {
		return customScreen.getCustomDisplaySourceType().equals(displaySource.getType());
	}

	public AmstradProgramBrowser getProgramBrowser() {
		return getAmstradContext().getProgramBrowser(getAmstradPc());
	}

	public AmstradSettings getUserSettings() {
		return getAmstradContext().getUserSettings();
	}

	public AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	public abstract String getName();

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public AmstradSystemSettings getSystemSettings() {
		return systemSettings;
	}

	public AmstradSystemLogs getSystemLogs() {
		return systemLogs;
	}

	public AmstradSystemScreenSet getScreenSet() {
		return screenSet;
	}

	private void setScreenSet(AmstradSystemScreenSet screenSet) {
		this.screenSet = screenSet;
	}

	public AmstradSystemScreen getCurrentScreen() {
		return currentScreen;
	}

	private void setCurrentScreen(AmstradSystemScreen screen) {
		this.currentScreen = screen;
	}

	private class AmstradPcPostStartupActionInvoker extends AmstradPcStateAdapter {

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			doPostStartupActions(amstradPc);
		}

	}

	private class AmstradMonitorDisplaySourceTracker extends AmstradMonitorAdapter {

		@Override
		public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
			AmstradSystemScreen previousScreen = getCurrentScreen();
			AmstradSystemScreen currentScreen = findCurrentScreen();
			setCurrentScreen(currentScreen);
			notifyScreenChange(previousScreen, currentScreen);
		}

	}

}