package org.maia.amstrad.pc.browser.repo;

import org.maia.amstrad.pc.AmstradMonitorMode;

public abstract class AmstradProgramInfo {

	protected AmstradProgramInfo() {
	}

	public abstract String getProgramName();

	public abstract AmstradMonitorMode getPreferredMonitorMode();

}