package org.maia.amstrad.pc.menu;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.util.GenericListenerList;

public class AmstradMenuBar extends JMenuBar implements AmstradMenu {

	private AmstradPc amstradPc;

	private MenuController menuController;

	private GenericListenerList<AmstradMenuBarListener> listeners;

	public AmstradMenuBar(AmstradPc amstradPc) {
		this.amstradPc = amstradPc;
		this.menuController = new MenuController();
		this.listeners = new GenericListenerList<AmstradMenuBarListener>();
	}

	@Override
	public void install() {
		AmstradPcFrame frame = getFrame();
		if (frame != null) {
			frame.installMenuBar(this);
		}
	}

	@Override
	public void uninstall() {
		AmstradPcFrame frame = getFrame();
		if (frame != null) {
			frame.uninstallMenuBar();
		}
	}

	@Override
	public JMenu add(JMenu menu) {
		menu.addMenuListener(getMenuController());
		return super.add(menu);
	}

	public void addListener(AmstradMenuBarListener listener) {
		getListeners().addListener(listener);
	}

	public void removeListener(AmstradMenuBarListener listener) {
		getListeners().removeListener(listener);
	}

	private void fireMenuBarSelectionChanged() {
		for (AmstradMenuBarListener listener : getListeners()) {
			listener.menuBarSelectionChanged(this);
		}
	}

	protected AmstradPcFrame getFrame() {
		return getAmstradPc().getFrame();
	}

	@Override
	public AmstradPc getAmstradPc() {
		return amstradPc;
	}

	private MenuController getMenuController() {
		return menuController;
	}

	private GenericListenerList<AmstradMenuBarListener> getListeners() {
		return listeners;
	}

	private class MenuController implements MenuListener {

		public MenuController() {
		}

		@Override
		public void menuSelected(MenuEvent e) {
			fireMenuBarSelectionChanged();
		}

		@Override
		public void menuDeselected(MenuEvent e) {
			fireMenuBarSelectionChanged();
		}

		@Override
		public void menuCanceled(MenuEvent e) {
			fireMenuBarSelectionChanged();
		}

	}

}