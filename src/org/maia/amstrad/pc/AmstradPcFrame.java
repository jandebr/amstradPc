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
import org.maia.util.GenericListenerList;

public abstract class AmstradPcFrame extends JFrame
		implements AmstradPcStateListener, WindowListener, WindowStateListener {

	private AmstradPc amstradPc;

	private boolean powerOffWhenClosed;

	private boolean closing;

	private GenericListenerList<AmstradPcFrameListener> frameListeners;

	protected AmstradPcFrame(AmstradPc amstradPc, String title, boolean powerOffWhenClosed) {
		super(title);
		this.amstradPc = amstradPc;
		this.powerOffWhenClosed = powerOffWhenClosed;
		this.frameListeners = new GenericListenerList<AmstradPcFrameListener>();
		setFocusable(false);
		setAlwaysOnTop(amstradPc.getMonitor().isWindowAlwaysOnTop());
		setIconImage(UIResources.cpcIcon.getImage());
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // handled inside windowClosing()
		amstradPc.addStateListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		getContentPane().add(getContentComponent(), BorderLayout.CENTER);
	}

	public void installMenuBar(AmstradMenuBar menuBar) {
		setJMenuBar(menuBar);
		if (isVisible()) {
			pack();
		}
	}

	public void uninstallMenuBar() {
		setJMenuBar(null);
		if (isVisible()) {
			pack();
		}
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

	public void deiconify() {
		setExtendedState(getExtendedState() & ~Frame.ICONIFIED);
	}

	public void iconify() {
		setExtendedState(getExtendedState() | Frame.ICONIFIED);
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		setVisible(true);
		deiconify();
		toFront();
		requestFocus();
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
			fireAmstradPcFrameClosed();
			if (isPowerOffWhenClosed()) {
				if (!getAmstradPc().isTerminated()) {
					AmstradFactory.getInstance().getAmstradContext().powerOff(getAmstradPc());
				}
			}
			dispose();
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
			// user may have maximized the frame via the window controls
			getAmstradPc().getMonitor().makeFullscreen();
		}
	}

	public void addFrameListener(AmstradPcFrameListener listener) {
		getFrameListeners().addListener(listener);
	}

	public void removeFrameListener(AmstradPcFrameListener listener) {
		getFrameListeners().removeListener(listener);
	}

	private void fireAmstradPcFrameClosed() {
		for (AmstradPcFrameListener listener : getFrameListeners()) {
			listener.amstradPcFrameClosed(this);
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

	public boolean isPowerOffWhenClosed() {
		return powerOffWhenClosed;
	}

	private boolean isClosing() {
		return closing;
	}

	private void setClosing(boolean closing) {
		this.closing = closing;
	}

	private GenericListenerList<AmstradPcFrameListener> getFrameListeners() {
		return frameListeners;
	}

}