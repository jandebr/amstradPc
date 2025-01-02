package org.maia.amstrad.gui.browser.classic;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradSettings;

public class ClassicProgramBrowserThemeFromSettings implements ClassicProgramBrowserTheme {

	private static final String SETTING_PREFIX = "program_browser.classic.theme.";

	private static final String SETTING_MAIN_WINDOW_BORDER_INK = SETTING_PREFIX + "window.main.border.ink";

	private static final String SETTING_MAIN_WINDOW_BACKGROUND_INK = SETTING_PREFIX + "window.main.background.ink";

	private static final String SETTING_MAIN_WINDOW_TITLE_INK = SETTING_PREFIX + "window.main.title.ink";

	private static final String SETTING_MODAL_WINDOW_BORDER_INK = SETTING_PREFIX + "window.modal.border.ink";

	private static final String SETTING_MODAL_WINDOW_BACKGROUND_INK = SETTING_PREFIX + "window.modal.background.ink";

	private static final String SETTING_MODAL_WINDOW_TITLE_INK = SETTING_PREFIX + "window.modal.title.ink";

	private static final String SETTING_FOLDER_ENTRY_INK = SETTING_PREFIX + "entry.folder.ink";

	private static final String SETTING_PROGRAM_ENTRY_INK = SETTING_PREFIX + "entry.program.ink";

	private static final String SETTING_EMPTY_ENTRY_INK = SETTING_PREFIX + "entry.empty.ink";

	private static final String SETTING_ENTRY_HIGHLIGHT_INK = SETTING_PREFIX + "entry.highlight.ink";

	private static final String SETTING_FOCUS_ENTRY_HIGHLIGHT_INK = SETTING_PREFIX + "entry.highlight_focus.ink";

	private static final String SETTING_ENTRY_CURSOR_INK = SETTING_PREFIX + "entry.cursor.ink";

	private static final String SETTING_DISABLED_MENUITEM_INK = SETTING_PREFIX + "menu_item.disabled.ink";

	private static final String SETTING_EXTENT_HINT_INK = SETTING_PREFIX + "extent_hint.ink";

	public ClassicProgramBrowserThemeFromSettings() {
	}

	@Override
	public int getMainWindowBorderInk() {
		return getInkSetting(SETTING_MAIN_WINDOW_BORDER_INK, 1);
	}

	@Override
	public int getMainWindowBackgroundInk() {
		return getInkSetting(SETTING_MAIN_WINDOW_BACKGROUND_INK, 1);
	}

	@Override
	public int getMainWindowTitleInk() {
		return getInkSetting(SETTING_MAIN_WINDOW_TITLE_INK, 23);
	}

	@Override
	public int getModalWindowBorderInk() {
		return getInkSetting(SETTING_MODAL_WINDOW_BORDER_INK, 14);
	}

	@Override
	public int getModalWindowBackgroundInk() {
		return getInkSetting(SETTING_MODAL_WINDOW_BACKGROUND_INK, 0);
	}

	@Override
	public int getModalWindowTitleInk() {
		return getInkSetting(SETTING_MODAL_WINDOW_TITLE_INK, 23);
	}

	@Override
	public int getFolderEntryInk() {
		return getInkSetting(SETTING_FOLDER_ENTRY_INK, 25);
	}

	@Override
	public int getProgramEntryInk() {
		return getInkSetting(SETTING_PROGRAM_ENTRY_INK, 26);
	}

	@Override
	public int getEmptyEntryInk() {
		return getInkSetting(SETTING_EMPTY_ENTRY_INK, 13);
	}

	@Override
	public int getEntryHighlightInk() {
		return getInkSetting(SETTING_ENTRY_HIGHLIGHT_INK, 3);
	}

	@Override
	public int getFocusEntryHighlightInk() {
		return getInkSetting(SETTING_FOCUS_ENTRY_HIGHLIGHT_INK, 2);
	}

	@Override
	public int getEntryCursorInk() {
		return getInkSetting(SETTING_ENTRY_CURSOR_INK, 24);
	}

	@Override
	public int getDisabledMenuItemInk() {
		return getInkSetting(SETTING_DISABLED_MENUITEM_INK, 13);
	}

	@Override
	public int getExtentHintInk() {
		return getInkSetting(SETTING_EXTENT_HINT_INK, 13);
	}

	private int getInkSetting(String key, int defaultInk) {
		int ink = 0;
		try {
			ink = Integer.parseInt(getSettings().get(key, String.valueOf(defaultInk)));
		} catch (NumberFormatException e) {
			System.err.println(e);
		}
		return ink;
	}

	private AmstradSettings getSettings() {
		return AmstradFactory.getInstance().getAmstradContext().getUserSettings();
	}

}