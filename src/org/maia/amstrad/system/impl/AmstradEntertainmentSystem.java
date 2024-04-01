package org.maia.amstrad.system.impl;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import org.maia.amstrad.AmstradException;
import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.menu.maker.AmstradMenuEmulatedLookAndFeel;
import org.maia.amstrad.pc.menu.maker.AmstradPopupMenuMaker;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemScreen;
import org.maia.amstrad.system.AmstradSystemScreenSet;
import org.maia.amstrad.system.AmstradSystemSettings;
import org.maia.amstrad.system.AmstradSystemTermination;
import org.maia.amstrad.system.impl.screen.AmstradSystemCustomScreen;
import org.maia.amstrad.system.impl.screen.AmstradSystemNativeScreen;
import org.maia.amstrad.system.impl.screen.AmstradSystemScreenSetImpl;
import org.maia.amstrad.system.impl.terminate.AmstradSystemAnimatedTermination;

public class AmstradEntertainmentSystem extends AmstradSystem {

	public static final String NAME = "ENTERTAINMENT";

	public static final AmstradSystemSettings SETTINGS = new EntertainmentSystemSettings();

	private static final String PROGRAM_BROWSER_SCREEN_ID = "PROGRAM_BROWSER";

	public AmstradEntertainmentSystem() {
	}

	@Override
	protected void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException {
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		amstradPc.start();
	}

	@Override
	protected void doPostStartupActions(AmstradPc amstradPc) {
		super.doPostStartupActions(amstradPc);
		AmstradMonitor monitor = amstradPc.getMonitor();
		monitor.setMode(AmstradMonitorMode.COLOR);
		monitor.setWindowAlwaysOnTop(false); // Keep system windows accessible (e.g. Bluetooth manager) since fullscreen
		getAmstradContext().showProgramBrowser(amstradPc);
	}

	@Override
	protected AmstradSystemTermination createTermination() {
		return new AmstradSystemAnimatedTermination();
	}

	@Override
	protected AmstradSystemSettings createSystemSettings() {
		return SETTINGS;
	}

	@Override
	protected AmstradSystemScreenSet createScreenSet() {
		AmstradSystemScreenSetImpl screenSet = new AmstradSystemScreenSetImpl(this);
		screenSet.setNativeScreen(createNativeScreen());
		screenSet.addCustomScreen(createProgramBrowserScreen());
		return screenSet;
	}

	private AmstradSystemScreen createNativeScreen() {
		AmstradSystemNativeScreen screen = new AmstradSystemNativeScreen(this);
		screen.setPopupMenu(new ProgramPopupMenuMaker().createPopupMenu());
		screen.setShowTurbo(false);
		screen.setShowControlKeys(true); // with dynamic auto-hide
		screen.setShowTapeActivity(false);
		return screen;
	}

	private AmstradSystemScreen createProgramBrowserScreen() {
		ProgramBrowserDisplaySource displaySource = getAmstradPc().getActions().getProgramBrowserAction()
				.getDisplaySource();
		AmstradSystemCustomScreen screen = new AmstradSystemCustomScreen(PROGRAM_BROWSER_SCREEN_ID, this,
				displaySource);
		screen.setPopupMenu(new ProgramBrowserPopupMenuMaker().createPopupMenu());
		screen.setShowPause(false);
		screen.setShowTurbo(false);
		screen.setShowControlKeys(true);
		screen.setAutohideControlKeys(false);
		return screen;
	}

	@Override
	public String getName() {
		return NAME;
	}

	private abstract class EntertainmentPopupMenuMaker extends AmstradPopupMenuMaker {

		protected EntertainmentPopupMenuMaker() {
			super(AmstradEntertainmentSystem.this.getAmstradPc(), new AmstradMenuEmulatedLookAndFeel(
					AmstradEntertainmentSystem.this.getAmstradPc().getMonitor().getGraphicsContext()));
		}

	}

	private class ProgramPopupMenuMaker extends EntertainmentPopupMenuMaker {

		@Override
		protected AmstradPopupMenu doCreateMenu() {
			AmstradPopupMenu popupMenu = new AmstradPopupMenu(getAmstradPc());
			popupMenu.add(createPauseResumeMenuItem());
			popupMenu.add(createProgramInfoMenuItem());
			popupMenu.add(createVirtualKeyboardMenuItem());
			popupMenu.add(createSettingsMenu());
			popupMenu.add(new JSeparator());
			popupMenu.add(createProgramBrowserMenuItem());
			return updatePopupMenuLookAndFeel(popupMenu);
		}

		protected JMenu createSettingsMenu() {
			JMenu menu = new JMenu("Settings");
			menu.add(createAudioMenuItem());
			menu.add(createJoystickMenu());
			menu.add(createMonitorModeMenu());
			menu.add(createMonitorEffectsMenu());
			return updateMenuLookAndFeel(menu, UIResources.settingsIcon);
		}

		protected JMenuItem createProgramBrowserMenuItem() {
			getAmstradPc().getActions().getProgramBrowserAction().setNameToOpen("Quit program");
			return updateMenuItemLookAndFeel(super.createProgramBrowserMenuItem(), UIResources.quitIcon);
		}

	}

	private class ProgramBrowserPopupMenuMaker extends EntertainmentPopupMenuMaker {

		@Override
		protected AmstradPopupMenu doCreateMenu() {
			AmstradPopupMenu popupMenu = new AmstradPopupMenu(getAmstradPc());
			popupMenu.add(createAudioMenuItem());
			popupMenu.add(createJoystickMenu());
			popupMenu.add(createMonitorEffectsMenu());
			popupMenu.add(new JSeparator());
			popupMenu.add(createPowerOffMenuItem());
			return updatePopupMenuLookAndFeel(popupMenu);
		}

	}

	private static class EntertainmentSystemSettings implements AmstradSystemSettings {

		public EntertainmentSystemSettings() {
		}

		@Override
		public boolean isProgramCentric() {
			return true;
		}

		@Override
		public boolean isLaunchInFullscreen() {
			return true;
		}

		@Override
		public boolean isFullscreenToggleEnabled() {
			return false;
		}

		@Override
		public boolean isUsingOriginalJemuMenu() {
			return false;
		}

		@Override
		public boolean isProgramSourceCodeAccessible() {
			return false;
		}

		@Override
		public boolean isProgramAuthoringToolsAvailable() {
			return false;
		}

	}

}