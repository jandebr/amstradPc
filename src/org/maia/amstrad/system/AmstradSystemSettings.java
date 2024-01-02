package org.maia.amstrad.system;

import org.maia.amstrad.pc.tape.AmstradTape;

public interface AmstradSystemSettings {

	/**
	 * Tells whether the program browser is the central and start screen at launch
	 * 
	 * @return <code>true</code> iff the program browser is centric
	 */
	boolean isProgramBrowserCentric();

	/**
	 * Tells whether fullscreen toggling is enabled
	 * 
	 * @return <code>true</code> iff fullscreen can be toggled
	 */
	boolean isFullscreenToggleEnabled();

	/**
	 * Tells whether the original Jemu menu is to be shown
	 * 
	 * @return <code>true</code> iff the original Jemu menu is to be shown
	 */
	boolean isUsingOriginalJemuMenu();

	/**
	 * Tells whether visual indications for tape activity are to be shown
	 * 
	 * @return <code>true</code> iff tape activity is to be shown
	 * 
	 * @see AmstradTape#isActive()
	 */
	boolean isTapeActivityShown();

	/**
	 * Tells whether the source code of programs is accessible
	 * 
	 * @return <code>true</code> iff the source code is accessible
	 */
	boolean isProgramSourceCodeAccessible();

	/**
	 * Tells whether program authoring tools are available
	 * 
	 * @return <code>true</code> iff program authoring tools are available
	 */
	boolean isProgramAuthoringToolsAvailable();

}