package org.maia.amstrad.system.impl;

import org.maia.amstrad.AmstradException;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemSettings;
import org.maia.amstrad.system.AmstradSystemTermination;

public class AmstradEntertainmentSystem extends AmstradSystem {

	public static final String NAME = "ENTERTAINMENT";

	public AmstradEntertainmentSystem() {
	}

	@Override
	protected void doLaunch(AmstradPc amstradPc, String[] args) throws AmstradException {
		AmstradMonitor monitor = amstradPc.getMonitor();
		monitor.setMode(AmstradMonitorMode.COLOR);
		monitor.setWindowAlwaysOnTop(false); // Keep system windows accessible (e.g. Bluetooth manager)
		AmstradPcFrame frame = amstradPc.displayInFrame(false);
		createPopupMenu().install();
		amstradPc.addStateListener(new AmstradPcPostStartupAction());
		amstradPc.start();
	}

	@Override
	protected AmstradSystemTermination createTermination() {
		return new AmstradSystemAnimatedTermination();
	}

	@Override
	protected AmstradSystemSettings createSystemSettings() {
		return new EntertainmentSystemSettings();
	}

	@Override
	public String getName() {
		return NAME;
	}

	private class AmstradPcPostStartupAction extends AmstradPcStateAdapter {

		@Override
		public void amstradPcStarted(AmstradPc amstradPc) {
			amstradPc.getMonitor().makeFullscreen();
			getAmstradContext().showProgramBrowser(amstradPc);
		}

	}

	private static class EntertainmentSystemSettings implements AmstradSystemSettings {

		@Override
		public boolean isProgramBrowserCentric() {
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
		public boolean isTapeActivityShown() {
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