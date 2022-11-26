package org.maia.amstrad.basic;

public abstract class BasicProgramRuntime {

	private BasicRuntime basicRuntime;

	protected BasicProgramRuntime(BasicRuntime basicRuntime) {
		this.basicRuntime = basicRuntime;
	}

	public void run() {
		getBasicRuntime().keyboardEnter("RUN", true);
	}

	public BasicRuntime getBasicRuntime() {
		return basicRuntime;
	}

}