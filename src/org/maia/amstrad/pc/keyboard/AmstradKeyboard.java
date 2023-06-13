package org.maia.amstrad.pc.keyboard;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.pc.AmstradDevice;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.util.AmstradIO;
import org.maia.amstrad.util.AmstradListenerList;

public abstract class AmstradKeyboard extends AmstradDevice {

	private AmstradListenerList<AmstradKeyboardListener> keyboardListeners;

	protected AmstradKeyboard(AmstradPc amstradPc) {
		super(amstradPc);
		this.keyboardListeners = new AmstradListenerList<AmstradKeyboardListener>();
	}

	public abstract boolean isTyping();

	public abstract boolean isAutotyping();

	public abstract void type(CharSequence text, boolean waitUntilTyped);

	public void type(CharSequence text) {
		type(text, true);
	}

	public void typeFileContents(File textFile, boolean waitUntilTyped) throws IOException {
		type(AmstradIO.readTextFileContents(textFile), waitUntilTyped);
	}

	public void typeFileContents(File textFile) throws IOException {
		typeFileContents(textFile, true);
	}

	public void enter(CharSequence text, boolean waitUntilTyped) {
		type(text + "\n", waitUntilTyped);
	}

	public void enter(CharSequence text) {
		enter(text, true);
	}

	public void enter(boolean waitUntilTyped) {
		enter("", waitUntilTyped);
	}

	public void enter() {
		enter(true);
	}

	public abstract void breakEscape();

	public abstract AmstradKeyboardController getController();

	public void addKeyboardListener(AmstradKeyboardListener listener) {
		getKeyboardListeners().addListener(listener);
	}

	public void removeKeyboardListener(AmstradKeyboardListener listener) {
		getKeyboardListeners().removeListener(listener);
	}

	protected void fireKeyboardEventDispatched(AmstradKeyboardEvent event) {
		if (!getKeyboardListeners().isEmpty()) {
			for (AmstradKeyboardListener listener : getKeyboardListeners()) {
				listener.amstradKeyboardEventDispatched(event);
			}
		}
	}

	protected void fireKeyboardBreakEscaped() {
		for (AmstradKeyboardListener listener : getKeyboardListeners()) {
			listener.amstradKeyboardBreakEscaped(this);
		}
	}

	protected AmstradListenerList<AmstradKeyboardListener> getKeyboardListeners() {
		return keyboardListeners;
	}

}