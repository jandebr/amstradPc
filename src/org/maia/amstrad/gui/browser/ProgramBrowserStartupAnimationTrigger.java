package org.maia.amstrad.gui.browser;

public enum ProgramBrowserStartupAnimationTrigger {

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

	private ProgramBrowserStartupAnimationTrigger(String displayName) {
		this.displayName = displayName;
	}

	public static ProgramBrowserStartupAnimationTrigger withDisplayName(String displayName) {
		for (ProgramBrowserStartupAnimationTrigger trigger : ProgramBrowserStartupAnimationTrigger.values()) {
			if (trigger.getDisplayName().equals(displayName))
				return trigger;
		}
		return null;
	}

	public String getDisplayName() {
		return displayName;
	}

}