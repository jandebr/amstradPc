package org.maia.amstrad.jemu.impl;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.HeadlessException;
import java.awt.MenuBar;

import javax.swing.JFrame;

import jemu.settings.Settings;

import org.maia.amstrad.jemu.JemuFrameAdapter;

public class JemuFrameBridge extends JemuFrameAdapter {

	private JFrame frame;

	public JemuFrameBridge() {
	}

	public JemuFrameBridge(JFrame frame) {
		setFrame(frame);
	}

	@Override
	public void setTitle(String title) {
		if (!isFrameLess()) {
			getFrame().setTitle(title);
		}
	}

	@Override
	public void setMenuBar(MenuBar menuBar) {
		if (!isFrameLess()) {
			if (Settings.getBoolean(Settings.SHOWMENU, true)) {
				getFrame().setMenuBar(menuBar);
			}
		}
	}

	@Override
	public void removeMenuBar(MenuBar menuBar) {
		if (!isFrameLess()) {
			getFrame().remove(menuBar);
		}
	}

	@Override
	public void setLocation(int x, int y) {
		if (!isFrameLess()) {
			getFrame().setLocation(x, y);
		}
	}

	@Override
	public void setSize(int width, int height) {
		if (!isFrameLess()) {
			getFrame().setSize(width, height);
		}
	}

	@Override
	public void setResizable(boolean resizable) {
		if (!isFrameLess()) {
			getFrame().setResizable(resizable);
		}
	}

	@Override
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		if (!isFrameLess()) {
			getFrame().setAlwaysOnTop(alwaysOnTop);
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (!isFrameLess()) {
			getFrame().setVisible(visible);
		}
	}

	@Override
	public void pack() {
		if (!isFrameLess()) {
			getFrame().pack();
		}
	}

	@Override
	public void dispose() {
		if (!isFrameLess()) {
			getFrame().dispose();
		}
	}

	@Override
	public void setUndecorated(boolean undecorated) {
		if (!isFrameLess()) {
			getFrame().setUndecorated(undecorated);
		}
	}

	@Override
	public int getX() {
		if (!isFrameLess()) {
			return getFrame().getX();
		} else {
			return 0;
		}
	}

	@Override
	public int getY() {
		if (!isFrameLess()) {
			return getFrame().getY();
		} else {
			return 0;
		}
	}

	@Override
	public Dimension getSize() {
		if (!isFrameLess()) {
			return getFrame().getSize();
		} else {
			return new Dimension();
		}
	}

	@Override
	public FileDialog createFileDialog(String title, int mode) {
		if (isFrameLess())
			throw new HeadlessException("Not backed by any frame");
		return new FileDialog(getFrame(), title, mode);
	}

	private boolean isFrameLess() {
		return getFrame() == null;
	}

	private JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

}