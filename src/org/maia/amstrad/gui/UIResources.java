package org.maia.amstrad.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class UIResources {

	public static Icon pauseIcon = loadIcon("pause32.png");

	public static Icon quitIcon = loadIcon("poweroff32.png");

	public static Icon menuArrowIcon = loadIcon("menu-arrowh-right12x22.png");

	public static Icon checkBoxMenuItemIcon = new ButtonStateIcon(loadIcon("checked32.png"),
			loadIcon("unchecked32.png"));

	public static Icon radioButtonMenuItemIcon = new ButtonStateIcon(loadIcon("selected32.png"),
			loadIcon("unselected32.png"));

	private static Icon loadIcon(String resourceName) {
		Icon icon = null;
		try {
			InputStream in = UIResources.class.getResourceAsStream("icons/" + resourceName);
			icon = new ImageIcon(ImageIO.read(in));
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return icon;
	}

	private static class ButtonStateIcon implements Icon {

		private Icon selectedIcon;

		private Icon unselectedIcon;

		public ButtonStateIcon(Icon selectedIcon, Icon unselectedIcon) {
			this.selectedIcon = selectedIcon;
			this.unselectedIcon = unselectedIcon;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			boolean selected = ((AbstractButton) c).getModel().isSelected();
			if (selected) {
				getSelectedIcon().paintIcon(c, g, x, y);
			} else {
				getUnselectedIcon().paintIcon(c, g, x, y);
			}
		}

		@Override
		public int getIconWidth() {
			return getSelectedIcon().getIconWidth();
		}

		@Override
		public int getIconHeight() {
			return getSelectedIcon().getIconHeight();
		}

		private Icon getSelectedIcon() {
			return selectedIcon;
		}

		private Icon getUnselectedIcon() {
			return unselectedIcon;
		}

	}

}