package org.maia.amstrad.pc.keyboard;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradDevice;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.util.AmstradIO;

public abstract class AmstradKeyboard extends AmstradDevice {

	private List<AmstradKeyboardListener> keyboardListeners;

	protected AmstradKeyboard(AmstradPc amstradPc) {
		super(amstradPc);
		this.keyboardListeners = new Vector<AmstradKeyboardListener>();
	}
	
	public abstract boolean isTyping();

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

	public abstract AmstradKeyboardController getController();

	public void addKeyboardListener(AmstradKeyboardListener listener) {
		getKeyboardListeners().add(listener);
	}

	public void removeKeyboardListener(AmstradKeyboardListener listener) {
		getKeyboardListeners().remove(listener);
	}

	protected void fireKeyboardEventDispatched(AmstradKeyboardEvent event) {
		if (!getKeyboardListeners().isEmpty()) {
			for (AmstradKeyboardListener listener : getKeyboardListeners()) {
				listener.amstradKeyboardEventDispatched(event);
			}
		}
	}

	protected void fireKeyboardBreakEscaped() {
		for (AmstradKeyboardListener listener : getKeyboardListenersFixedList()) {
			listener.amstradKeyboardBreakEscaped(this);
		}
	}

	private List<AmstradKeyboardListener> getKeyboardListenersFixedList() {
		return new Vector<AmstradKeyboardListener>(getKeyboardListeners());
	}

	protected List<AmstradKeyboardListener> getKeyboardListeners() {
		return keyboardListeners;
	}

}