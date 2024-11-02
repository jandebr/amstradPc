package org.maia.amstrad.gui.browser.classic.components;

import java.util.Stack;

import org.maia.amstrad.program.repo.AmstradProgramRepository;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public class StackedFolderItemList {

	private AmstradProgramRepository programRepository;

	private Stack<FolderItemList> stack;

	private int maxItemsShowing;

	public StackedFolderItemList(AmstradProgramRepository programRepository, int maxItemsShowing) {
		this.programRepository = programRepository;
		this.stack = new Stack<FolderItemList>();
		this.maxItemsShowing = maxItemsShowing;
		reset();
	}

	public void reset() {
		getProgramRepository().refresh();
		getStack().clear();
		getStack().push(new FolderItemList(getProgramRepository().getRootNode(), getMaxItemsShowing()));
	}

	public void browseIntoSelectedItem() {
		if (canBrowseIntoSelectedItem()) {
			push(new FolderItemList(getSelectedItem().asFolder(), getMaxItemsShowing()));
		}
	}

	public void browseBack() {
		if (size() > 1) {
			pop();
		}
	}

	public boolean canBrowseIntoSelectedItem() {
		Node selectedItem = getSelectedItem();
		if (selectedItem != null) {
			return selectedItem.isFolder();
		} else {
			return false;
		}
	}

	public boolean canCreateProgramMenu() {
		Node selectedItem = getSelectedItem();
		if (selectedItem != null) {
			return selectedItem.isProgram();
		} else {
			return false;
		}
	}

	public Node getSelectedItem() {
		Node selectedItem = null;
		if (size() > 0) {
			selectedItem = getStack().peek().getSelectedItem();
		}
		return selectedItem;
	}

	public int size() {
		return getStack().size();
	}

	public FolderItemList peek() {
		return getStack().peek();
	}

	public FolderItemList peek(int distanceFromTop) {
		return getStack().get(size() - 1 - distanceFromTop);
	}

	private void push(FolderItemList itemList) {
		getStack().push(itemList);
	}

	private void pop() {
		getStack().pop();
	}

	private AmstradProgramRepository getProgramRepository() {
		return programRepository;
	}

	private Stack<FolderItemList> getStack() {
		return stack;
	}

	public int getMaxItemsShowing() {
		return maxItemsShowing;
	}

}