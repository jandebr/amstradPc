package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.maia.amstrad.pc.AmstradPc;

public class FullscreenAction extends AmstradPcAction {

	public FullscreenAction(AmstradPc amstradPc) {
		this(amstradPc, "Fullscreen");
	}

	public FullscreenAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
	}

	public FullscreenAction(AmstradPc amstradPc, String name, Icon icon) {
		super(amstradPc, name, icon);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		getAmstradPc().toggleFullscreen();
	}

}