package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import org.maia.amstrad.pc.AmstradPc;

public class MonitorFullscreenAction extends AmstradPcAction {

	public MonitorFullscreenAction(AmstradPc amstradPc) {
		this(amstradPc, "Fullscreen switch");
	}

	public MonitorFullscreenAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().toggleWindowFullscreen();
	}

}