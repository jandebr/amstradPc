package org.maia.amstrad.tape.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

public class Viewer {

	private JComponent view;

	private String title;

	private boolean exitOnClose;

	private JFrame frame;

	public Viewer(JComponent view, String title, boolean exitOnClose) {
		this.view = view;
		this.title = title;
		this.exitOnClose = exitOnClose;
	}

	public void buildAndShow() {
		build();
		show();
	}

	public void build() {
		JFrame frame = new JFrame(getTitle());
		setFrame(frame);
		frame.getContentPane().add(getView());
		frame.pack();
		frame.setDefaultCloseOperation(isExitOnClose() ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(((ImageIcon) UIResourcesTape.windowIcon).getImage());
		if (isDefaultCenteredOnScreen()) {
			centerOnScreen();
		}
	}

	public void setJMenuBar(JMenuBar menuBar) {
		if (getFrame() != null) {
			getFrame().setJMenuBar(menuBar);
		}
	}

	public void show() {
		if (getFrame() != null) {
			getFrame().setVisible(true);
		}
	}

	public void hide() {
		if (getFrame() != null) {
			getFrame().setVisible(false);
		}
	}

	public void close() {
		if (getFrame() != null) {
			getFrame().dispatchEvent(new WindowEvent(getFrame(), WindowEvent.WINDOW_CLOSING));
		}
	}

	public void maximize() {
		if (getFrame() != null) {
			getFrame().setExtendedState(getFrame().getExtendedState() | Frame.MAXIMIZED_BOTH);
		}
	}

	public void centerOnScreen() {
		if (getFrame() != null) {
			Dimension screenSize = UIFactoryTape.getScreenSize();
			getFrame().setLocation(
					new Point((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2));
		}
	}

	public boolean isDefaultCenteredOnScreen() {
		return true;
	}

	public boolean isExitOnClose() {
		return exitOnClose;
	}

	public String getTitle() {
		return title;
	}

	public JComponent getView() {
		return view;
	}

	protected JFrame getFrame() {
		return frame;
	}

	private void setFrame(JFrame frame) {
		this.frame = frame;
	}

}