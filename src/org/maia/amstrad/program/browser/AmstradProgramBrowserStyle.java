package org.maia.amstrad.program.browser;

public enum AmstradProgramBrowserStyle {

	CLASSIC("Classic"),

	CAROUSEL("Carousel");

	private String displayName;

	private AmstradProgramBrowserStyle(String displayName) {
		this.displayName = displayName;
	}

	public static AmstradProgramBrowserStyle forDisplayNameIgnoreCase(String displayName) {
		for (AmstradProgramBrowserStyle style : AmstradProgramBrowserStyle.values()) {
			if (style.getDisplayName().equalsIgnoreCase(displayName))
				return style;
		}
		return null;
	}

	public String getDisplayName() {
		return displayName;
	}

}