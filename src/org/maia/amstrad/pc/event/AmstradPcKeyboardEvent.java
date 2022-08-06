package org.maia.amstrad.pc.event;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;

public class AmstradPcKeyboardEvent extends AmstradPcEvent {

	private KeyEvent keyPressed;

	public AmstradPcKeyboardEvent(AmstradPc source, KeyEvent keyPressed) {
		super(source);
		this.keyPressed = keyPressed;
	}

	public KeyEvent getKeyPressed() {
		return keyPressed;
	}

}