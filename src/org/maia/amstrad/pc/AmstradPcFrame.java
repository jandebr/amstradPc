package org.maia.amstrad.pc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.menu.AmstradMenuBar;

public abstract class AmstradPcFrame extends JFrame
		implements AmstradPcStateListener, WindowListener, WindowStateListener {

	private AmstradPc amstradPc;

	private boolean closing;

	protected AmstradPcFrame(AmstradPc amstradPc, String title, boolean exitOnClose) {
		super(title);
		this.amstradPc = amstradPc;
		amstradPc.addStateListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		setFocusable(false);
		setAlwaysOnTop(amstradPc.getMonitor().isWindowAlwaysOnTop());
		setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(UIResources.cpcIcon.getImage());
		getContentPane().add(getContentComponent(), BorderLayout.CENTER);
	}

	public void installMenuBar(AmstradMenuBar menuBar) {
		setJMenuBar(menuBar);
	}

	public void uninstallMenuBar() {
		setJMenuBar(null);
	}

	public void centerOnScreen() {
		Dimension screen = getScreenSize();
		Dimension size = getSize();
		setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);
	}

	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public boolean isFullscreen() {
		return getAmstradPc().getMonitor().isFullscreen();
	}

	public void toggleFullscreen() {
		getAmstradPc().getMonitor().toggleFullscreen();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		setVisible(true);
		getContentComponent().requestFocus();
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public synchronized void amstradPcTerminated(AmstradPc amstradPc) {
		if (!isClosing()) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void amstradPcProgramLoaded(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void windowActivated(WindowEvent event) {
		// no action
	}

	@Override
	public void windowClosed(WindowEvent event) {
		// no action
	}

	@Override
	public synchronized void windowClosing(WindowEvent event) {
		if (!isClosing()) {
			setClosing(true);
			if (!getAmstradPc().isTerminated()) {
				getAmstradPc().terminate();
				AmstradFactory.getInstance().getAmstradContext().getUserSettings().flush();
			}
		}
	}

	@Override
	public void windowDeactivated(WindowEvent event) {
		// no action
	}

	@Override
	public void windowDeiconified(WindowEvent event) {
		// no action
	}

	@Override
	public void windowIconified(WindowEvent event) {
		// no action
	}

	@Override
	public void windowOpened(WindowEvent event) {
		// no action
	}

	@Override
	public void windowStateChanged(WindowEvent event) {
		if ((event.getNewState() & Frame.MAXIMIZED_BOTH) != 0) {
			getAmstradPc().getMonitor().makeFullscreen();
		}
	}

	protected abstract Component getContentComponent();

	public boolean isMenuBarInstalled() {
		return getInstalledMenuBar() != null;
	}

	public AmstradMenuBar getInstalledMenuBar() {
		return (AmstradMenuBar) getJMenuBar();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private boolean isClosing() {
		return closing;
	}

	private void setClosing(boolean closing) {
		this.closing = closing;
	}

}