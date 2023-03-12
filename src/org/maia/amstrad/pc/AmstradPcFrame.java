package org.maia.amstrad.pc;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.monitor.AmstradMonitor;

public class AmstradPcFrame extends JFrame implements AmstradPcStateListener, WindowListener {

	private AmstradPc amstradPc;

	private boolean closing;

	public AmstradPcFrame(AmstradPc amstradPc, boolean exitOnClose) {
		this(amstradPc, "JavaCPC - Amstrad CPC Emulator", exitOnClose);
	}

	public AmstradPcFrame(AmstradPc amstradPc, String title, boolean exitOnClose) {
		super(title);
		this.amstradPc = amstradPc;
		amstradPc.addStateListener(this);
		addWindowListener(this);
		setFocusable(false);
		setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		buildUI();
	}

	protected void buildUI() {
		getContentPane().add(getAmstradPc().getMonitor().getDisplayPane(), BorderLayout.CENTER);
	}

	public void installMenu() {
		if (isKioskMode()) {
			installPopupMenu();
		} else {
			installMenuBar();
		}
	}

	public void installMenuBar() {
		setJMenuBar(AmstradFactory.getInstance().createMenuBar(getAmstradPc()));
	}

	public void installPopupMenu() {
		JPopupMenu popupMenu = AmstradFactory.getInstance().createPopupMenu(getAmstradPc());
		getAmstradPc().getMonitor().getDisplayComponent().setComponentPopupMenu(popupMenu);
		popupMenu.addPopupMenuListener(new PopupMenuController());
	}

	public boolean isFullscreen() {
		return getAmstradPc().getMonitor().isWindowFullscreen();
	}

	public void toggleFullscreen() {
		getAmstradPc().getMonitor().toggleWindowFullscreen();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		AmstradMonitor monitor = amstradPc.getMonitor();
		if (isKioskMode()) {
			AmstradFactory.getInstance().getAmstradContext().showProgramBrowser(amstradPc);
			monitor.setWindowAlwaysOnTop(true);
			monitor.makeWindowFullscreen();
		}
		if (monitor.isWindowFullscreen()) {
			// forcing the display to nicely align in the middle
			monitor.toggleWindowFullscreen();
			monitor.toggleWindowFullscreen();
		}
		setVisible(true);
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
		setClosing(true);
		if (!getAmstradPc().isTerminated()) {
			getAmstradPc().terminate();
		}
		AmstradFactory.getInstance().getAmstradContext().getUserSettings().flush();
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

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private boolean isKioskMode() {
		return AmstradFactory.getInstance().getAmstradContext().isKioskMode();
	}

	private boolean isClosing() {
		return closing;
	}

	private void setClosing(boolean closing) {
		this.closing = closing;
	}

	private class PopupMenuController implements PopupMenuListener {

		public PopupMenuController() {
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			getAmstradPc().getMonitor().getDisplayPane().revalidate(); // ensures the display is restored properly where
																		// the popup menu was drawn in overlay
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}

	}

}