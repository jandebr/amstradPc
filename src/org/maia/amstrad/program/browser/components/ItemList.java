package org.maia.amstrad.program.browser.components;

public abstract class ItemList {

	private int maxItemsShowing;

	private int indexOfFirstItemShowing;

	private int indexOfSelectedItem;

	protected ItemList(int maxItemsShowing) {
		this.maxItemsShowing = maxItemsShowing;
	}

	public void browseHome() {
		setIndexOfFirstItemShowing(0);
		setIndexOfSelectedItem(0);
	}

	public void browseEnd() {
		setIndexOfFirstItemShowing(Math.max(size() - getMaxItemsShowing(), 0));
		setIndexOfSelectedItem(size() - 1);
	}

	public void browseOneItemDown() {
		int i = getIndexOfSelectedItem();
		if (i < size() - 1) {
			if (i - getIndexOfFirstItemShowing() + 1 == getMaxItemsShowing()) {
				setIndexOfFirstItemShowing(getIndexOfFirstItemShowing() + 1);
			}
			setIndexOfSelectedItem(i + 1);
		}
	}

	public void browseOneItemUp() {
		int i = getIndexOfSelectedItem();
		if (i > 0) {
			if (i == getIndexOfFirstItemShowing()) {
				setIndexOfFirstItemShowing(getIndexOfFirstItemShowing() - 1);
			}
			setIndexOfSelectedItem(i - 1);
		}
	}

	public void browseOnePageDown() {
		for (int i = 0; i < getMaxItemsShowing(); i++)
			browseOneItemDown();
	}

	public void browseOnePageUp() {
		for (int i = 0; i < getMaxItemsShowing(); i++)
			browseOneItemUp();
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public abstract int size();

	public int getIndexOfLastItemShowing() {
		return Math.min(getIndexOfFirstItemShowing() + getMaxItemsShowing(), size()) - 1;
	}

	public int getMaxItemsShowing() {
		return maxItemsShowing;
	}

	public int getIndexOfFirstItemShowing() {
		return indexOfFirstItemShowing;
	}

	private void setIndexOfFirstItemShowing(int index) {
		this.indexOfFirstItemShowing = index;
	}

	public int getIndexOfSelectedItem() {
		return indexOfSelectedItem;
	}

	private void setIndexOfSelectedItem(int index) {
		this.indexOfSelectedItem = index;
	}

}