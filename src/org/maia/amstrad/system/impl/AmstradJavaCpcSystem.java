package org.maia.amstrad.system.impl;

import java.io.File;

import org.maia.amstrad.AmstradException;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradProgramStoredInFile;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemSettings;

import jemu.settings.Settings;

public class AmstradJavaCpcSystem extends AmstradSystem {

	public static final String NAME = "JAVACPC";

	public AmstradJavaCpcSystem() {
	}

	@Override
	protected void overrideUserSettingsBeforeLaunch() {
		super.overrideUserSettingsBeforeLaunch();
		// Starting windowed to prevent initial 'black screens' with JEMU
		getUserSettings().setBool(Settings.FULLSCREEN, false);
	}

	@Override
	protected void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException {
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
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
	protected AmstradPc createAmstradPc() {
		return AmstradFactory.getInstance().createJemuClassicAmstradPc();
	}

	@Override
	protected AmstradSystemSettings createSystemSettings() {
		return new JavaCpcSystemSettings();
	}

	@Override
	public String getName() {
		return NAME;
	}

	private static class JavaCpcSystemSettings implements AmstradSystemSettings {

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
			return true;
		}

		@Override
		public boolean isTapeActivityShown() {
			return false;
		}

		@Override
		public boolean isProgramSourceCodeAccessible() {
			return true;
		}

		@Override
		public boolean isProgramAuthoringToolsAvailable() {
			return false;
		}

	}

}