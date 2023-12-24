package org.maia.amstrad.pc.frame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.util.GenericListenerList;

public abstract class AmstradPcFrame extends JFrame
		implements AmstradPcStateListener, WindowListener, WindowStateListener {

	private AmstradPc amstradPc;

	private JMenuBar installedMenuBar;

	private AmstradPcPopupMenu installedPopupMenu;

	private boolean closing;

	private GenericListenerList<AmstradPcFrameListener> frameListeners;

	protected AmstradPcFrame(AmstradPc amstradPc, String title, boolean exitOnClose) {
		super(title);
		this.amstradPc = amstradPc;
		this.frameListeners = new GenericListenerList<AmstradPcFrameListener>();
		amstradPc.addStateListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		setFocusable(false);
		setAlwaysOnTop(amstradPc.getMonitor().isWindowAlwaysOnTop());
		setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UIResources.cpcIcon.getImage());
		getContentPane().add(getContentComponent(), BorderLayout.CENTER);
	}

	public void addFrameListener(AmstradPcFrameListener listener) {
		getFrameListeners().addListener(listener);
	}

	public void removeFrameListener(AmstradPcFrameListener listener) {
		getFrameListeners().removeListener(listener);
	}

	public void installAndEnableMenuBar() {
		AmstradPcMenuMaker menuMaker = new AmstradPcMenuMaker(getAmstradPc().getActions(),
				AmstradPcMenuMaker.LookAndFeel.JAVA);
		JMenuBar menuBar = menuMaker.createMenuBar();
		setInstalledMenuBar(menuBar);
		enableMenuBar();
	}

	public void installAndEnablePopupMenu(boolean enableOnlyInFullscreen) {
		AmstradPcMenuMaker menuMaker = new AmstradPcMenuMaker(getAmstradPc().getActions(),
				AmstradPcMenuMaker.LookAndFeel.EMULATOR);
		AmstradPcPopupMenu popupMenu = menuMaker.createStandardPopupMenu();
		setInstalledPopupMenu(popupMenu);
		if (enableOnlyInFullscreen) {
			popupMenu.enableAutomaticallyWhenInFullscreen();
		} else {
			popupMenu.enablePopupMenu();
		}
	}

	public void enableMenuBar() {
		setJMenuBar(getInstalledMenuBar());
	}

	public void disableMenuBar() {
		setJMenuBar(null);
	}

	public boolean isMenuBarEnabled() {
		return getJMenuBar() != null;
	}

	public boolean isPopupMenuEnabled() {
		AmstradPcPopupMenu popupMenu = getInstalledPopupMenu();
		return popupMenu != null && popupMenu.isPopupMenuEnabled();
	}

	public boolean isPopupMenuShowing() {
		AmstradPcPopupMenu popupMenu = getInstalledPopupMenu();
		return popupMenu != null && popupMenu.isPopupMenuShowing();
	}

	public boolean isMenuKeyBindingsEnabled() {
		return isMenuBarEnabled() || isPopupMenuShowing();
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
			}
			AmstradFactory.getInstance().getAmstradContext().getUserSettings().flush();
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

	protected void refreshUI() {
		getContentComponent().revalidate();
	}

	protected void firePopupMenuWillBecomeVisible() {
		for (AmstradPcFrameListener listener : getFrameListeners())
			listener.popupMenuWillBecomeVisible(this);
	}

	protected void firePopupMenuWillBecomeInvisible() {
		for (AmstradPcFrameListener listener : getFrameListeners())
			listener.popupMenuWillBecomeInvisible(this);
	}

	protected abstract Component getContentComponent();

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	public JMenuBar getInstalledMenuBar() {
		return installedMenuBar;
	}

	private void setInstalledMenuBar(JMenuBar menuBar) {
		this.installedMenuBar = menuBar;
	}

	public AmstradPcPopupMenu getInstalledPopupMenu() {
		return installedPopupMenu;
	}

	private void setInstalledPopupMenu(AmstradPcPopupMenu popupMenu) {
		this.installedPopupMenu = popupMenu;
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