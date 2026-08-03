package org.maia.amstrad.system;

import java.util.List;

import org.maia.amstrad.gui.overlay.controlkeys.ControlKey;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.pc.tape.AmstradTape;

public interface AmstradSystemScreen {

	boolean isNativeScreen();

	boolean isUnknownScreen();

	String getScreenIdentifier();

	/**
	 * Returns the type of custom display source this screen is showing
	 * 
	 * @return The custom display source type, or <code>null</code> when this screen is either <em>native</em> or
	 *         <em>unknown</em>
	 * 
	 * @see #isNativeScreen()
	 * @see #isUnknownScreen()
	 */
	AmstradAlternativeDisplaySourceType getCustomDisplaySourceType();

	/**
	 * Returns the popup menu for this screen, if any
	 * 
	 * @return The popup menu, or <code>null</code> when none
	 */
	AmstradPopupMenu getPopupMenu();

	/**
	 * Returns any additional control keys (i.e., key shortcuts) for this screen
	 * 
	 * @return List of additional control keys, <code>null</code> or the empty list when none
	 * @see #isShowControlKeys()
	 */
	List<ControlKey> getAdditionalControlKeys();

	/**
	 * Tells whether to show the monitor for this screen
	 * 
	 * @return <code>true</code> iff the monitor is to be shown
	 */
	boolean isShowMonitor();

	/**
	 * Tells whether the monitor size can be adjusted in this screen
	 * 
	 * @return <code>true</code> iff the monitor can be resized
	 */
	boolean isMonitorResizable();

	/**
	 * Tells whether the paused state is to be shown on screen
	 * 
	 * @return <code>true</code> iff paused state is to be shown
	 */
	boolean isShowPause();

	/**
	 * Tells whether turbo mode is to be shown on screen
	 * 
	 * @return <code>true</code> iff turbo mode is to be shown
	 */
	boolean isShowTurbo();

	/**
	 * Tells whether control keys are to be shown on screen
	 * 
	 * @return <code>true</code> iff control keys are to be shown
	 */
	boolean isShowControlKeys();

	/**
	 * Tells whether control keys are automatically hidden after some time or some event
	 * 
	 * <p>
	 * This setting is only relevant when {@link #isShowControlKeys()} return <code>true</code>
	 * </p>
	 * 
	 * @return <code>true</code> iff control keys are hidden automatically
	 */
	boolean isAutohideControlKeys();

	/**
	 * Tells whether visual indications for tape activity are to be shown on screen
	 * 
	 * @return <code>true</code> iff tape activity is to be shown
	 * 
	 * @see AmstradTape#isActive()
	 */
	boolean isShowTapeActivity();

	/**
	 * Tells whether visual indications for automatic typing are to be shown on screen
	 * 
	 * @return <code>true</code> iff automatic typing is to be shown
	 * 
	 * @see AmstradKeyboard#isAutotyping()
	 */
	boolean isShowAutotype();

}