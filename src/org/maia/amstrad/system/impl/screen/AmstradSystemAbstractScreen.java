package org.maia.amstrad.system.impl.screen;

import java.util.Objects;

import org.maia.amstrad.gui.overlay.ControlKeysDisplayOverlay;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemScreen;

public abstract class AmstradSystemAbstractScreen implements AmstradSystemScreen {

	private String screenIdentifier;

	private AmstradSystem amstradSystem;

	private AmstradPopupMenu popupMenu;

	protected AmstradSystemAbstractScreen(String screenIdentifier, AmstradSystem amstradSystem) {
		if (screenIdentifier == null)
			throw new NullPointerException("Screen identifier is null");
		if (amstradSystem == null)
			throw new NullPointerException("Amstrad system is null");
		this.screenIdentifier = screenIdentifier;
		this.amstradSystem = amstradSystem;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getScreenIdentifier());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmstradSystemAbstractScreen other = (AmstradSystemAbstractScreen) obj;
		return Objects.equals(getScreenIdentifier(), other.getScreenIdentifier());
	}

	@Override
	public boolean isAutohideControlKeys() {
		return isDefaultAutohideControlKeys();
	}

	protected boolean isDefaultAutohideControlKeys() {
		return getAmstradSystem().getAmstradContext().getUserSettings().getBool(
				ControlKeysDisplayOverlay.SETTING_AUTOHIDE_CONTROLKEYS,
				ControlKeysDisplayOverlay.DEFAULT_AUTOHIDE_CONTROLKEYS);
	}

	@Override
	public String getScreenIdentifier() {
		return screenIdentifier;
	}

	@Override
	public AmstradPopupMenu getPopupMenu() {
		return popupMenu;
	}

	public void setPopupMenu(AmstradPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
	}

	protected AmstradSystem getAmstradSystem() {
		return amstradSystem;
	}

}
