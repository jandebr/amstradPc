package org.maia.amstrad.system.impl;

import java.io.File;

import javax.swing.JSeparator;

import org.maia.amstrad.AmstradException;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.menu.AmstradMenuBar;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.menu.maker.AmstradMenuBarMaker;
import org.maia.amstrad.pc.menu.maker.AmstradMenuDefaultLookAndFeel;
import org.maia.amstrad.pc.menu.maker.AmstradPopupMenuMaker;
import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemScreen;
import org.maia.amstrad.system.AmstradSystemScreenSet;
import org.maia.amstrad.system.AmstradSystemSettings;
import org.maia.amstrad.system.impl.screen.AmstradSystemNativeScreen;
import org.maia.amstrad.system.impl.screen.AmstradSystemProgramBrowserScreen;
import org.maia.amstrad.system.impl.screen.AmstradSystemScreenSetImpl;
import org.maia.amstrad.system.impl.screen.AmstradSystemUnknownScreen;

public class AmstradDesktopSystem extends AmstradSystem {

	public static final String NAME = "DESKTOP";

	public static final AmstradSystemSettings SETTINGS = new DesktopSystemSettings();

	public AmstradDesktopSystem() {
	}

	@Override
	protected void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException {
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		createMenuBar().install();
		if (args.length == 0) {
			amstradPc.start();
		} else if (args.length == 1) {
			amstradPc.launch(new AmstradProgramStoredInFile(new File(args[0])));
		} else {
			System.err.println("Invalid startup arguments");
			System.exit(1);
		}
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
		screenSet.setUnknownScreen(createUnknownScreen());
		return screenSet;
	}

	private AmstradSystemScreen createNativeScreen() {
		AmstradSystemNativeScreen screen = new AmstradSystemNativeScreen(this);
		screen.setPopupMenu(createPopupMenu());
		screen.setShowControlKeys(false);
		return screen;
	}

	private AmstradSystemScreen createProgramBrowserScreen() {
		AmstradSystemProgramBrowserScreen screen = new AmstradSystemProgramBrowserScreen(this);
		screen.setPopupMenu(createPopupMenu());
		screen.setShowControlKeys(false);
		return screen;
	}

	private AmstradSystemScreen createUnknownScreen() {
		AmstradSystemUnknownScreen screen = new AmstradSystemUnknownScreen(this);
		screen.setPopupMenu(createPopupMenu());
		screen.setShowControlKeys(false);
		return screen;
	}

	private AmstradMenuBar createMenuBar() {
		return new DesktopMenuBarMaker().createMenuBar();
	}

	private AmstradPopupMenu createPopupMenu() {
		return new DesktopPopupMenuMaker().createPopupMenu();
	}

	@Override
	public String getName() {
		return NAME;
	}

	private class DesktopMenuBarMaker extends AmstradMenuBarMaker {

		public DesktopMenuBarMaker() {
			super(AmstradDesktopSystem.this.getAmstradPc(), new AmstradMenuDefaultLookAndFeel());
		}

		@Override
		protected AmstradMenuBar doCreateMenu() {
			AmstradMenuBar menuBar = new AmstradMenuBar(getAmstradPc());
			menuBar.add(createFileMenu());
			menuBar.add(createEmulatorMenu());
			menuBar.add(createMonitorMenu());
			menuBar.add(createWindowMenu());
			return updateMenuBarLookAndFeel(menuBar);
		}

	}

	private class DesktopPopupMenuMaker extends AmstradPopupMenuMaker {

		public DesktopPopupMenuMaker() {
			super(AmstradDesktopSystem.this.getAmstradPc(), new AmstradMenuDefaultLookAndFeel());
		}

		@Override
		protected AmstradPopupMenu doCreateMenu() {
			AmstradPopupMenu popupMenu = new AmstradPopupMenu(getAmstradPc());
			popupMenu.add(createProgramBrowserMenuItem());
			popupMenu.add(createProgramBrowserSetupMenuItem());
			popupMenu.add(createProgramBrowserResetMenuItem());
			popupMenu.add(createProgramInfoMenuItem());
			popupMenu.add(new JSeparator());
			popupMenu.add(createAudioMenuItem());
			popupMenu.add(createVirtualKeyboardMenuItem());
			popupMenu.add(createJoystickMenu());
			popupMenu.add(createPauseResumeMenuItem());
			popupMenu.add(new JSeparator());
			popupMenu.add(createScreenshotMenuItem());
			popupMenu.add(createScreenshotWithMonitorEffectMenuItem());
			popupMenu.add(createMonitorModeMenu());
			popupMenu.add(createMonitorEffectsMenu());
			popupMenu.add(createMonitorSizeMenu());
			popupMenu.add(createMonitorFullscreenMenuItem());
			popupMenu.add(new JSeparator());
			popupMenu.add(createPowerOffMenuItem());
			return updatePopupMenuLookAndFeel(popupMenu);
		}

	}

	private static class DesktopSystemSettings implements AmstradSystemSettings {

		public DesktopSystemSettings() {
		}

		@Override
		public boolean isProgramCentric() {
			return false;
		}

		@Override
		public boolean isLaunchInFullscreen() {
			return false;
		}

		@Override
		public boolean isFullscreenToggleEnabled() {
			return true;
		}

		@Override
		public boolean isUsingOriginalJemuMenu() {
			return false;
		}

		@Override
		public boolean isProgramSourceCodeAccessible() {
			return true;
		}

		@Override
		public boolean isProgramAuthoringToolsAvailable() {
			return true;
		}

	}

}