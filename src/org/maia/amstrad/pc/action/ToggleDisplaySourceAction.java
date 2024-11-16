package org.maia.amstrad.pc.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public abstract class ToggleDisplaySourceAction extends AmstradPcAction {

	/**
	 * Action name when in the state to open the display source
	 */
	private String nameToOpen;

	/**
	 * Action name when in the state to close the display source
	 */
	private String nameToClose;

	/**
	 * Action key. Is <code>null</code> when not actionable from a key event
	 */
	private ToggleActionKey actionKey;

	private boolean waitForKeyModifierRelease;

	private boolean suppressToggleUntilKeyModifierRelease;

	protected static final KeyModifier CTRL_KEY_MODIFIER = new CtrlKeyModifier();

	protected static final KeyModifier CTRL_SHIFT_KEY_MODIFIER = new CtrlShiftKeyModifier();

	protected ToggleDisplaySourceAction(AmstradPc amstradPc) {
		this(amstradPc, null);
	}

	protected ToggleDisplaySourceAction(AmstradPc amstradPc, ToggleActionKey actionKey) {
		this(amstradPc, "Open", "Close", actionKey);
	}

	protected ToggleDisplaySourceAction(AmstradPc amstradPc, String nameToOpen, String nameToClose) {
		this(amstradPc, nameToOpen, nameToClose, null);
	}

	protected ToggleDisplaySourceAction(AmstradPc amstradPc, String nameToOpen, String nameToClose,
			ToggleActionKey actionKey) {
		super(amstradPc, nameToOpen);
		this.nameToOpen = nameToOpen;
		this.nameToClose = nameToClose;
		this.actionKey = actionKey;
		updateName();
		amstradPc.getMonitor().addMonitorListener(this);
		if (hasActionKey()) {
			amstradPc.getKeyboard().addKeyboardListener(this);
		}
	}

	@Override
	public void amstradDisplaySourceChanged(AmstradMonitor monitor) {
		super.amstradDisplaySourceChanged(monitor);
		updateName();
	}

	@Override
	public final void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
		super.amstradKeyboardEventDispatched(event);
		if (hasActionKey()) {
			if (event.isKeyPressed() && getActionKey().getKeyModifier().matches(event)) {
				waitForKeyModifierRelease = true;
				if (event.getKeyCode() == getActionKey().getKeyCode()) {
					if (isTriggeredByMenuKeyBindings()) {
						// will be handled by actionPerformed()
					} else {
						// will not be handled by actionPerformed()
						toggleDisplaySource(true);
					}
				}
			} else if (waitForKeyModifierRelease && event.isKeyReleased() && event.getKeyModifiers() == 0) {
				if (suppressToggleUntilKeyModifierRelease
						&& getAmstradPc().getMonitor().isPrimaryDisplaySourceShowing()) {
					getAmstradPc().getKeyboard().getController().sendKeyboardEventsToComputer(true);
				}
				waitForKeyModifierRelease = false;
				suppressToggleUntilKeyModifierRelease = false;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		boolean invokedByKeyEvent = hasActionKey() && getActionKey().getKeyModifier().isHeldDown(event);
		toggleDisplaySource(invokedByKeyEvent);
	}

	protected final void toggleDisplaySource(boolean invokedByKeyEvent) {
		if (getNameToOpen().equals(getName())) {
			showDisplaySource(invokedByKeyEvent);
		} else {
			closeDisplaySource(invokedByKeyEvent);
		}
	}

	protected final void showDisplaySource(boolean invokedByKeyEvent) {
		if (canToggleDisplaySource(invokedByKeyEvent) && canShowDisplaySource(invokedByKeyEvent)) {
			doShowDisplaySource();
			suppressToggleUntilKeyModifierRelease = invokedByKeyEvent;
		}
	}

	protected void doShowDisplaySource() {
		getDisplaySource().show();
	}

	protected final void closeDisplaySource(boolean invokedByKeyEvent) {
		if (canToggleDisplaySource(invokedByKeyEvent) && canCloseDisplaySource(invokedByKeyEvent)) {
			doCloseDisplaySource();
			suppressToggleUntilKeyModifierRelease = invokedByKeyEvent;
			if (invokedByKeyEvent) {
				getAmstradPc().getKeyboard().getController().sendKeyboardEventsToComputer(false);
			}
		}
	}

	protected void doCloseDisplaySource() {
		getDisplaySource().close();
	}

	protected boolean canShowDisplaySource(boolean invokedByKeyEvent) {
		return true; // Subclasses may override this method to add extra conditions
	}

	protected boolean canCloseDisplaySource(boolean invokedByKeyEvent) {
		return true; // Subclasses may override this method to add extra conditions
	}

	private boolean canToggleDisplaySource(boolean invokedByKeyEvent) {
		if (!isEnabled())
			return false;
		if (getDisplaySource() == null)
			return false;
		if (invokedByKeyEvent && suppressToggleUntilKeyModifierRelease)
			return false;
		return true;
	}

	protected void updateDisplaySource() {
		if (isDisplaySourceShowing()) {
			doShowDisplaySource();
		}
	}

	private void updateName() {
		if (isDisplaySourceShowing()) {
			changeName(getNameToClose());
		} else {
			changeName(getNameToOpen());
		}
	}

	protected abstract boolean isDisplaySourceShowing();

	protected abstract AmstradAlternativeDisplaySource getDisplaySource();

	protected boolean hasActionKey() {
		return getActionKey() != null;
	}

	public String getNameToOpen() {
		return nameToOpen;
	}

	public void setNameToOpen(String nameToOpen) {
		this.nameToOpen = nameToOpen;
		updateName();
	}

	public String getNameToClose() {
		return nameToClose;
	}

	public void setNameToClose(String nameToClose) {
		this.nameToClose = nameToClose;
		updateName();
	}

	protected ToggleActionKey getActionKey() {
		return actionKey;
	}

	protected static class ToggleActionKey {

		/**
		 * A constant number from {@link KeyEvent}
		 */
		private int keyCode;

		private KeyModifier keyModifier;

		public ToggleActionKey(int keyCode, KeyModifier keyModifier) {
			if (keyModifier == null)
				throw new NullPointerException("Action key must have a key modifier");
			this.keyCode = keyCode;
			this.keyModifier = keyModifier;
		}

		public int getKeyCode() {
			return keyCode;
		}

		public KeyModifier getKeyModifier() {
			return keyModifier;
		}

	}

	protected static abstract class KeyModifier {

		protected abstract boolean isHeldDown(ActionEvent event);

		protected abstract boolean matches(AmstradKeyboardEvent event);

	}

	private static class CtrlKeyModifier extends KeyModifier {

		@Override
		protected boolean isHeldDown(ActionEvent event) {
			return (event.getModifiers() & ActionEvent.CTRL_MASK) > 0;
		}

		@Override
		protected boolean matches(AmstradKeyboardEvent event) {
			return event.isControlDown() && !event.isShiftDown() && !event.isAltDown();
		}

	}

	private static class CtrlShiftKeyModifier extends KeyModifier {

		@Override
		protected boolean isHeldDown(ActionEvent event) {
			return (event.getModifiers() & (ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK)) > 0;
		}

		@Override
		protected boolean matches(AmstradKeyboardEvent event) {
			return event.isControlDown() && event.isShiftDown() && !event.isAltDown();
		}

	}

}