package org.maia.amstrad.system;

public interface AmstradSystemSettings {

	/**
	 * Tells whether to launch with an inventory of programs that can be run or played
	 * 
	 * @return <code>true</code> iff program centric
	 */
	boolean isProgramCentric();

	/**
	 * Tells whether to launch in fullscreen or windowed
	 * 
	 * @return <code>true</code> when launching in fullscreen, <code>false</code> when windowed
	 */
	boolean isLaunchInFullscreen();

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