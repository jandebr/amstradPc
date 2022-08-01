package org.maia.amstrad.pc.browser.repo;

import org.maia.amstrad.pc.AmstradMonitorMode;
import org.maia.amstrad.pc.AmstradPc;

public abstract class AmstradProgram {

	protected AmstradProgram() {
	}

	public abstract String getProgramName();

	public abstract AmstradMonitorMode getPreferredMonitorMode();

	public abstract boolean hasInfo();

	public abstract void loadInto(AmstradPc amstradPc) throws AmstradProgramException;

	public void runWith(AmstradPc amstradPc) throws AmstradProgramException {
		loadInto(amstradPc);
		amstradPc.getBasicRuntime().run();
	}

}