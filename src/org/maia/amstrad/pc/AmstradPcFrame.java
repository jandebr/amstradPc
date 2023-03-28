package org.maia.amstrad.pc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.action.AmstradPcMenuMaker;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.util.AmstradUtils;

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
			JPopupMenu popupMenu = new AmstradPcMenuMaker(getAmstradPc().getActions(),
					AmstradPcMenuMaker.MenuFlavor.KIOSK_MENU).createPopupMenu();
			getAmstradPc().getMonitor().getDisplayComponent().setComponentPopupMenu(popupMenu);
			popupMenu.addPopupMenuListener(new PopupMenuController());
			installControllerOnMenus(popupMenu, new MenuController());
		} else {
			setJMenuBar(new AmstradPcMenuMaker(getAmstradPc().getActions()).createMenuBar());
		}
	}

	private void installControllerOnMenus(MenuElement element, MenuController controller) {
		if (element instanceof JMenu) {
			((JMenu) element).addMenuListener(controller);
		}
		for (MenuElement child : element.getSubElements()) {
			installControllerOnMenus(child, controller);
		}
	}

	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public void centerOnScreen() {
		Dimension screen = getScreenSize();
		Dimension size = getSize();
		setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);
	}

	private boolean extendsOutsideScreen() {
		Dimension screen = getScreenSize();
		Dimension size = getSize();
		Point loc = getLocationOnScreen();
		return loc.x < 0 || loc.y < 0 || loc.x + size.width > screen.width || loc.y + size.height > screen.height;
	}

	public boolean isFullscreen() {
		return getAmstradPc().getMonitor().isWindowFullscreen();
	}

	public void toggleFullscreen() {
		getAmstradPc().getMonitor().toggleWindowFullscreen();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		setVisible(true);
		AmstradMonitor monitor = amstradPc.getMonitor();
		if (monitor.isWindowFullscreen()) {
			// Force full screen as it is not consistently working
			if (monitor.getDisplayPane().getLocationOnScreen().getX() != 0) {
				System.out.println("Force center display on screen");
				monitor.toggleWindowFullscreen();
				monitor.toggleWindowFullscreen();
			}
		}
		new MonitorDisplayUltimateCenterer().start(); // final check and attempts
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

	private void refreshUI() {
		getAmstradPc().getMonitor().getDisplayPane().revalidate();
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

	private class MonitorDisplayUltimateCenterer extends Thread {

		public MonitorDisplayUltimateCenterer() {
			super("MonitorDisplayUltimateCenterer");
			setDaemon(true);
		}

		@Override
		public void run() {
			AmstradUtils.sleep(1000L);
			AmstradMonitor monitor = getAmstradPc().getMonitor();
			if (monitor.isWindowFullscreen()) {
				JComponent displayComp = monitor.getDisplayComponent();
				int expectedX = (getScreenSize().width - displayComp.getWidth()) / 2;
				int attempts = 0;
				while (Math.abs(displayComp.getLocationOnScreen().x - expectedX) > 2 && ++attempts <= 3) {
					System.out.println("Ultimate center display on screen");
					monitor.toggleWindowFullscreen();
					monitor.toggleWindowFullscreen();
					AmstradUtils.sleep(500L);
				}
				System.out.println("Display is centered fullscreen");
			} else if (extendsOutsideScreen()) {
				centerOnScreen();
				System.out.println("Display is centered on screen");
			}
		}

	}

	private class PopupMenuController implements PopupMenuListener {

		public PopupMenuController() {
		}

		@Override
		public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			refreshUI(); // ensures the display is restored properly
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
		}

	}

	private class MenuController implements MenuListener {

		public MenuController() {
		}

		@Override
		public void menuSelected(MenuEvent e) {
		}

		@Override
		public void menuDeselected(MenuEvent e) {
			refreshUI(); // ensures the display is restored properly
		}

		@Override
		public void menuCanceled(MenuEvent e) {
		}

	}

}