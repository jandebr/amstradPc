package org.maia.amstrad.gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class UIResources {

	private static String iconFolder = "/icons/";

	public static Icon pauseIcon = loadIcon(iconFolder + "pause32.png");

	private static Icon loadIcon(String resourceName) {
		return new ImageIcon(UIResources.class.getResource(resourceName));
	}

}