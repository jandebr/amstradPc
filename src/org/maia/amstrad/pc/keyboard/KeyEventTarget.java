package org.maia.amstrad.pc.keyboard;

import java.awt.event.KeyEvent;

public interface KeyEventTarget {

	void pressKey(KeyEvent keyEvent);

	void releaseKey(KeyEvent keyEvent);

}