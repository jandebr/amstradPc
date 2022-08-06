package org.maia.amstrad.pc.menu;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;

public class MonitorFullscreenAction extends AmstradPcAction {

	public MonitorFullscreenAction(AmstradPc amstradPc) {
		this(amstradPc, "Fullscreen switch");
	}

	public MonitorFullscreenAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		amstradPc.addEventListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleFullscreen();
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			KeyEvent key = ((AmstradPcKeyboardEvent) event).getKeyPressed();
			if (key.getKeyCode() == KeyEvent.VK_F11) {
				toggleFullscreen();
			}
		}
	}

	private void toggleFullscreen() {
		getAmstradPc().toggleWindowFullscreen();
	}

}