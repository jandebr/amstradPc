package org.maia.amstrad.pc.menu.maker;

import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.menu.AmstradMenuBar;
import org.maia.amstrad.pc.menu.AmstradPopupMenu;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradGraphicsContext;
import org.maia.amstrad.pc.monitor.display.AmstradSystemColors;

public class AmstradMenuEmulatedLookAndFeel extends AmstradMenuLookAndFeel {

	private AmstradGraphicsContext graphicsContext;

	private Font menuItemFont;

	private static final int EMULATOR_LAF_COLOR_BACKGROUND = 0;

	private static final int EMULATOR_LAF_COLOR_FOREGROUND = 26;

	private static final int EMULATOR_LAF_COLOR_BORDER = 3;

	private static final int EMULATOR_LAF_COLOR_SELECTION_BG = 3;

	private static final int EMULATOR_LAF_COLOR_SELECTION_FG = 25;

	public AmstradMenuEmulatedLookAndFeel(AmstradGraphicsContext graphicsContext) {
		this.graphicsContext = graphicsContext;
	}

	@Override
	public void applySystemWide() {
		UIManager.put("PopupMenu.background", getSystemColors().getColor(EMULATOR_LAF_COLOR_BACKGROUND));
		UIManager.put("PopupMenu.foreground", getSystemColors().getColor(EMULATOR_LAF_COLOR_FOREGROUND));
		UIManager.put("Menu.selectionBackground", getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_BG));
		UIManager.put("Menu.selectionForeground", getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_FG));
		UIManager.put("Menu.arrowIcon", UIResources.menuArrowIcon);
		UIManager.put("MenuItem.selectionBackground", getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_BG));
		UIManager.put("MenuItem.selectionForeground", getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_FG));
		UIManager.put("CheckBoxMenuItem.selectionBackground",
				getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_BG));
		UIManager.put("CheckBoxMenuItem.selectionForeground",
				getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_FG));
		UIManager.put("CheckBoxMenuItem.checkIcon", UIResources.checkBoxMenuItemIcon);
		UIManager.put("RadioButtonMenuItem.selectionBackground",
				getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_BG));
		UIManager.put("RadioButtonMenuItem.selectionForeground",
				getSystemColors().getColor(EMULATOR_LAF_COLOR_SELECTION_FG));
		UIManager.put("RadioButtonMenuItem.checkIcon", UIResources.radioButtonMenuItemIcon);
	}

	@Override
	public void applyToMenuBar(AmstradMenuBar menuBar) {
		// nothing special
	}

	@Override
	public void applyToPopupMenu(AmstradPopupMenu popupMenu) {
		popupMenu.setBackground(getSystemColors().getColor(EMULATOR_LAF_COLOR_BACKGROUND));
		popupMenu.setForeground(getSystemColors().getColor(EMULATOR_LAF_COLOR_FOREGROUND));
		popupMenu.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(getSystemColors().getColor(EMULATOR_LAF_COLOR_BACKGROUND), 1),
				BorderFactory.createLineBorder(getSystemColors().getColor(EMULATOR_LAF_COLOR_BORDER), 4)));
		popupMenu.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void applyToMenuItem(JMenuItem item, Icon icon) {
		item.setBackground(getSystemColors().getColor(EMULATOR_LAF_COLOR_BACKGROUND));
		item.setForeground(getSystemColors().getColor(EMULATOR_LAF_COLOR_FOREGROUND));
		item.setFont(getMenuItemFont());
		item.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
		item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (icon != null) {
			item.setIcon(icon);
		}
	}

	private AmstradSystemColors getSystemColors() {
		return AmstradSystemColors.getSystemColors(AmstradMonitorMode.COLOR);
	}

	private Font getMenuItemFont() {
		if (menuItemFont == null) {
			menuItemFont = getGraphicsContext().getSystemFont().deriveFont(16f);
		}
		return menuItemFont;
	}

	private AmstradGraphicsContext getGraphicsContext() {
		return graphicsContext;
	}

}