package org.maia.amstrad.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;

import org.maia.amstrad.pc.action.AudioAction;
import org.maia.amstrad.pc.action.MonitorFullscreenAction;
import org.maia.amstrad.pc.action.PauseResumeAction;
import org.maia.amstrad.pc.action.ProgramBrowserAction;
import org.maia.amstrad.pc.action.VirtualKeyboardAction;
import org.maia.graphics2d.image.ImageUtils;

public class UIResources {

	public static ImageIcon aboutBackdrop = loadIcon("about.png");

	public static ImageIcon cpcIcon = loadIcon("cpc24.png");

	public static ImageIcon basicIcon = loadIcon("basic32.png");

	public static ImageIcon browserIcon = loadIcon("browser32.png");

	public static Icon basicOrBrowserIcon = new BasicOrBrowserIcon(basicIcon, browserIcon);

	public static ImageIcon browserSetupIcon = loadIcon("browser-setup32.png");

	public static ImageIcon browserResetIcon = loadIcon("browser-reset32.png");

	public static ImageIcon settingsIcon = loadIcon("settings32.png");

	public static ImageIcon joystickIcon = loadIcon("joystick32.png");

	public static ImageIcon muteIcon = loadIcon("mute32.png");

	public static ImageIcon unmuteIcon = loadIcon("unmute32.png");

	public static Icon audioIcon = new AudioIcon(muteIcon, unmuteIcon);

	public static ImageIcon virtualKeyboardOffIcon = loadIcon("vkeyboard-off32.png");

	public static ImageIcon virtualKeyboardOnIcon = loadIcon("vkeyboard-on32.png");

	public static Icon virtualKeyboardIcon = new VirtualKeyboardIcon(virtualKeyboardOffIcon, virtualKeyboardOnIcon);

	public static ImageIcon pauseIcon = loadIcon("pause32.png");

	public static ImageIcon resumeIcon = loadIcon("resume32.png");

	public static Icon pauseResumeIcon = new PauseResumeIcon(pauseIcon, resumeIcon);

	public static ImageIcon fullscreenIcon = loadIcon("fullscreen32.png");

	public static ImageIcon windowedIcon = loadIcon("windowed32.png");

	public static Icon windowIcon = new WindowIcon(fullscreenIcon, windowedIcon);

	public static ImageIcon infoIcon = loadIcon("info32.png");

	public static ImageIcon screenshotIcon = loadIcon("photo32.png");

	public static ImageIcon screenshotWithBorderIcon = loadIcon("photo-border32.png");

	public static ImageIcon screenshotWithoutBorderIcon = loadIcon("photo-noborder32.png");

	public static ImageIcon screenshotWithMonitorIcon = loadIcon("photo-monitor32.png");

	public static ImageIcon monitorModeIcon = loadIcon("monitor-mode32.png");

	public static ImageIcon monitorEffectIcon = loadIcon("monitor-effect32.png");

	public static ImageIcon monitorSizeIcon = loadIcon("windowed32.png");

	public static ImageIcon menuArrowIcon = loadIcon("menu-arrowh-right12x22.png");

	public static Icon checkBoxMenuItemIcon = new ButtonStateIcon(loadIcon("unchecked32.png"),
			loadIcon("checked32.png"));

	public static Icon radioButtonMenuItemIcon = new ButtonStateIcon(loadIcon("unselected32.png"),
			loadIcon("selected32.png"));

	public static ImageIcon quitIcon = loadIcon("quit32.png");

	public static ImageIcon powerOffIcon = loadIcon("poweroff32.png");

	public static ImageIcon pauseOverlayIcon = loadIcon("overlay/pause64.png");

	public static ImageIcon pauseSmallOverlayIcon = loadIcon("overlay/pause32.png");

	public static ImageIcon turboOverlayIcon = loadIcon("overlay/turbo64.png");

	public static ImageIcon turboSmallOverlayIcon = loadIcon("overlay/turbo32.png");

	public static ImageIcon autotypeOverlayIcon = loadIcon("overlay/autotype64.png");

	public static ImageIcon autotypeSmallOverlayIcon = loadIcon("overlay/autotype32.png");

	public static ImageIcon tapeOverlayIcon = loadIcon("overlay/tape64.png");

	public static ImageIcon tapeSmallOverlayIcon = loadIcon("overlay/tape32.png");

	public static ImageIcon tapeReadOverlayIcon = loadIcon("overlay/play22.png");

	public static ImageIcon tapeWriteOverlayIcon = loadIcon("overlay/record22.png");

	public static ImageIcon loadIcon(String resourceName) {
		ImageIcon icon = null;
		Image image = loadImage("icons/" + resourceName);
		if (image != null) {
			icon = new ImageIcon(image);
		}
		return icon;
	}

	public static BufferedImage loadImage(String resourceName) {
		return ImageUtils.readFromStream(UIResources.class.getResourceAsStream("images/" + resourceName));
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

	private static class AudioIcon extends ButtonStateIcon {

		public AudioIcon(Icon mutedIcon, Icon unmutedIcon) {
			super(mutedIcon, unmutedIcon);
		}

		@Override
		protected boolean isSelected(Component c) {
			Action a = ((JMenuItem) c).getAction();
			return ((AudioAction) a).isMuted();
		}

	}

	private static class VirtualKeyboardIcon extends ButtonStateIcon {

		public VirtualKeyboardIcon(Icon offIcon, Icon onIcon) {
			super(offIcon, onIcon);
		}

		@Override
		protected boolean isSelected(Component c) {
			Action a = ((JMenuItem) c).getAction();
			return !((VirtualKeyboardAction) a).isActive();
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

	private static class WindowIcon extends ButtonStateIcon {

		public WindowIcon(Icon fullscreenIcon, Icon windowedIcon) {
			super(fullscreenIcon, windowedIcon);
		}

		@Override
		protected boolean isSelected(Component c) {
			Action a = ((JMenuItem) c).getAction();
			return ((MonitorFullscreenAction) a).isFullscreen();
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