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

	private JPopupMenu popupMenu;

	private boolean closing;

	protected AmstradPcFrame(AmstradPc amstradPc, String title, boolean exitOnClose) {
		super(title);
		this.amstradPc = amstradPc;
		amstradPc.addStateListener(this);
		addWindowListener(this);
		addWindowStateListener(this);
		setFocusable(false);
		setDefaultCloseOperation(exitOnClose ? JFrame.EXIT_ON_CLOSE : JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UIResources.cpcIcon.getImage());
		getContentPane().add(getContentComponent(), BorderLayout.CENTER);
	}

	protected abstract Component getContentComponent();

	public void installMenuBar() {
		AmstradPcMenuMaker menuMaker = new AmstradPcMenuMaker(getAmstradPc().getActions(),
				AmstradPcMenuMaker.LookAndFeel.JAVA);
		setJMenuBar(menuMaker.createMenuBar());
	}

	public void installPopupMenu(boolean enableOnlyInFullscreen) {
		AmstradPcMenuMaker menuMaker = new AmstradPcMenuMaker(getAmstradPc().getActions(),
				AmstradPcMenuMaker.LookAndFeel.EMULATOR);
		JPopupMenu popupMenu = menuMaker.createStandardPopupMenu();
		popupMenu.addPopupMenuListener(new PopupMenuController());
		installControllerOnMenus(popupMenu, new MenuController());
		getAmstradPc().getKeyboard().addKeyboardListener(new PopupMenuTriggerByKeyPress(KeyEvent.VK_F2));
		setPopupMenu(popupMenu);
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

	private void enablePopupMenu() {
		getAmstradPc().getMonitor().getDisplayComponent().setComponentPopupMenu(getPopupMenu());
	}

	private void disablePopupMenu() {
		getAmstradPc().getMonitor().getDisplayComponent().setComponentPopupMenu(null);
	}

	public boolean isPopupMenuEnabled() {
		return getPopupMenu() != null
				&& getPopupMenu().equals(getAmstradPc().getMonitor().getDisplayComponent().getComponentPopupMenu());
	}

	public static Dimension getScreenSize() {
		return Toolkit.getDefaultToolkit().getScreenSize();
	}

	public void centerOnScreen() {
		Dimension screen = getScreenSize();
		Dimension size = getSize();
		setLocation((screen.width - size.width) / 2, (screen.height - size.height) / 2);
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

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private JPopupMenu getPopupMenu() {
		return popupMenu;
	}

	private void setPopupMenu(JPopupMenu popupMenu) {
		this.popupMenu = popupMenu;
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
					JPopupMenu popupMenu = getPopupMenu();
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