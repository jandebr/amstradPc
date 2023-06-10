package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

public class MonitorFullscreenAction extends AmstradPcAction {

	public MonitorFullscreenAction(AmstradPc amstradPc) {
		this(amstradPc, "Fullscreen switch");
	}

	public MonitorFullscreenAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		amstradPc.getKeyboard().addKeyboardListener(this);
		setEnabled(getAmstradContext().getMode().isFullscreenToggleEnabled());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleFullscreen();
	}

	@Override
	public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (event.isKeyPressed() && event.getKeyCode() == KeyEvent.VK_F11) {
			toggleFullscreen();
		}
	}

	private void toggleFullscreen() {
		if (isEnabled()) {
			getAmstradPc().getMonitor().toggleWindowFullscreen();
		}
	}

}