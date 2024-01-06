package org.maia.amstrad.system.impl.screen;

import java.util.Objects;

import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.system.AmstradSystem;
import org.maia.amstrad.system.AmstradSystemScreen;

public abstract class AmstradSystemAbstractScreen implements AmstradSystemScreen {

	private String screenIdentifier;

	private AmstradSystem amstradSystem;

	private AmstradPopupMenu popupMenu;

	private boolean showPause;

	private boolean showControlKeys;

	private boolean autohideControlKeys;

	private boolean showTapeActivity;

	protected AmstradSystemAbstractScreen(String screenIdentifier, AmstradSystem amstradSystem) {
		if (screenIdentifier == null)
			throw new NullPointerException("Screen identifier is null");
		if (amstradSystem == null)
			throw new NullPointerException("Amstrad system is null");
		this.screenIdentifier = screenIdentifier;
		this.amstradSystem = amstradSystem;
		// Defaults
		this.showPause = true;
		this.showControlKeys = false;
		this.autohideControlKeys = true;
		this.showTapeActivity = true;
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

	@Override
	public boolean isShowPause() {
		return showPause;
	}

	public void setShowPause(boolean show) {
		this.showPause = show;
	}

	@Override
	public boolean isShowControlKeys() {
		return showControlKeys;
	}

	public void setShowControlKeys(boolean show) {
		this.showControlKeys = show;
	}

	@Override
	public boolean isAutohideControlKeys() {
		return autohideControlKeys;
	}

	public void setAutohideControlKeys(boolean autohide) {
		this.autohideControlKeys = autohide;
	}

	@Override
	public boolean isShowTapeActivity() {
		return showTapeActivity;
	}

	public void setShowTapeActivity(boolean show) {
		this.showTapeActivity = show;
	}

}