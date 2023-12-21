package org.maia.amstrad;

import java.io.File;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.pc.frame.AmstradPcFrame;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.tape.AmstradTape;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramStoredInFile;

import jemu.settings.Settings;

public abstract class AmstradMode extends AmstradPcStateAdapter {

	public static final AmstradMode DESKTOP = new DesktopAmstradMode();

	public static final AmstradMode DISCOVER = new DiscoverAmstradMode();

	public static final AmstradMode POWERON = new PowerOnAmstradMode();

	public static final AmstradMode GT65POWERON = new GT65PowerOnAmstradMode();

	public static final AmstradMode CLASSIC = new ClassicAmstradMode();

	public static final AmstradMode DEFAULT_MODE = DESKTOP;

	public static AmstradMode forName(String name) {
		if (DESKTOP.getName().equalsIgnoreCase(name)) {
			return DESKTOP;
		} else if (DISCOVER.getName().equalsIgnoreCase(name)) {
			return DISCOVER;
		} else if (POWERON.getName().equalsIgnoreCase(name)) {
			return POWERON;
		} else if (GT65POWERON.getName().equalsIgnoreCase(name)) {
			return GT65POWERON;
		} else if (CLASSIC.getName().equalsIgnoreCase(name)) {
			return CLASSIC;
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
																// initial 'black screens' with JEMU
		getUserSettings().setBool(Settings.SHOWMENU, isUsingOriginalJemuMenu());
		getUserSettings().setBool(Settings.TERMINATE_ANIMATE, isAnimateOnTerminate());
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

	/**
	 * Tells whether an animation is to be shown when quitting
	 * <p>
	 * The animation also provides a delay allowing the configured <em>system command</em> to be cancelled
	 * </p>
	 * 
	 * @return <code>true</code> iff an animation is to be shown
	 */
	public abstract boolean isAnimateOnTerminate();

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

	private static class DesktopAmstradMode extends AmstradMode {

		public DesktopAmstradMode() {
			super("DESKTOP");
		}

		@Override
		protected void doLaunch(String[] args) throws AmstradProgramException {
			AmstradPc amstradPc = getAmstradFactory().createAmstradPc();
			AmstradPcFrame frame = amstradPc.displayInFrame(true);
			frame.installAndEnableMenuBar();
			frame.installAndEnablePopupMenu(true);
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

		@Override
		public boolean isAnimateOnTerminate() {
			return false;
		}

	}

	private static abstract class ImmersiveAmstradMode extends AmstradMode {

		protected ImmersiveAmstradMode(String name) {
			super(name);
		}

		@Override
		protected void doLaunch(String[] args) {
			AmstradPc amstradPc = getAmstradFactory().createAmstradPc();
			AmstradMonitor monitor = amstradPc.getMonitor();
			monitor.setMode(getMonitorModeAtLaunch());
			monitor.setWindowAlwaysOnTop(true);
			AmstradPcFrame frame = amstradPc.displayInFrame(true);
			frame.installAndEnablePopupMenu(false);
			amstradPc.addStateListener(this);
			amstradPc.start();
		}

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			amstradPc.getMonitor().makeFullscreen();
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

		@Override
		public boolean isAnimateOnTerminate() {
			return true;
		}

		protected AmstradMonitorMode getMonitorModeAtLaunch() {
			return AmstradMonitorMode.COLOR;
		}

	}

	private static class DiscoverAmstradMode extends ImmersiveAmstradMode {

		public DiscoverAmstradMode() {
			super("DISCOVER");
		}

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			getAmstradContext().showProgramBrowser(amstradPc);
			super.amstradPcStarted(amstradPc);
		}

		@Override
		public boolean isProgramBrowserCentric() {
			return true;
		}

	}

	private static class PowerOnAmstradMode extends ImmersiveAmstradMode {

		public PowerOnAmstradMode() {
			this("POWERON");
		}

		protected PowerOnAmstradMode(String name) {
			super(name);
		}

		@Override
		public boolean isProgramBrowserCentric() {
			return false;
		}

	}

	private static class GT65PowerOnAmstradMode extends PowerOnAmstradMode {

		public GT65PowerOnAmstradMode() {
			super("GT65POWERON");
		}

		@Override
		protected AmstradMonitorMode getMonitorModeAtLaunch() {
			return AmstradMonitorMode.GREEN;
		}

	}

	private static class ClassicAmstradMode extends AmstradMode {

		public ClassicAmstradMode() {
			super("CLASSIC");
		}

		@Override
		protected void doLaunch(String[] args) throws AmstradProgramException {
			AmstradPc amstradPc = getAmstradFactory().createJemuClassicAmstradPc();
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

		@Override
		public boolean isAnimateOnTerminate() {
			return false;
		}

	}

}