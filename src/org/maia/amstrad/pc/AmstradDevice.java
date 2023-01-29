package org.maia.amstrad.pc;

public abstract class AmstradDevice {

	private AmstradPc amstradPc;

	protected AmstradDevice(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	protected void checkStarted() {
		if (!getAmstradPc().isStarted())
			throw new IllegalStateException("The Amstrad PC has not been started");
	}

	protected void checkNotTerminated() {
		if (getAmstradPc().isTerminated())
			throw new IllegalStateException("The Amstrad PC was terminated");
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}