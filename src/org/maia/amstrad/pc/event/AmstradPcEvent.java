package org.maia.amstrad.pc.event;

import java.util.EventObject;

import org.maia.amstrad.pc.AmstradPc;

public class AmstradPcEvent extends EventObject {

	public AmstradPcEvent(AmstradPc source) {
		super(source);
	}

	@Override
	public AmstradPc getSource() {
		return (AmstradPc) super.getSource();
	}

}