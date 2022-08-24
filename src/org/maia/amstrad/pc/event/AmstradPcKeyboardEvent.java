package org.maia.amstrad.pc.event;

import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;

public class AmstradPcKeyboardEvent extends AmstradPcEvent {

	private KeyEvent key;

	public AmstradPcKeyboardEvent(AmstradPc source, KeyEvent key) {
		super(source);
		this.key = key;
	}

	public boolean isKeyPressed() {
		return getKey().getID() == KeyEvent.KEY_PRESSED;
	}

	public boolean isKeyReleased() {
		return getKey().getID() == KeyEvent.KEY_RELEASED;
	}

	public boolean isKeyTyped() {
		return getKey().getID() == KeyEvent.KEY_TYPED;
	}

	public boolean isControlDown() {
		return getKey().isControlDown();
	}

	public boolean isAltDown() {
		return getKey().isAltDown();
	}

	public boolean isShiftDown() {
		return getKey().isShiftDown();
	}

	public int getKeyCode() {
		return getKey().getKeyCode();
	}

	public KeyEvent getKey() {
		return key;
	}

}