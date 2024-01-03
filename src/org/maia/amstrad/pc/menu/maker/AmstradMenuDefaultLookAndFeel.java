package org.maia.amstrad.pc.menu.maker;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import org.maia.amstrad.pc.menu.AmstradMenuBar;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;

public class AmstradMenuDefaultLookAndFeel extends AmstradMenuLookAndFeel {

	private static Color PopupMenu_background;

	private static Color PopupMenu_foreground;

	private static Color Menu_selectionBackground;

	private static Color Menu_selectionForeground;

	private static Icon Menu_arrowIcon;

	private static Color MenuItem_selectionBackground;

	private static Color MenuItem_selectionForeground;

	private static Color CheckBoxMenuItem_selectionBackground;

	private static Color CheckBoxMenuItem_selectionForeground;

	private static Icon CheckBoxMenuItem_checkIcon;

	private static Color RadioButtonMenuItem_selectionBackground;

	private static Color RadioButtonMenuItem_selectionForeground;

	private static Icon RadioButtonMenuItem_checkIcon;

	static {
		PopupMenu_background = (Color) UIManager.get("PopupMenu.background");
		PopupMenu_foreground = (Color) UIManager.get("PopupMenu.foreground");
		Menu_selectionBackground = (Color) UIManager.get("Menu.selectionBackground");
		Menu_selectionForeground = (Color) UIManager.get("Menu.selectionForeground");
		Menu_arrowIcon = (Icon) UIManager.get("Menu.arrowIcon");
		MenuItem_selectionBackground = (Color) UIManager.get("MenuItem.selectionBackground");
		MenuItem_selectionForeground = (Color) UIManager.get("MenuItem.selectionForeground");
		CheckBoxMenuItem_selectionBackground = (Color) UIManager.get("CheckBoxMenuItem.selectionBackground");
		CheckBoxMenuItem_selectionForeground = (Color) UIManager.get("CheckBoxMenuItem.selectionForeground");
		CheckBoxMenuItem_checkIcon = (Icon) UIManager.get("CheckBoxMenuItem.checkIcon");
		RadioButtonMenuItem_selectionBackground = (Color) UIManager.get("RadioButtonMenuItem.selectionBackground");
		RadioButtonMenuItem_selectionForeground = (Color) UIManager.get("RadioButtonMenuItem.selectionForeground");
		RadioButtonMenuItem_checkIcon = (Icon) UIManager.get("RadioButtonMenuItem.checkIcon");
	}

	public AmstradMenuDefaultLookAndFeel() {
	}

	@Override
	public void applySystemWide() {
		UIManager.put("PopupMenu.background", PopupMenu_background);
		UIManager.put("PopupMenu.foreground", PopupMenu_foreground);
		UIManager.put("Menu.selectionBackground", Menu_selectionBackground);
		UIManager.put("Menu.selectionForeground", Menu_selectionForeground);
		UIManager.put("Menu.arrowIcon", Menu_arrowIcon);
		UIManager.put("MenuItem.selectionBackground", MenuItem_selectionBackground);
		UIManager.put("MenuItem.selectionForeground", MenuItem_selectionForeground);
		UIManager.put("CheckBoxMenuItem.selectionBackground", CheckBoxMenuItem_selectionBackground);
		UIManager.put("CheckBoxMenuItem.selectionForeground", CheckBoxMenuItem_selectionForeground);
		UIManager.put("CheckBoxMenuItem.checkIcon", CheckBoxMenuItem_checkIcon);
		UIManager.put("RadioButtonMenuItem.selectionBackground", RadioButtonMenuItem_selectionBackground);
		UIManager.put("RadioButtonMenuItem.selectionForeground", RadioButtonMenuItem_selectionForeground);
		UIManager.put("RadioButtonMenuItem.checkIcon", RadioButtonMenuItem_checkIcon);
	}

	@Override
	public void applyToMenuBar(AmstradMenuBar menuBar) {
		// nothing special
	}

	@Override
	public void applyToPopupMenu(AmstradPopupMenu popupMenu) {
		// nothing special
	}

	@Override
	public void applyToMenuItem(JMenuItem item, Icon icon) {
		// nothing special
	}

}