package org.maia.amstrad.gui.browser.components;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramMenu extends ItemList {

	private ProgramBrowserDisplaySource browser;

	private AmstradProgram program;

	private List<ProgramMenuItem> menuItems;

	public ProgramMenu(ProgramBrowserDisplaySource browser, AmstradProgram program, int maxItemsShowing) {
		super(maxItemsShowing);
		this.browser = browser;
		this.program = program;
		this.menuItems = new Vector<ProgramMenuItem>();
		populateMenu();
	}

	private void populateMenu() {
		ProgramBrowserDisplaySource browser = getBrowser();
		AmstradProgram program = getProgram();
		addMenuItem(new ProgramRunMenuItem(browser, program));
		addMenuItem(new ProgramLoadMenuItem(browser, program));
		addMenuItem(new ProgramInfoMenuItem(browser, program));
		addMenuItem(new ProgramImagesMenuItem(browser, program));
		addMenuItem(new ProgramFileReferencesMenuItem(browser, program));
		addMenuItem(new ProgramCloseMenuItem(browser, program));
	}

	private void addMenuItem(ProgramMenuItem menuItem) {
		getMenuItems().add(menuItem);
	}

	public void addReturnMenuItem() {
		int insertionIndex = -1;
		for (int i = 0; i < size(); i++) {
			ProgramMenuItem item = getItem(i);
			if (ProgramRunMenuItem.class.isAssignableFrom(item.getClass())) {
				insertionIndex = i; // insert right before Run
			} else if (ProgramReturnMenuItem.class.isAssignableFrom(item.getClass())) {
				insertionIndex = -1; // already in the menu
				break;
			}
		}
		if (insertionIndex >= 0) {
			getMenuItems().add(insertionIndex, new ProgramReturnMenuItem(getBrowser(), getProgram()));
			browseTo(insertionIndex); // make it the selected item
		}
	}

	@Override
	public int size() {
		return getMenuItems().size();
	}

	public ProgramMenuItem getSelectedItem() {
		ProgramMenuItem selectedItem = null;
		if (!isEmpty()) {
			return getItem(getIndexOfSelectedItem());
		}
		return selectedItem;
	}

	public ProgramMenuItem getItem(int index) {
		return getMenuItems().get(index);
	}

	private ProgramBrowserDisplaySource getBrowser() {
		return browser;
	}

	public AmstradProgram getProgram() {
		return program;
	}

	public List<ProgramMenuItem> getMenuItems() {
		return menuItems;
	}

}