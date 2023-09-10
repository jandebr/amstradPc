package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorTripleSizeAction extends MonitorSizeAction {

	public MonitorTripleSizeAction(AmstradPc amstradPc) {
		this(amstradPc, "Triple size");
	}

	public MonitorTripleSizeAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name, 3);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().getMonitor().setTripleSize();
	}

}