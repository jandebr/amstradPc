package org.maia.amstrad.program.browser;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.AmstradContext;
import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.action.ProgramBrowserAction;

public class AmstradProgramBrowserStyleManager {

	private List<AmstradProgramBrowserStyle> styles;

	private static final String SETTING_PROGRAM_BROWSER_STYLE = "program_browser.style";

	public AmstradProgramBrowserStyleManager() {
		this.styles = new Vector<AmstradProgramBrowserStyle>();
	}

	public void addStyle(AmstradProgramBrowserStyle style) {
		getStyles().add(style);
	}

	public void applyStyle(AmstradProgramBrowserStyle style, AmstradPc amstradPc) {
		if (style != null && !style.equals(getStyle(amstradPc))) {
			ProgramBrowserAction browserAction = amstradPc.getActions().getProgramBrowserAction();
			if (browserAction != null) {
				AmstradProgramBrowser browser = style.createProgramBrowser(amstradPc);
				browserAction.reset(browser);
				setDefaultStyle(getCurrentMode(), style);
			}
		}
	}

	public AmstradProgramBrowserStyle getStyle(AmstradPc amstradPc) {
		AmstradProgramBrowserStyle style = null;
		ProgramBrowserAction browserAction = amstradPc.getActions().getProgramBrowserAction();
		if (browserAction != null) {
			AmstradProgramBrowser browser = browserAction.getProgramBrowser();
			if (browser != null) {
				style = browser.getStyle();
			}
		}
		return style;
	}

	public AmstradProgramBrowserStyle getDefaultStyle() {
		AmstradProgramBrowserStyle style = getDefaultStyle(getCurrentMode());
		if (style == null) {
			style = getDefaultStyle(null);
		}
		if (style == null && !getStyles().isEmpty()) {
			style = getStyles().get(0);
		}
		return style;
	}

	private AmstradProgramBrowserStyle getDefaultStyle(String mode) {
		String styleName = getAmstradContext().getUserSettings().get(getDefaultStyleSetting(mode), null);
		return getStyleForName(styleName);
	}

	private void setDefaultStyle(String mode, AmstradProgramBrowserStyle style) {
		String styleName = style.getDisplayName().toLowerCase();
		getAmstradContext().getUserSettings().set(getDefaultStyleSetting(mode), styleName);
	}

	private String getDefaultStyleSetting(String mode) {
		String setting = SETTING_PROGRAM_BROWSER_STYLE;
		if (mode != null) {
			setting += "." + mode.toLowerCase();
		}
		return setting;
	}

	private AmstradProgramBrowserStyle getStyleForName(String styleName) {
		if (styleName != null) {
			for (AmstradProgramBrowserStyle style : getStyles()) {
				if (style.getDisplayName().equalsIgnoreCase(styleName))
					return style;
			}
		}
		return null;
	}

	private String getCurrentMode() {
		return getAmstradContext().getMode();
	}

	private AmstradContext getAmstradContext() {
		return AmstradFactory.getInstance().getAmstradContext();
	}

	public List<AmstradProgramBrowserStyle> getStyles() {
		return styles;
	}

}