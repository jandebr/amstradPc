package org.maia.amstrad.basic;

import org.maia.amstrad.program.loader.AmstradProgramRuntime;

public abstract class BasicProgramRuntime implements AmstradProgramRuntime {

	private BasicRuntime basicRuntime;

	protected BasicProgramRuntime(BasicRuntime basicRuntime) {
		this.basicRuntime = basicRuntime;
	}

	@Override
	public void run() {
		getBasicRuntime().keyboardEnter("RUN", true);
	}

	public BasicRuntime getBasicRuntime() {
		return basicRuntime;
	}

}