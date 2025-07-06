package org.maia.amstrad.pc.joystick;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcDevice;
import org.maia.util.GenericListenerList;
import org.maia.util.SystemUtils;

import jemu.settings.Settings;

public abstract class AmstradJoystick extends AmstradPcDevice {

	private AmstradJoystickID joystickId;

	private boolean active;

	private boolean autoRepeatEnabled;

	private GenericListenerList<AmstradJoystickStateListener> joystickStateListeners;

	private GenericListenerList<AmstradJoystickEventListener> joystickEventListeners;

	private AmstradJoystickController controller;

	protected AmstradJoystick(AmstradPc amstradPc, AmstradJoystickID joystickId) {
		super(amstradPc);
		this.joystickId = joystickId;
		this.joystickStateListeners = new GenericListenerList<AmstradJoystickStateListener>();
		this.joystickEventListeners = new GenericListenerList<AmstradJoystickEventListener>();
		this.controller = createController();
		switchActiveState(getInitialActiveState());
	}

	protected AmstradJoystickController createController() {
		return new AmstradJoystickController(this);
	}

	public abstract void showSetupDialog();

	public abstract boolean isConnected();

	public final void switchActiveState(boolean active) {
		if (active) {
			activate();
		} else {
			deactivate();
		}
	}

	public void activate() {
		if (!isActive()) {
			setActive(true);
			Settings.setBoolean(getSettingsKeyForActiveState(), true);
			doActivate();
			fireJoystickActivated();
		}
	}

	protected abstract void doActivate();

	public void deactivate() {
		if (isActive()) {
			setActive(false);
			Settings.setBoolean(getSettingsKeyForActiveState(), false);
			doDeactivate();
			fireJoystickDeactivated();
		}
	}

	protected abstract void doDeactivate();

	public void activateDelayed(long delayInMillis) {
		new Thread() {

			@Override
			public void run() {
				SystemUtils.sleep(delayInMillis);
				activate();
			}

		}.start();
	}

	public void switchAutoRepeatEnabled(boolean autoRepeatEnabled) {
		if (autoRepeatEnabled != isAutoRepeatEnabled()) {
			setAutoRepeatEnabled(autoRepeatEnabled);
			doSwitchAutoRepeatEnabled(autoRepeatEnabled);
		}
	}

	protected abstract void doSwitchAutoRepeatEnabled(boolean autoRepeatEnabled);

	public void addJoystickStateListener(AmstradJoystickStateListener listener) {
		getJoystickStateListeners().addListener(listener);
	}

	public void removeJoystickStateListener(AmstradJoystickStateListener listener) {
		getJoystickStateListeners().removeListener(listener);
	}

	public void addJoystickEventListener(AmstradJoystickEventListener listener) {
		getJoystickEventListeners().addListener(listener);
	}

	public void removeJoystickEventListener(AmstradJoystickEventListener listener) {
		getJoystickEventListeners().removeListener(listener);
	}

	protected void fireJoystickConnected() {
		for (AmstradJoystickStateListener listener : getJoystickStateListeners())
			listener.amstradJoystickConnected(this);
	}

	protected void fireJoystickDisconnected() {
		for (AmstradJoystickStateListener listener : getJoystickStateListeners())
			listener.amstradJoystickDisconnected(this);
	}

	protected void fireJoystickActivated() {
		for (AmstradJoystickStateListener listener : getJoystickStateListeners())
			listener.amstradJoystickActivated(this);
	}

	protected void fireJoystickDeactivated() {
		for (AmstradJoystickStateListener listener : getJoystickStateListeners())
			listener.amstradJoystickDeactivated(this);
	}

	protected void fireJoystickEvent(AmstradJoystickEvent event) {
		for (AmstradJoystickEventListener listener : getJoystickEventListeners())
			listener.amstradJoystickEventDispatched(event);
	}

	protected boolean getInitialActiveState() {
		return Settings.getBoolean(getSettingsKeyForActiveState(), false);
	}

	private String getSettingsKeyForActiveState() {
		return Settings.JOYSTICK + "." + getJoystickId().getIdentifier();
	}

	public boolean isPrimaryJoystick() {
		return AmstradJoystickID.JOYSTICK0.equals(getJoystickId());
	}

	public boolean isSecondaryJoystick() {
		return AmstradJoystickID.JOYSTICK1.equals(getJoystickId());
	}

	public AmstradJoystickID getJoystickId() {
		return joystickId;
	}

	public boolean isActive() {
		return active;
	}

	private void setActive(boolean active) {
		this.active = active;
	}

	public boolean isAutoRepeatEnabled() {
		return autoRepeatEnabled;
	}

	private void setAutoRepeatEnabled(boolean autoRepeatEnabled) {
		this.autoRepeatEnabled = autoRepeatEnabled;
	}

	protected GenericListenerList<AmstradJoystickStateListener> getJoystickStateListeners() {
		return joystickStateListeners;
	}

	protected GenericListenerList<AmstradJoystickEventListener> getJoystickEventListeners() {
		return joystickEventListeners;
	}

	public AmstradJoystickController getController() {
		return controller;
	}

}