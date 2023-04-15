package org.maia.amstrad.gui.components;

public abstract class ScrollableItemList<T extends ScrollableItem> {

	private int maxItemsShowing;

	private int indexOfFirstItemShowing;

	private int indexOfSelectedItem;

	protected ScrollableItemList(int maxItemsShowing) {
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
		if (getIndexOfLastItemShowing() < size() - 1) {
			setIndexOfFirstItemShowing(
					Math.min(getIndexOfFirstItemShowing() + getMaxItemsShowing(), size() - getMaxItemsShowing()));
			setIndexOfSelectedItem(getIndexOfFirstItemShowing());
		}
	}

	public void browseOnePageUp() {
		if (getIndexOfFirstItemShowing() > 0) {
			setIndexOfFirstItemShowing(Math.max(getIndexOfFirstItemShowing() - getMaxItemsShowing(), 0));
			setIndexOfSelectedItem(getIndexOfFirstItemShowing());
		}
	}

	public void browseTo(int itemIndex) {
		while (getIndexOfSelectedItem() < itemIndex && getIndexOfSelectedItem() < size() - 1) {
			browseOneItemDown();
		}
		while (getIndexOfSelectedItem() > itemIndex && getIndexOfSelectedItem() > 0) {
			browseOneItemUp();
		}
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public abstract int size();

	public abstract T getItem(int index);

	public T getSelectedItem() {
		T selectedItem = null;
		if (!isEmpty()) {
			return getItem(getIndexOfSelectedItem());
		}
		return selectedItem;
	}

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