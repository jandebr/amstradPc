package org.maia.amstrad.pc.frame;

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
import org.maia.amstrad.pc.monitor.AmstradMonitorAdapter;

public class AmstradPcPopupMenu extends JPopupMenu {

	private AmstradPc amstradPc;

	public static int KEY_CODE_TRIGGER = KeyEvent.VK_F2;

	public AmstradPcPopupMenu(AmstradPc amstradPc) {
		this(amstradPc, "Amstrad Menu");
	}

	public AmstradPcPopupMenu(AmstradPc amstradPc, String label) {
		super(label);
		this.amstradPc = amstradPc;
		init();
	}

	private void init() {
		getAmstradPc().getKeyboard().addKeyboardListener(new PopupMenuKeyActivator(KEY_CODE_TRIGGER));
		addPopupMenuListener(new PopupMenuController());
		installControllerOnMenus(this, new MenuController());
	}

	private void installControllerOnMenus(MenuElement element, MenuController controller) {
		if (element instanceof JMenu) {
			((JMenu) element).addMenuListener(controller);
		}
		for (MenuElement child : element.getSubElements()) {
			installControllerOnMenus(child, controller);
		}
	}

	public void enableAutomaticallyWhenInFullscreen() {
		getAmstradPc().getMonitor().addMonitorListener(new PopupMenuFullscreenActivator());
		if (getFrame().isFullscreen()) {
			enablePopupMenu();
		}
	}

	public void enablePopupMenu() {
		getAttachedComponent().setComponentPopupMenu(this);
	}

	public void disablePopupMenu() {
		getAttachedComponent().setComponentPopupMenu(null);
	}

	public void showPopupMenu() {
		if (isPopupMenuShowing())
			return;
		if (isPopupMenuEnabled()) {
			Dimension dim = getPreferredSize();
			JComponent comp = getAmstradPc().getMonitor().getDisplayComponent();
			show(comp, (comp.getWidth() - dim.width) / 2, (comp.getHeight() - dim.height) / 2);
			forceInitialMenuSelection();
		}
	}

	public void handleKeyEvent(KeyEvent keyEvent) {
		processKeyEvent(keyEvent);
	}

	private void forceInitialMenuSelection() {
		handleKeyEvent(createInitialMenuSelectionKeyEvent(KeyEvent.KEY_PRESSED));
		handleKeyEvent(createInitialMenuSelectionKeyEvent(KeyEvent.KEY_RELEASED));
	}

	private KeyEvent createInitialMenuSelectionKeyEvent(int keyEventType) {
		Component source = getAmstradPc().getFrame().getRootPane();
		return new KeyEvent(source, keyEventType, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN,
				KeyEvent.CHAR_UNDEFINED);
	}

	public boolean isPopupMenuEnabled() {
		return equals(getAttachedComponent().getComponentPopupMenu());
	}

	public boolean isPopupMenuShowing() {
		return isPopupMenuEnabled() && isShowing();
	}

	private JComponent getAttachedComponent() {
		return getAmstradPc().getMonitor().getDisplayComponent();
	}

	private AmstradPcFrame getFrame() {
		return getAmstradPc().getFrame();
	}

	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private class PopupMenuFullscreenActivator extends AmstradMonitorAdapter {

		public PopupMenuFullscreenActivator() {
		}

		@Override
		public void amstradMonitorFullscreenChanged(AmstradMonitor monitor) {
			if (monitor.isFullscreen()) {
				enablePopupMenu();
			} else {
				disablePopupMenu();
			}
		}

	}

	private class PopupMenuKeyActivator extends AmstradKeyboardAdapter {

		private int triggerKeyCode;

		public PopupMenuKeyActivator(int triggerKeyCode) {
			this.triggerKeyCode = triggerKeyCode;
		}

		@Override
		public void amstradKeyboardEventDispatched(AmstradKeyboardEvent event) {
			if (event.isKeyPressed() && event.getKeyCode() == getTriggerKeyCode()) {
				if (!isTerminating()) {
					showPopupMenu();
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
			getFrame().firePopupMenuWillBecomeVisible();
		}

		@Override
		public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			getFrame().refreshUI(); // ensures the display is restored properly
			getFrame().firePopupMenuWillBecomeInvisible();
		}

		@Override
		public void popupMenuCanceled(PopupMenuEvent e) {
			getFrame().firePopupMenuWillBecomeInvisible();
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
			getFrame().refreshUI(); // ensures the display is restored properly
		}

		@Override
		public void menuCanceled(MenuEvent e) {
		}

	}

}