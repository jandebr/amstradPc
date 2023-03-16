package org.maia.amstrad.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.maia.amstrad.gui.browser.action.ProgramBrowserAction;
import org.maia.amstrad.pc.action.PauseResumeAction;

public class UIResources {

	public static Icon basicIcon = loadIcon("basic32.png");

	public static Icon browserIcon = loadIcon("browser32.png");

	public static Icon basicOrBrowserIcon = new BasicOrBrowserIcon(basicIcon, browserIcon);

	public static Icon browserSetupIcon = loadIcon("browser-setup32.png");

	public static Icon pauseIcon = loadIcon("pause32.png");

	public static Icon resumeIcon = loadIcon("resume32.png");

	public static Icon pauseResumeIcon = new PauseResumeIcon(pauseIcon, resumeIcon);

	public static Icon infoIcon = loadIcon("info32.png");

	public static Icon screenshotIcon = loadIcon("photo32.png");

	public static Icon screenshotWithMonitorIcon = loadIcon("photo-monitor32.png");

	public static Icon monitorModeIcon = loadIcon("monitor-mode32.png");

	public static Icon monitorEffectIcon = loadIcon("monitor-effect32.png");

	public static Icon menuArrowIcon = loadIcon("menu-arrowh-right12x22.png");

	public static Icon checkBoxMenuItemIcon = new ButtonStateIcon(loadIcon("unchecked32.png"),
			loadIcon("checked32.png"));

	public static Icon radioButtonMenuItemIcon = new ButtonStateIcon(loadIcon("unselected32.png"),
			loadIcon("selected32.png"));

	public static Icon quitIcon = loadIcon("poweroff32.png");

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

		private Icon unselectedIcon;

		private Icon selectedIcon;

		public ButtonStateIcon(Icon unselectedIcon, Icon selectedIcon) {
			this.unselectedIcon = unselectedIcon;
			this.selectedIcon = selectedIcon;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			if (isSelected(c)) {
				getSelectedIcon().paintIcon(c, g, x, y);
			} else {
				getUnselectedIcon().paintIcon(c, g, x, y);
			}
		}

		protected boolean isSelected(Component c) {
			return ((AbstractButton) c).getModel().isSelected();
		}

		@Override
		public int getIconWidth() {
			return getSelectedIcon().getIconWidth();
		}

		@Override
		public int getIconHeight() {
			return getSelectedIcon().getIconHeight();
		}

		private Icon getUnselectedIcon() {
			return unselectedIcon;
		}

		private Icon getSelectedIcon() {
			return selectedIcon;
		}

	}

	private static class PauseResumeIcon extends ButtonStateIcon {

		public PauseResumeIcon(Icon pauseIcon, Icon resumeIcon) {
			super(pauseIcon, resumeIcon);
		}

		@Override
		protected boolean isSelected(Component c) {
			Action a = ((JMenuItem) c).getAction();
			return ((PauseResumeAction) a).isPaused();
		}

	}

	private static class BasicOrBrowserIcon extends ButtonStateIcon {

		public BasicOrBrowserIcon(Icon basicIcon, Icon browserIcon) {
			super(basicIcon, browserIcon);
		}

		@Override
		protected boolean isSelected(Component c) {
			Action a = ((JMenuItem) c).getAction();
			return !((ProgramBrowserAction) a).isProgramBrowserShowing();
		}

	}

}