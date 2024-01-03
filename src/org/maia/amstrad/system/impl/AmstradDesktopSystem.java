package org.maia.amstrad.system.impl;

import java.io.File;

import org.maia.amstrad.AmstradException;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemScreen;
import org.maia.amstrad.system.AmstradSystemScreenSet;
import org.maia.amstrad.system.AmstradSystemSettings;
import org.maia.amstrad.system.impl.screen.AmstradSystemNativeScreen;
import org.maia.amstrad.system.impl.screen.AmstradSystemScreenSetImpl;
import org.maia.amstrad.system.impl.screen.AmstradSystemUnknownScreen;

public class AmstradDesktopSystem extends AmstradSystem {

	public static final String NAME = "DESKTOP";

	public AmstradDesktopSystem() {
	}

	@Override
	protected void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException {
		AmstradPcFrame frame = amstradPc.displayInFrame(false);
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
		return new DesktopSystemSettings();
	}

	@Override
	protected AmstradSystemScreenSet createScreenSet() {
		AmstradSystemScreenSetImpl screenSet = new AmstradSystemScreenSetImpl(this);
		screenSet.setNativeScreen(createNativeScreen());
		screenSet.setUnknownScreen(createUnknownScreen());
		return screenSet;
	}

	private AmstradSystemScreen createNativeScreen() {
		AmstradSystemNativeScreen screen = new AmstradSystemNativeScreen(this);
		screen.setPopupMenu(createPopupMenu());
		return screen;
	}

	private AmstradSystemScreen createUnknownScreen() {
		AmstradSystemUnknownScreen screen = new AmstradSystemUnknownScreen(this);
		screen.setPopupMenu(createPopupMenu());
		screen.setAutohideControlKeys(false);
		return screen;
	}

	@Override
	public String getName() {
		return NAME;
	}

	private static class DesktopSystemSettings implements AmstradSystemSettings {

		@Override
		public boolean isProgramBrowserCentric() {
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
		public boolean isTapeActivityShown() {
			return true;
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