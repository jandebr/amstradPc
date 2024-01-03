package org.maia.amstrad.system;

import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySource;

public interface AmstradSystemScreen {

	boolean isNativeScreen();

	boolean isUnknownScreen();

	boolean isAutohideControlKeys();

	String getScreenIdentifier();

	/**
	 * Returns the custom display source
	 * 
	 * @return The custom display source, or <code>null</code> when this screen is either <em>native</em> or
	 *         <em>unknown</em>
	 * 
	 * @see #isNativeScreen()
	 * @see #isUnknownScreen()
	 */
	AmstradAlternativeDisplaySource getCustomDisplaySource();

	/**
	 * Returns the popup menu for this screen, if any
	 * 
	 * @return The popup menu, or <code>null</code> when none
	 */
	AmstradPopupMenu getPopupMenu();

}