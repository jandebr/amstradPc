package org.maia.amstrad.pc.menu;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardAdapter;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;
import org.maia.amstrad.pc.monitor.AmstradMonitor;

public class AmstradPopupMenu extends JPopupMenu implements AmstradMenu {

	private AmstradPc amstradPc;

	private PopupMenuKeyActivator keyActivator;

	private PopupMenuController popupMenuController;

	private MenuController menuController;

	public static int KEY_CODE_TRIGGER = KeyEvent.VK_F2;

	public AmstradPopupMenu(AmstradPc amstradPc) {
		this(amstradPc, "Amstrad Menu");
	}

	public AmstradPopupMenu(AmstradPc amstradPc, String label) {
		super(label);
		this.amstradPc = amstradPc;
		this.keyActivator = new PopupMenuKeyActivator(KEY_CODE_TRIGGER);
		this.popupMenuController = new PopupMenuController();
		this.menuController = new MenuController();
	}

	@Override
	public void install() {
		AmstradMonitor monitor = getMonitor();
		if (monitor != null) {
			monitor.installPopupMenu(this);
			activate();
		}
	}

	@Override
	public void uninstall() {
		AmstradMonitor monitor = getMonitor();
		if (monitor != null) {
			monitor.uninstallPopupMenu();
			deactivate();
		}
	}

	public void showPopupMenu() {
		if (isPopupMenuInstalled() && !isPopupMenuShowing()) {
			Dimension dim = getPreferredSize();
			JComponent comp = getMonitor().getDisplayComponent();
			show(comp, (comp.getWidth() - dim.width) / 2, (comp.getHeight() - dim.height) / 2);
			forceInitialMenuSelection();
		}
	}

	public void hidePopupMenu() {
		if (isPopupMenuShowing()) {
			cancelPopupMenu();
		}
	}

	public void handleKeyEvent(KeyEvent keyEvent) {
		processKeyEvent(keyEvent);
	}

	private void activate() {
		getAmstradPc().getKeyboard().addKeyboardListener(getKeyActivator());
		addPopupMenuListener(getPopupMenuController());
		installControllerOnMenus(this);
	}

	private void installControllerOnMenus(MenuElement element) {
		if (element instanceof JMenu) {
			JMenu menu = ((JMenu) element);
			menu.removeMenuListener(getMenuController()); // not adding twice
			menu.addMenuListener(getMenuController());
		}
		for (MenuElement child : element.getSubElements()) {
			installControllerOnMenus(child);
		}
	}

	private void deactivate() {
		getAmstradPc().getKeyboard().removeKeyboardListener(getKeyActivator());
		removePopupMenuListener(getPopupMenuController());
	}

	private void forceInitialMenuSelection() {
		handleKeyEvent(createInitialMenuSelectionKeyEvent(KeyEvent.KEY_PRESSED));
		handleKeyEvent(createInitialMenuSelectionKeyEvent(KeyEvent.KEY_RELEASED));
	}

	private void cancelPopupMenu() {
		handleKeyEvent(createCancelMenuKeyEvent(KeyEvent.KEY_PRESSED));
		handleKeyEvent(createCancelMenuKeyEvent(KeyEvent.KEY_RELEASED));
	}

	private KeyEvent createInitialMenuSelectionKeyEvent(int keyEventType) {
		Component source = getAmstradPc().getFrame().getRootPane();
		return new KeyEvent(source, keyEventType, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN,
				KeyEvent.CHAR_UNDEFINED);
	}

	private KeyEvent createCancelMenuKeyEvent(int keyEventType) {
		Component source = getAmstradPc().getFrame().getRootPane();
		return new KeyEvent(source, keyEventType, System.currentTimeMillis(), 0, KeyEvent.VK_ESCAPE,
				KeyEvent.CHAR_UNDEFINED);
	}

	private void refreshUI() {
		AmstradMonitor monitor = getMonitor();
		if (monitor != null) {
			monitor.getDisplayComponent().revalidate();
		}
	}

	public boolean isPopupMenuInstalled() {
		return equals(getMonitor().getInstalledPopupMenu());
	}

	public boolean isPopupMenuShowing() {
		return isPopupMenuInstalled() && isShowing();
	}

	private AmstradMonitor getMonitor() {
		return getAmstradPc().getMonitor();
	}

	@Override
	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private PopupMenuKeyActivator getKeyActivator() {
		return keyActivator;
	}

	private PopupMenuController getPopupMenuController() {
		return popupMenuController;
	}

	private MenuController getMenuController() {
		return menuController;
	}

	private class PopupMenuKeyActivator extends AmstradKeyboardAdapter {

		private int triggerKeyCode;

		public PopupMenuKeyActivator(int triggerKeyCode) {
			this.triggerKeyCode = triggerKeyCode;
		}

		@Override
		public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
			if (event.isKeyPressed() && event.getKeyCode() == getTriggerKeyCode() && !isTerminating()) {
				if (!isPopupMenuShowing()) {
					showPopupMenu();
				} else {
					// This code actually never gets hit, since key focus is on popup menu
					hidePopupMenu();
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
			firePopupMenuWillBecomeVisible();
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			firePopupMenuWillBecomeInvisible();
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			firePopupMenuWillBecomeInvisible();
		}

		private void firePopupMenuWillBecomeVisible() {
			AmstradMonitor monitor = getMonitor();
			if (monitor != null) {
				monitor.firePopupMenuWillBecomeVisible(AmstradPopupMenu.this);
			}
		}

		private void firePopupMenuWillBecomeInvisible() {
			refreshUI(); // ensures the display is restored properly
			AmstradMonitor monitor = getMonitor();
			if (monitor != null) {
				monitor.firePopupMenuWillBecomeInvisible(AmstradPopupMenu.this);
			}
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