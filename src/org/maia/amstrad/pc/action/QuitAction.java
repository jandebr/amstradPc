package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.event.AmstradPcEvent;
import org.maia.amstrad.pc.event.AmstradPcKeyboardEvent;

public class QuitAction extends AmstradPcAction {

	public QuitAction(AmstradPc amstradPc) {
		this(amstradPc, "Quit");
	}

	public QuitAction(AmstradPc amstradPc, String name) {
		super(amstradPc, name);
		amstradPc.addEventListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		quit();
	}

	@Override
	public void amstradPcEventDispatched(AmstradPcEvent event) {
		super.amstradPcEventDispatched(event);
		if (event instanceof AmstradPcKeyboardEvent) {
			KeyEvent key = ((AmstradPcKeyboardEvent) event).getKeyPressed();
			if (key.getKeyCode() == KeyEvent.VK_Q && key.isControlDown() && key.isShiftDown()) {
				quit();
			}
		}
	}

	private void quit() {
		getAmstradPc().terminate();
	}

}