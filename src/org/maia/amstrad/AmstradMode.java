package org.maia.amstrad;

import java.io.File;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.tape.AmstradTape;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramStoredInFile;

import jemu.settings.Settings;

public abstract class AmstradMode extends AmstradPcStateAdapter {

	public static final AmstradMode DEFAULT = new DefaultAmstradMode();

	public static final AmstradMode KIOSK = new KioskAmstradMode();

	public static final AmstradMode LEGACY = new LegacyAmstradMode();

	public static AmstradMode forName(String name) {
		if (DEFAULT.getName().equalsIgnoreCase(name)) {
			return DEFAULT;
		} else if (KIOSK.getName().equalsIgnoreCase(name)) {
			return KIOSK;
		} else if (LEGACY.getName().equalsIgnoreCase(name)) {
			return LEGACY;
		} else {
			return null;
		}
	}

	private String name;

	protected AmstradMode(String name) {
		this.name = name;
	}

	public final void launch(String[] args) throws Exception {
		System.out.println("Launching in " + getName() + " mode");
		overrideSettingsBeforeLaunch();
		doLaunch(args);
	}

	protected void overrideSettingsBeforeLaunch() {
		getUserSettings().setBool(Settings.FULLSCREEN, false); // implementations can toggle to fullscreen at a later
																// point in time, but this is a safer setting to prevent
																// initial 'black screens'
		getUserSettings().setBool(Settings.SHOWMENU, isUsingOriginalJemuMenu());
	}

	protected abstract void doLaunch(String[] args) throws Exception;

	/**
	 * Tells whether the AmstradPc primary display is the central and start screen at launch
	 * 
	 * @return <code>true</code> iff the AmstradPc primary display is centric
	 */
	public final boolean isPrimaryDisplayCentric() {
		return !isProgramBrowserCentric();
	}

	/**
	 * Tells whether the program browser is the central and start screen at launch
	 * 
	 * @return <code>true</code> iff the program browser is centric
	 */
	public abstract boolean isProgramBrowserCentric();

	/**
	 * Tells whether fullscreen toggling is enabled
	 * 
	 * @return <code>true</code> iff fullscreen can be toggled
	 */
	public abstract boolean isFullscreenToggleEnabled();

	/**
	 * Tells whether the original Jemu menu is to be shown
	 * 
	 * @return <code>true</code> iff the original Jemu menu is to be shown
	 */
	public abstract boolean isUsingOriginalJemuMenu();

	/**
	 * Tells whether visual indications for tape activity are to be shown
	 * 
	 * @return <code>true</code> iff tape activity is to be shown
	 * 
	 * @see AmstradTape#isActive()
	 */
	public abstract boolean isTapeActivityShown();

	protected AmstradSettings getUserSettings() {
		return getAmstradContext().getUserSettings();
	}

	protected AmstradContext getAmstradContext() {
		return getAmstradFactory().getAmstradContext();
	}

	protected AmstradFactory getAmstradFactory() {
		return AmstradFactory.getInstance();
	}

	public String getName() {
		return name;
	}

	private static class DefaultAmstradMode extends AmstradMode {

		public DefaultAmstradMode() {
			super("DEFAULT");
		}

		@Override
		protected void doLaunch(String[] args) throws AmstradProgramException {
			AmstradPc amstradPc = getAmstradFactory().createAmstradPc();
			AmstradPcFrame frame = amstradPc.displayInFrame(true);
			frame.installMenuBar();
			frame.installPopupMenu(true);
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

	}

	private static class KioskAmstradMode extends AmstradMode {

		public KioskAmstradMode() {
			super("KIOSK");
		}

		@Override
		protected void doLaunch(String[] args) {
			AmstradPc amstradPc = getAmstradFactory().createAmstradPc();
			AmstradMonitor monitor = amstradPc.getMonitor();
			monitor.setMonitorMode(AmstradMonitorMode.COLOR);
			monitor.setWindowAlwaysOnTop(true);
			amstradPc.addStateListener(this);
			AmstradPcFrame frame = amstradPc.displayInFrame(true);
			frame.installPopupMenu(false);
			amstradPc.start();
		}

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			getAmstradContext().showProgramBrowser(amstradPc);
			amstradPc.getMonitor().makeWindowFullscreen();
		}

		@Override
		public boolean isProgramBrowserCentric() {
			return true;
		}

		@Override
		public boolean isFullscreenToggleEnabled() {
			return false; // always in fullscreen
		}

		@Override
		public boolean isUsingOriginalJemuMenu() {
			return false;
		}

		@Override
		public boolean isTapeActivityShown() {
			return false;
		}

	}

	private static class LegacyAmstradMode extends AmstradMode {

		public LegacyAmstradMode() {
			super("LEGACY");
		}

		@Override
		protected void doLaunch(String[] args) throws AmstradProgramException {
			AmstradPc amstradPc = getAmstradFactory().createAmstradPc();
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

	}

}