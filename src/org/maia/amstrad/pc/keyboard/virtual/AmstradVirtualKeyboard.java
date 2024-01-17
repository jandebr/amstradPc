package org.maia.amstrad.pc.keyboard.virtual;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcDevice;
import org.maia.amstrad.pc.keyboard.KeyEventTarget;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;
import org.maia.util.GenericListenerList;

public class AmstradVirtualKeyboard extends AmstradPcDevice {

	private KeyEventTarget target;

	private KeyEventTargetController targetController;

	private AmstradVirtualKeyboardLayout layout;

	private List<KeyGroup> keyGroups;

	private boolean active;

	private Key keyAtInitialCursor;

	private Key keyAtCursor;

	private Key keyBeingPressed;

	private boolean autoDeactivateOnKeyRelease;

	private GenericListenerList<AmstradVirtualKeyboardStateListener> keyboardStateListeners;

	public AmstradVirtualKeyboard(AmstradPc amstradPc, AmstradVirtualKeyboardLayout layout) {
		super(amstradPc);
		this.targetController = new KeyEventTargetController();
		this.layout = layout;
		this.keyGroups = new Vector<KeyGroup>();
		this.keyboardStateListeners = new GenericListenerList<AmstradVirtualKeyboardStateListener>();
	}

	public synchronized void changeTarget(KeyEventTarget newTarget) {
		if (newTarget != null) {
			releaseKeyBeingPressed();
			setTarget(newTarget);
		}
	}

	public synchronized void addKeyGroup(KeyGroup group) {
		getKeyGroups().add(group);
		if (getKeyAtInitialCursor() == null && !group.isEmpty()) {
			setKeyAtInitialCursor(group.getKeys().get(0));
		}
	}

	public final void switchActiveState(boolean active) {
		if (active) {
			activate();
		} else {
			deactivate();
		}
	}

	public final void toggleActiveState() {
		if (isActive()) {
			deactivate();
		} else {
			activate();
		}
	}

	public synchronized void activate() {
		if (!isActive()) {
			setActive(true);
			if (getKeyAtCursor() == null) {
				changeKeyAtCursor(getKeyAtInitialCursor());
			}
			fireKeyboardActivated();
		}
	}

	public synchronized void deactivate() {
		if (isActive()) {
			releaseKeyBeingPressed();
			setActive(false);
			fireKeyboardDeactivated();
		}
	}

	public synchronized void moveCursorLeft() {
		if (isActive()) {
			changeKeyAtCursor(getLayout().getKeyMovingLeft(this));
		}
	}

	public synchronized void moveCursorRight() {
		if (isActive()) {
			changeKeyAtCursor(getLayout().getKeyMovingRight(this));
		}
	}

	public synchronized void moveCursorUp() {
		if (isActive()) {
			changeKeyAtCursor(getLayout().getKeyMovingUp(this));
		}
	}

	public synchronized void moveCursorDown() {
		if (isActive()) {
			changeKeyAtCursor(getLayout().getKeyMovingDown(this));
		}
	}

	public synchronized void changeKeyAtCursor(Key key) {
		if (key != null) {
			setKeyAtCursor(key);
		}
	}

	public synchronized void pressKeyAtCursor() {
		if (isActive()) {
			Key key = getKeyAtCursor();
			if (key != null) {
				releaseKeyBeingPressed(); // if any
				setKeyBeingPressed(key);
				setAutoDeactivateOnKeyRelease(shouldDeactivateOnKeyRelease(key));
				for (KeyEvent event : key.produceKeyPressedEventSequence()) {
					getTarget().pressKey(event);
				}
			}
		}
	}

	public synchronized void releaseKeyBeingPressed() {
		Key key = getKeyBeingPressed();
		if (key != null) {
			for (KeyEvent event : key.produceKeyReleasedEventSequence()) {
				getTarget().releaseKey(event);
			}
			setKeyBeingPressed(null);
			if (isAutoDeactivateOnKeyRelease()) {
				deactivate();
			}
		}
	}

	protected boolean shouldDeactivateOnKeyRelease(Key key) {
		return false; // Subclasses may override this
	}

	public void addKeyboardStateListener(AmstradVirtualKeyboardStateListener listener) {
		getKeyboardStateListeners().addListener(listener);
	}

	public void removeKeyboardStateListener(AmstradVirtualKeyboardStateListener listener) {
		getKeyboardStateListeners().removeListener(listener);
	}

	protected void fireKeyboardActivated() {
		for (AmstradVirtualKeyboardStateListener listener : getKeyboardStateListeners())
			listener.amstradVirtualKeyboardActivated(this);
	}

	protected void fireKeyboardDeactivated() {
		for (AmstradVirtualKeyboardStateListener listener : getKeyboardStateListeners())
			listener.amstradVirtualKeyboardDeactivated(this);
	}

	protected KeyEventTarget getTarget() {
		return target;
	}

	private void setTarget(KeyEventTarget target) {
		this.target = target;
	}

	private KeyEventTargetController getTargetController() {
		return targetController;
	}

	public AmstradVirtualKeyboardLayout getLayout() {
		return layout;
	}

	public List<KeyGroup> getKeyGroups() {
		return keyGroups;
	}

	public boolean isActive() {
		return active;
	}

	private void setActive(boolean active) {
		this.active = active;
	}

	public Key getKeyAtInitialCursor() {
		return keyAtInitialCursor;
	}

	public void setKeyAtInitialCursor(Key key) {
		this.keyAtInitialCursor = key;
	}

	public Key getKeyAtCursor() {
		return keyAtCursor;
	}

	private void setKeyAtCursor(Key key) {
		this.keyAtCursor = key;
	}

	public Key getKeyBeingPressed() {
		return keyBeingPressed;
	}

	private void setKeyBeingPressed(Key key) {
		this.keyBeingPressed = key;
	}

	private boolean isAutoDeactivateOnKeyRelease() {
		return autoDeactivateOnKeyRelease;
	}

	private void setAutoDeactivateOnKeyRelease(boolean autoDeactivate) {
		this.autoDeactivateOnKeyRelease = autoDeactivate;
	}

	protected GenericListenerList<AmstradVirtualKeyboardStateListener> getKeyboardStateListeners() {
		return keyboardStateListeners;
	}

	public static abstract class Key {

		private KeyEvent prototypeEvent;

		protected Key(KeyEvent prototypeEvent) {
			this.prototypeEvent = prototypeEvent;
		}

		public List<KeyEvent> produceKeyPressedEventSequence() {
			KeyEvent prototype = getPrototypeEvent();
			Component source = prototype.getComponent();
			long now = System.currentTimeMillis();
			int mods = prototype.getModifiersEx();
			List<KeyEvent> sequence = new Vector<KeyEvent>(4);
			sequence.addAll(produceKeyPressedModifiersEventSequence(mods, source));
			sequence.add(new KeyEvent(source, KeyEvent.KEY_PRESSED, now, mods, getKeyCode(), getKeyChar()));
			return sequence;
		}

		public List<KeyEvent> produceKeyReleasedEventSequence() {
			KeyEvent prototype = getPrototypeEvent();
			Component source = prototype.getComponent();
			long now = System.currentTimeMillis();
			int mods = prototype.getModifiersEx();
			List<KeyEvent> sequence = new Vector<KeyEvent>(4);
			sequence.add(new KeyEvent(source, KeyEvent.KEY_RELEASED, now, mods, getKeyCode(), getKeyChar()));
			sequence.addAll(produceKeyReleasedModifiersEventSequence(mods, source));
			return sequence;
		}

		private List<KeyEvent> produceKeyPressedModifiersEventSequence(int keyModifiers, Component source) {
			boolean ctrl = (keyModifiers & KeyEvent.CTRL_DOWN_MASK) > 0;
			boolean shift = (keyModifiers & KeyEvent.SHIFT_DOWN_MASK) > 0;
			boolean alt = (keyModifiers & KeyEvent.ALT_DOWN_MASK) > 0;
			if (!ctrl && !shift && !alt) {
				return Collections.emptyList();
			} else {
				List<KeyEvent> sequence = new Vector<KeyEvent>(3);
				char cUnd = KeyEvent.CHAR_UNDEFINED;
				long now = System.currentTimeMillis();
				int mods = 0;
				if (ctrl) {
					mods |= KeyEvent.CTRL_DOWN_MASK;
					sequence.add(new KeyEvent(source, KeyEvent.KEY_PRESSED, now, mods, KeyEvent.VK_CONTROL, cUnd));
				}
				if (shift) {
					mods |= KeyEvent.SHIFT_DOWN_MASK;
					sequence.add(new KeyEvent(source, KeyEvent.KEY_PRESSED, now, mods, KeyEvent.VK_SHIFT, cUnd));
				}
				if (alt) {
					mods |= KeyEvent.ALT_DOWN_MASK;
					sequence.add(new KeyEvent(source, KeyEvent.KEY_PRESSED, now, mods, KeyEvent.VK_ALT, cUnd));
				}
				return sequence;
			}
		}

		private List<KeyEvent> produceKeyReleasedModifiersEventSequence(int keyModifiers, Component source) {
			boolean ctrl = (keyModifiers & KeyEvent.CTRL_DOWN_MASK) > 0;
			boolean shift = (keyModifiers & KeyEvent.SHIFT_DOWN_MASK) > 0;
			boolean alt = (keyModifiers & KeyEvent.ALT_DOWN_MASK) > 0;
			if (!ctrl && !shift && !alt) {
				return Collections.emptyList();
			} else {
				List<KeyEvent> sequence = new Vector<KeyEvent>(3);
				char cUnd = KeyEvent.CHAR_UNDEFINED;
				long now = System.currentTimeMillis();
				int mods = keyModifiers;
				if (alt) {
					mods &= ~KeyEvent.ALT_DOWN_MASK;
					sequence.add(new KeyEvent(source, KeyEvent.KEY_RELEASED, now, mods, KeyEvent.VK_ALT, cUnd));
				}
				if (shift) {
					mods &= ~KeyEvent.SHIFT_DOWN_MASK;
					sequence.add(new KeyEvent(source, KeyEvent.KEY_RELEASED, now, mods, KeyEvent.VK_SHIFT, cUnd));
				}
				if (ctrl) {
					mods &= ~KeyEvent.CTRL_DOWN_MASK;
					sequence.add(new KeyEvent(source, KeyEvent.KEY_RELEASED, now, mods, KeyEvent.VK_CONTROL, cUnd));
				}
				return sequence;
			}
		}

		public int getKeyCode() {
			return getPrototypeEvent().getKeyCode();
		}

		public char getKeyChar() {
			return getPrototypeEvent().getKeyChar();
		}

		private KeyEvent getPrototypeEvent() {
			return prototypeEvent;
		}

	}

	public static abstract class KeyGroup {

		private List<Key> keys;

		protected KeyGroup() {
			this.keys = new Vector<Key>();
		}

		public void addKey(Key key) {
			getKeys().add(key);
		}

		public boolean isEmpty() {
			return getKeys().isEmpty();
		}

		public List<Key> getKeys() {
			return keys;
		}

	}

	private class KeyEventTargetController extends AmstradMonitorAdapter {

		public KeyEventTargetController() {
			getAmstradPc().getMonitor().addMonitorListener(this);
			updateTarget();
		}

		@Override
		public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
			updateTarget();
		}

		private void updateTarget() {
			AmstradMonitor monitor = getAmstradPc().getMonitor();
			if (monitor.isAlternativeDisplaySourceShowing()) {
				changeTarget(monitor.getCurrentAlternativeDisplaySource());
			} else {
				changeTarget(getAmstradPc().getKeyboard());
			}
		}

	}

}