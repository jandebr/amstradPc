package org.maia.amstrad.pc;

public abstract class AmstradPcDevice {

	private AmstradPc amstradPc;

	protected AmstradPcDevice(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

}