package org.maia.amstrad.system.impl;

import java.io.File;

import org.maia.amstrad.AmstradException;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemSettings;

public class AmstradDesktopSystem extends AmstradSystem {

	public static final String NAME = "DESKTOP";

	public AmstradDesktopSystem() {
	}

	@Override
	protected void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException {
		AmstradPcFrame frame = amstradPc.displayInFrame(false);
		createMenuBar().install();
		createPopupMenu().install();
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