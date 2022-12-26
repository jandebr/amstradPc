package org.maia.amstrad.program.browser.navigate;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;

public class ProgramMenu extends ItemList {

	private AmstradProgram program;

	private List<ProgramMenuItem> menuItems;

	public ProgramMenu(ProgramBrowserDisplaySource browser, AmstradProgram program, int maxItemsShowing) {
		super(maxItemsShowing);
		this.program = program;
		this.menuItems = new Vector<ProgramMenuItem>();
		populateMenu(browser);
	}

	private void populateMenu(ProgramBrowserDisplaySource browser) {
		AmstradProgram program = getProgram();
		addMenuItem(new ProgramRunMenuItem(browser, program));
		addMenuItem(new ProgramLoadMenuItem(browser, program));
		addMenuItem(new ProgramInfoMenuItem(browser, program));
		addMenuItem(new ProgramImagesMenuItem(browser, program));
		addMenuItem(new ProgramCloseMenuItem(browser, program));
	}

	private void addMenuItem(ProgramMenuItem menuItem) {
		getMenuItems().add(menuItem);
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

	public AmstradProgram getProgram() {
		return program;
	}

	public List<ProgramMenuItem> getMenuItems() {
		return menuItems;
	}

}