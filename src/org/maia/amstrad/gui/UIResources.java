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

	public static ImageIcon basicIcon = loadIcon("basic32.png");

	public static ImageIcon browserIcon = loadIcon("browser32.png");

	public static Icon basicOrBrowserIcon = new BasicOrBrowserIcon(basicIcon, browserIcon);

	public static ImageIcon browserSetupIcon = loadIcon("browser-setup32.png");

	public static ImageIcon pauseIcon = loadIcon("pause32.png");

	public static ImageIcon resumeIcon = loadIcon("resume32.png");

	public static Icon pauseResumeIcon = new PauseResumeIcon(pauseIcon, resumeIcon);

	public static ImageIcon infoIcon = loadIcon("info32.png");

	public static ImageIcon screenshotIcon = loadIcon("photo32.png");

	public static ImageIcon screenshotWithMonitorIcon = loadIcon("photo-monitor32.png");

	public static ImageIcon monitorModeIcon = loadIcon("monitor-mode32.png");

	public static ImageIcon monitorEffectIcon = loadIcon("monitor-effect32.png");

	public static ImageIcon menuArrowIcon = loadIcon("menu-arrowh-right12x22.png");

	public static Icon checkBoxMenuItemIcon = new ButtonStateIcon(loadIcon("unchecked32.png"),
			loadIcon("checked32.png"));

	public static Icon radioButtonMenuItemIcon = new ButtonStateIcon(loadIcon("unselected32.png"),
			loadIcon("selected32.png"));

	public static ImageIcon quitIcon = loadIcon("poweroff32.png");

	public static ImageIcon pauseOverlayIcon = loadIcon("overlay/pause64.png");

	public static ImageIcon pauseSmallOverlayIcon = loadIcon("overlay/pause32.png");

	public static ImageIcon autotypeOverlayIcon = loadIcon("overlay/autotype64.png");

	public static ImageIcon autotypeSmallOverlayIcon = loadIcon("overlay/autotype32.png");

	public static ImageIcon tapeOverlayIcon = loadIcon("overlay/tape64.png");

	public static ImageIcon tapeSmallOverlayIcon = loadIcon("overlay/tape32.png");

	public static ImageIcon tapeReadOverlayIcon = loadIcon("overlay/play22.png");

	public static ImageIcon tapeWriteOverlayIcon = loadIcon("overlay/record22.png");

	private static ImageIcon loadIcon(String resourceName) {
		ImageIcon icon = null;
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