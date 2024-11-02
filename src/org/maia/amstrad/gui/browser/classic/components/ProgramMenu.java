package org.maia.amstrad.gui.browser.classic.components;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.gui.browser.classic.ClassicProgramBrowserDisplaySource;
import org.maia.amstrad.gui.components.ScrollableItemList;
import org.maia.amstrad.program.AmstradProgram;

public class ProgramMenu extends ScrollableItemList<ProgramMenuItem> {

	private ClassicProgramBrowserDisplaySource browserDisplaySource;

	private AmstradProgram program;

	private List<ProgramMenuItem> menuItems;

	private static int DEFAULT_MAX_ITEMS_SHOWING = 7;

	public ProgramMenu(ClassicProgramBrowserDisplaySource browserDisplaySource, AmstradProgram program) {
		this(browserDisplaySource, program, DEFAULT_MAX_ITEMS_SHOWING);
	}

	public ProgramMenu(ClassicProgramBrowserDisplaySource browserDisplaySource, AmstradProgram program,
			int maxItemsShowing) {
		super(maxItemsShowing);
		this.browserDisplaySource = browserDisplaySource;
		this.program = program;
		this.menuItems = new Vector<ProgramMenuItem>();
		populateMenu();
	}

	private void populateMenu() {
		addMenuItem(new ProgramRunMenuItem(this));
		if (getBrowserDisplaySource().getSystemSettings().isProgramSourceCodeAccessible()) {
			addMenuItem(new ProgramLoadMenuItem(this));
		}
		addMenuItem(new ProgramInfoMenuItem(this));
		addMenuItem(new ProgramImagesMenuItem(this));
		if (getBrowserDisplaySource().getSystemSettings().isProgramAuthoringToolsAvailable()) {
			addMenuItem(new ProgramFileReferencesMenuItem(this));
		}
		addMenuItem(new ProgramCloseMenuItem(this));
	}

	private void addMenuItem(ProgramMenuItem menuItem) {
		getMenuItems().add(menuItem);
	}

	public void addReturnMenuItem() {
		if (getItemTyped(ProgramReturnMenuItem.class) != null)
			return; // already in the menu
		int insertionIndex = getMenuItems().indexOf(getItemTyped(ProgramRunMenuItem.class));
		if (insertionIndex >= 0) {
			getMenuItems().add(insertionIndex, new ProgramReturnMenuItem(this));
			browseTo(insertionIndex); // make it the selected item
		}
	}

	@Override
	public int size() {
		return getMenuItems().size();
	}

	@Override
	public ProgramMenuItem getItem(int index) {
		return getMenuItems().get(index);
	}

	public <T extends ProgramMenuItem> T getItemTyped(Class<T> type) {
		T item = null;
		int i = 0;
		while (item == null && i < size()) {
			if (type.isAssignableFrom(getItem(i).getClass())) {
				item = type.cast(getItem(i));
			} else {
				i++;
			}
		}
		return item;
	}

	public ClassicProgramBrowserDisplaySource getBrowserDisplaySource() {
		return browserDisplaySource;
	}

	public AmstradProgram getProgram() {
		return program;
	}

	public List<ProgramMenuItem> getMenuItems() {
		return menuItems;
	}

}