package org.maia.amstrad.pc;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.UIResources;
import org.maia.amstrad.pc.action.AmstradPcMenuMaker;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;

public abstract class AmstradPcFrame extends JFrame
		implements AmstradPcStateListener, WindowListener, WindowStateListener {

	private AmstradPc amstradPc;

	private JMenuBar installedMenuBar;

	private JPopupMenu installedPopupMenu;

	private boolean closing;

	protected AmstradPcFrame(AmstradPc amstradPc, String title, boolean exitOnClose) {
		super(title);
		this.amstradPc = amstradPc;
		amstradPc.addStateListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		setFocusable(false);
		setAlwaysOnTop(amstradPc.getMonitor().isWindowAlwaysOnTop());
		setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UIResources.cpcIcon.getImage());
		getContentPane().add(getContentComponent(), BorderLayout.CENTER);
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
		JPopupMenu popupMenu = menuMaker.createStandardPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuController());
		installControllerOnMenus(popupMenu, new MenuController());
		getAmstradPc().getKeyboard().addKeyboardListener(new PopupMenuTriggerByKeyPress(KeyEvent.VK_F2));
		setInstalledPopupMenu(popupMenu);
		if (enableOnlyInFullscreen) {
			getAmstradPc().getMonitor().addMonitorListener(new PopupMenuFullscreenActivator());
		}
		if (!enableOnlyInFullscreen || isFullscreen()) {
			enablePopupMenu();
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

	public void enableMenuBar() {
		setJMenuBar(getInstalledMenuBar());
	}

	public void disableMenuBar() {
		setJMenuBar(null);
	}

	public boolean isMenuBarEnabled() {
		return getJMenuBar() != null;
	}

	public void enablePopupMenu() {
		getAmstradPc().getMonitor().getDisplayComponent().setComponentPopupMenu(getInstalledPopupMenu());
	}

	public void disablePopupMenu() {
		getAmstradPc().getMonitor().getDisplayComponent().setComponentPopupMenu(null);
	}

	public boolean isPopupMenuEnabled() {
		return getInstalledPopupMenu() != null && getInstalledPopupMenu()
				.equals(getAmstradPc().getMonitor().getDisplayComponent().getComponentPopupMenu());
	}

	public boolean isMenuKeyBindingsEnabled() {
		return isMenuBarEnabled() || (isPopupMenuEnabled() && getInstalledPopupMenu().isShowing());
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
		return getAmstradPc().getMonitor().isWindowFullscreen();
	}

	public void toggleFullscreen() {
		getAmstradPc().getMonitor().toggleWindowFullscreen();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
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
			getAmstradPc().getMonitor().makeWindowFullscreen();
		}
	}

	protected void refreshUI() {
		getContentComponent().revalidate();
	}

	protected abstract Component getContentComponent();

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private JMenuBar getInstalledMenuBar() {
		return installedMenuBar;
	}

	private void setInstalledMenuBar(JMenuBar menuBar) {
		this.installedMenuBar = menuBar;
	}

	private JPopupMenu getInstalledPopupMenu() {
		return installedPopupMenu;
	}

	private void setInstalledPopupMenu(JPopupMenu popupMenu) {
		this.installedPopupMenu = popupMenu;
	}

	private boolean isClosing() {
		return closing;
	}

	private void setClosing(boolean closing) {
		this.closing = closing;
	}

	private class PopupMenuFullscreenActivator extends AmstradMonitorAdapter {

		public PopupMenuFullscreenActivator() {
		}

		@Override
		public void amstradWindowFullscreenChanged(AmstradMonitor monitor) {
			if (monitor.isWindowFullscreen()) {
				enablePopupMenu();
			} else {
				disablePopupMenu();
			}
		}

	}

	private class PopupMenuTriggerByKeyPress extends AmstradKeyboardAdapter {

		private int triggerKeyCode;

		public PopupMenuTriggerByKeyPress(int triggerKeyCode) {
			this.triggerKeyCode = triggerKeyCode;
		}

		@Override
		public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
			if (event.isKeyPressed() && event.getKeyCode() == getTriggerKeyCode()) {
				if (isPopupMenuEnabled() && !isTerminating()) {
					JPopupMenu popupMenu = getInstalledPopupMenu();
					Dimension dim = popupMenu.getPreferredSize();
					JComponent comp = getAmstradPc().getMonitor().getDisplayComponent();
					popupMenu.show(comp, (comp.getWidth() - dim.width) / 2, (comp.getHeight() - dim.height) / 2);
				}
			}
		}

		private boolean isTerminating() {
			return AmstradFactory.getInstance().getAmstradContext().isTerminationShowing(getAmstradPc());
		}

		public int getTriggerKeyCode() {
			return triggerKeyCode;
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