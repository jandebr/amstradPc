package org.maia.amstrad.jemu;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.MenuBar;

public abstract class JemuFrameAdapter {

	protected JemuFrameAdapter() {
	}

	public abstract void setTitle(String title);

	public abstract void setMenuBar(MenuBar menuBar);

	public abstract void removeMenuBar(MenuBar menuBar);

	public abstract void setLocation(int x, int y);

	public abstract void setSize(int width, int height);

	public abstract void setResizable(boolean resizable);

	public abstract void setAlwaysOnTop(boolean alwaysOnTop);

	public abstract void setVisible(boolean visible);

	public abstract void pack();

	public abstract void dispose();

	public abstract void setUndecorated(boolean undecorated);

	public abstract int getX();

	public abstract int getY();

	public abstract Dimension getSize();

	public abstract FileDialog createFileDialog(String title, int mode);

}