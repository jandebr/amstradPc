package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;

public class WindowFullscreenAction extends AmstradPcAction {

	private static String NAME_FULLSCREEN = "Show fullscreen";

	private static String NAME_WINDOWED = "Show windowed";

	public WindowFullscreenAction(AmstradPc amstradPc) {
		super(amstradPc, "");
		updateName();
		amstradPc.getMonitor().addMonitorListener(this);
		amstradPc.getKeyboard().addKeyboardListener(this);
		setEnabled(getAmstradContext().getMode().isFullscreenToggleEnabled());
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		toggleFullscreen();
	}

	@Override
	public void amstradWindowFullscreenChanged(AmstradMonitor monitor) {
		super.amstradWindowFullscreenChanged(monitor);
		updateName();
	}

	private void updateName() {
		if (isFullscreen()) {
			changeName(NAME_WINDOWED);
		} else {
			changeName(NAME_FULLSCREEN);
		}
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

	public boolean isFullscreen() {
		return getAmstradPc().getMonitor().isWindowFullscreen();
	}

}