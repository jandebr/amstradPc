package org.maia.amstrad.gui.browser.components;

import java.util.List;

import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public class FolderItemList extends ItemList {

	private FolderNode folderNode;

	public FolderItemList(FolderNode folderNode, int maxItemsShowing) {
		super(maxItemsShowing);
		this.folderNode = folderNode;
	}

	@Override
	public int size() {
		return getItems().size();
	}

	public Node getSelectedItem() {
		Node selectedItem = null;
		if (!isEmpty()) {
			return getItem(getIndexOfSelectedItem());
		}
		return selectedItem;
	}

	public Node getItem(int index) {
		return getItems().get(index);
	}

	public List<Node> getItems() {
		return getFolderNode().getChildNodes();
	}

	private FolderNode getFolderNode() {
		return folderNode;
	}

}