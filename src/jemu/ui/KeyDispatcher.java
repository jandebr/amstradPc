package jemu.ui;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

import jemu.core.device.Computer;

public class KeyDispatcher implements KeyListener {

	private Component source;

	private Computer destination;

	private boolean ctrlDown;

	private boolean shiftDown;

	private boolean altDown;

	private boolean virtualShiftKey, virtualUnshiftKey, deferredShift, skipUnshift;

	private KeyEvent virtualShiftKeyEventPressed;

	private KeyEvent virtualShiftKeyEventReleased;

	public KeyDispatcher(Component source) {
		this(source, null);
	}

	public KeyDispatcher(Component source, Computer destination) {
		changeSource(source);
		changeDestination(destination);
	}

	public synchronized void resetKeyModifiers() {
		if (hasSource()) {
			Component source = getSource();
			char cUnd = KeyEvent.CHAR_UNDEFINED;
			keyReleased(new KeyEvent(source, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_CONTROL, cUnd));
			keyReleased(new KeyEvent(source, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_SHIFT, cUnd));
			keyReleased(new KeyEvent(source, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_ALT, cUnd));
		}
	}

	public synchronized void breakEscape() {
		if (hasDestination() && getDestination().isRunning()) {
			getDestination().breakEscape();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// no action
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// System.out.println("KEY PRESSED " + formatKeyEvent(e));
		// Remember modifiers
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_CONTROL) {
			setCtrlDown(true);
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			setShiftDown(true);
		} else if (keyCode == KeyEvent.VK_ALT) {
			setAltDown(true);
		}
		// Map & handle key press
		e = cloneKeyEvent(e);
		virtualShiftKey = false;
		virtualUnshiftKey = false;
		applyKeyboardMapping(e);
		handleMappedKeyPress(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// System.out.println("KEY RELEASED " + formatKeyEvent(e));
		// Map & handle key release
		e = cloneKeyEvent(e);
		virtualShiftKey = false;
		virtualUnshiftKey = false;
		applyKeyboardMapping(e);
		handleMappedKeyRelease(e);
		// Restore modifiers
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_CONTROL) {
			setCtrlDown(false);
		} else if (keyCode == KeyEvent.VK_SHIFT) {
			setShiftDown(false);
		} else if (keyCode == KeyEvent.VK_ALT) {
			setAltDown(false);
		}
	}

	protected void applyKeyboardMapping(KeyEvent e) {
		applyDefaultKeyboardMapping(e);
	}

	protected void applyDefaultKeyboardMapping(KeyEvent e) {
		char keyChar = e.getKeyChar();
		int keyCode = e.getKeyCode();
		if (keyCode == 65406 || "²³°é§èçàùµ~¨".indexOf(keyChar) >= 0) {
			e.setKeyCode(KeyEvent.VK_UNDEFINED);
		} else if (keyChar == '0' && e.isShiftDown()) {
			e.setKeyCode(123); // 0
		} else if (keyChar >= '1' && keyChar <= '9' && e.isShiftDown()) {
			e.setKeyCode(112 + (keyChar - '1')); // 1,2,...,9
		} else if (keyCode == 106) {
			virtualShiftKey = true;
			e.setKeyCode(59); // numpad '*'
		} else if (keyCode == 107) {
			virtualShiftKey = true;
			e.setKeyCode(222); // numpad '+'
		} else if (keyCode == 109) {
			e.setKeyCode(45); // numpad '-'
		} else if (keyCode == 111) {
			e.setKeyCode(47); // numpad '/'
		} else if (keyChar == '&') {
			virtualShiftKey = true;
			e.setKeyCode(54);
		} else if (keyChar == '|') {
			virtualShiftKey = true;
			e.setKeyCode(91);
		} else if (keyChar == '"') {
			virtualShiftKey = true;
			e.setKeyCode(50);
		} else if (keyChar == '#') {
			virtualShiftKey = true;
			e.setKeyCode(51);
		} else if (keyChar == '\'') {
			virtualShiftKey = true;
			e.setKeyCode(55);
		} else if (keyChar == '(') {
			virtualShiftKey = true;
			e.setKeyCode(56);
		} else if (keyChar == '!') {
			virtualShiftKey = true;
			e.setKeyCode(49);
		} else if (keyChar == '{') {
			virtualShiftKey = true;
			e.setKeyCode(65406);
		} else if (keyChar == '}') {
			virtualShiftKey = true;
			e.setKeyCode(93);
		} else if (keyChar == ')') {
			virtualShiftKey = true;
			e.setKeyCode(57);
		} else if (keyChar == '$') {
			virtualShiftKey = true;
			e.setKeyCode(52);
		} else if (keyChar == '´') {
			virtualShiftKey = true;
			e.setKeyCode(55);
		} else if (keyChar == '`') {
			virtualShiftKey = true;
			e.setKeyCode(92);
		} else if (keyChar == '<') {
			virtualShiftKey = true;
			e.setKeyCode(44);
		} else if (keyChar == '=') {
			virtualShiftKey = true;
			e.setKeyCode(45);
		} else if (keyChar == '@') {
			e.setKeyCode(91);
		} else if (keyChar == '-') {
			e.setKeyCode(45);
		} else if (keyChar == '^') {
			e.setKeyCode(61);
		} else if (keyCode == 130) {
			e.setKeyCode(65406); // [
		} else if (keyChar == ']') {
			e.setKeyCode(93);
		} else if (keyChar == '\\') {
			e.setKeyCode(92);
		} else if (keyChar == ',') {
			e.setKeyCode(44);
		} else if (keyChar == ';') {
			e.setKeyCode(222);
		} else if (keyChar == ':') {
			e.setKeyCode(59);
		} else if (keyChar == '/') {
			virtualUnshiftKey = true;
			e.setKeyCode(47);
		} else if (keyChar == '_') {
			e.setKeyCode(48);
		} else if (keyChar == '*') {
			e.setKeyCode(59);
		} else if (keyChar == '%') {
			e.setKeyCode(53);
		} else if (keyChar == '£') {
			e.setKeyCode(61);
		} else if (keyChar == '>') {
			e.setKeyCode(46);
		} else if (keyChar == '?') {
			e.setKeyCode(47);
		} else if (keyChar == '.') {
			e.setKeyCode(110);
		} else if (keyChar == '+') {
			e.setKeyCode(222);
		}
	}

	protected void handleMappedKeyPress(KeyEvent e) {
		if (shouldDispatchKeyToDestination(e)) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				deferredShift = true;
			} else {
				if ((deferredShift || virtualShiftKey) && !virtualUnshiftKey) {
					dispatchKeyToDestination(getVirtualShiftKeyEventPressed());
				} else if (deferredShift) {
					skipUnshift = true;
				}
				deferredShift = false;
				dispatchKeyToDestination(e);
			}
		}
	}

	protected void handleMappedKeyRelease(KeyEvent e) {
		if (shouldDispatchKeyToDestination(e)) {
			if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
				if (!deferredShift && !skipUnshift) {
					dispatchKeyToDestination(e);
				} else {
					deferredShift = false;
					skipUnshift = false;
				}
			} else {
				dispatchKeyToDestination(e);
				if (virtualShiftKey) {
					dispatchKeyToDestination(getVirtualShiftKeyEventReleased());
				}
			}
		}
	}

	protected void dispatchKeyToDestination(KeyEvent e) {
		if (hasDestination() && e != null) {
			getDestination().processKeyEvent(e);
		}
	}

	protected boolean shouldDispatchKeyToDestination(KeyEvent e) {
		if (Switches.blockKeyboard)
			return false;
		if (isFunctionKey(e) && !e.isShiftDown())
			return false;
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_UNDEFINED)
			return false;
		if (keyCode == KeyEvent.VK_CONTROL)
			return false;
		if (keyCode == KeyEvent.VK_ALT)
			return false;
		if (isAlphabeticKey(e) && (isCtrlDown() || isAltDown()))
			return false;
		return true;
	}

	private boolean isAlphabeticKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		return keyCode >= KeyEvent.VK_A && keyCode <= KeyEvent.VK_Z;
	}

	private boolean isFunctionKey(KeyEvent e) {
		int keyCode = e.getKeyCode();
		return keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F12;
	}

	private KeyEvent cloneKeyEvent(KeyEvent e) {
		return new KeyEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getKeyCode(),
				e.getKeyChar(), e.getKeyLocation());
	}

	public static String formatKeyEvent(KeyEvent e) {
		StringBuilder sb = new StringBuilder(64);
		sb.append("KeyEvent");
		if (e.getID() == KeyEvent.KEY_PRESSED) {
			sb.append(" pressed");
		} else if (e.getID() == KeyEvent.KEY_RELEASED) {
			sb.append(" released");
		} else if (e.getID() == KeyEvent.KEY_TYPED) {
			sb.append(" typed");
		} else {
			sb.append(" ?id=" + e.getID());
		}
		sb.append(" code:").append(e.getKeyCode());
		sb.append(" char:").append(e.getKeyChar());
		sb.append(" charNr:").append((int) e.getKeyChar());
		sb.append(" mod:").append(InputEvent.getModifiersExText(e.getModifiersEx()));
		sb.append(" ctrl:").append(e.isControlDown());
		sb.append(" shift:").append(e.isShiftDown());
		sb.append(" alt:").append(e.isAltDown());
		return sb.toString();
	}

	public synchronized void changeSource(Component source) {
		if (hasSource()) {
			getSource().removeKeyListener(this);
		}
		setSource(source);
		if (source != null) {
			if (!Arrays.asList(source.getKeyListeners()).contains(this))
				source.addKeyListener(this);
			setVirtualShiftKeyEventPressed(
					new KeyEvent(source, KeyEvent.KEY_PRESSED, 0L, 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
			setVirtualShiftKeyEventReleased(
					new KeyEvent(source, KeyEvent.KEY_RELEASED, 0L, 0, KeyEvent.VK_SHIFT, KeyEvent.CHAR_UNDEFINED));
		}
	}

	public synchronized void changeDestination(Computer destination) {
		setDestination(destination);
	}

	private boolean hasSource() {
		return getSource() != null;
	}

	private boolean hasDestination() {
		return getDestination() != null;
	}

	public Component getSource() {
		return source;
	}

	private void setSource(Component source) {
		this.source = source;
	}

	public Computer getDestination() {
		return destination;
	}

	private void setDestination(Computer destination) {
		this.destination = destination;
	}

	public boolean isCtrlDown() {
		return ctrlDown;
	}

	protected void setCtrlDown(boolean ctrlDown) {
		this.ctrlDown = ctrlDown;
	}

	public boolean isShiftDown() {
		return shiftDown;
	}

	protected void setShiftDown(boolean shiftDown) {
		this.shiftDown = shiftDown;
	}

	public boolean isAltDown() {
		return altDown;
	}

	protected void setAltDown(boolean altDown) {
		this.altDown = altDown;
	}

	private KeyEvent getVirtualShiftKeyEventPressed() {
		return virtualShiftKeyEventPressed;
	}

	private void setVirtualShiftKeyEventPressed(KeyEvent e) {
		this.virtualShiftKeyEventPressed = e;
	}

	private KeyEvent getVirtualShiftKeyEventReleased() {
		return virtualShiftKeyEventReleased;
	}

	private void setVirtualShiftKeyEventReleased(KeyEvent e) {
		this.virtualShiftKeyEventReleased = e;
	}

}