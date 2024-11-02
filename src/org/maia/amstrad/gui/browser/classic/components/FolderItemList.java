package org.maia.amstrad.gui.browser.classic.components;

import java.util.List;

import org.maia.amstrad.gui.components.ScrollableItemList;
import org.maia.amstrad.program.repo.AmstradProgramRepository.FolderNode;
import org.maia.amstrad.program.repo.AmstradProgramRepository.Node;

public class FolderItemList extends ScrollableItemList<Node> {

	private FolderNode folderNode;

	public FolderItemList(FolderNode folderNode, int maxItemsShowing) {
		super(maxItemsShowing);
		this.folderNode = folderNode;
	}

	@Override
	public int size() {
		return getItems().size();
	}

	@Override
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