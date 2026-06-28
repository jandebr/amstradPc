package org.maia.amstrad.gui.browser;

public enum ProgramBrowserStartupAnimationControl {

	/**
	 * Always show a startup animation
	 */
	ALWAYS("Always"),

	/**
	 * Never show a startup animation
	 */
	NEVER("Never"),

	/**
	 * Show a startup animation when the browser initialization takes longer than the animation's configured delay
	 */
	DELAYED("After delay");

	private String displayName;

	private ProgramBrowserStartupAnimationControl(String displayName) {
		this.displayName = displayName;
	}

	public static ProgramBrowserStartupAnimationControl withDisplayName(String displayName) {
		for (ProgramBrowserStartupAnimationControl control : ProgramBrowserStartupAnimationControl.values()) {
			if (control.getDisplayName().equals(displayName))
				return control;
		}
		return null;
	}

	public String getDisplayName() {
		return displayName;
	}

}