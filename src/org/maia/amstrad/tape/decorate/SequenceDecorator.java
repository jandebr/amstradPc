package org.maia.amstrad.tape.decorate;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Vector;

public abstract class SequenceDecorator<T extends SequenceDecoration> {

	private long[] offsetsInOrder;

	private List<T> decorationsInOrder;

	protected SequenceDecorator(int initialCapacity) {
		this.offsetsInOrder = new long[initialCapacity];
		this.decorationsInOrder = new Vector<T>(initialCapacity);
	}

	@Override
	public String toString() {
		int max = getMaximumDecorationsInToString();
		Iterator<T> it = getDecorationsInOrderIterator();
		StringBuilder sb = new StringBuilder(48 * max);
		sb.append(this.getClass().getSimpleName());
		sb.append(" [\n");
		int i = 0;
		int lower_i = max / 2 - (1 - max % 2);
		int upper_i = size() - max / 2;
		boolean dots = false;
		while (it.hasNext()) {
			T decoration = it.next();
			if (i <= lower_i || i >= upper_i) {
				sb.append('\t');
				sb.append(decoration.toString());
				sb.append('\n');
			} else if (!dots) {
				sb.append("\t...\n");
				dots = true;
			}
			i++;
		}
		sb.append("]");
		return sb.toString();
	}

	protected int getMaximumDecorationsInToString() {
		return 100;
	}

	public Iterator<T> getDecorationsInOrderIterator() {
		return new DecorationsInOrderIterator();
	}

	protected List<T> getDecorationsInRange(long from, long until, boolean includeOverlapping) {
		List<T> decorations = null;
		int i = firstIndexInRange(from, includeOverlapping);
		if (i >= 0) {
			int j = lastIndexInRange(until, includeOverlapping);
			if (j >= 0) {
				decorations = getDecorationsInOrder().subList(i, j + 1);
			}
		}
		if (decorations == null) {
			decorations = Collections.emptyList();
		}
		return decorations;
	}

	private int firstIndexInRange(long from, boolean includeOverlapping) {
		int i = Arrays.binarySearch(getOffsetsInOrder(), 0, size(), from);
		if (i < 0) {
			i = -(i + 1) - 1;
		}
		while (i >= 0
				&& (getDecorationAt(i).getOffset() >= from || (includeOverlapping && getDecorationAt(i).getEnd() >= from)))
			i--;
		if (i == size())
			return -1;
		return i + 1;
	}

	private int lastIndexInRange(long until, boolean includeOverlapping) {
		int i = Arrays.binarySearch(getOffsetsInOrder(), 0, size(), until);
		if (i < 0) {
			i = -(i + 1) - 1;
		}
		while (i >= 0
				&& i < size()
				&& (getDecorationAt(i).getEnd() <= until || (includeOverlapping && getDecorationAt(i).getOffset() <= until)))
			i++;
		i--;
		if (!includeOverlapping) {
			while (i >= 0 && getDecorationAt(i).getEnd() > until)
				i--;
		}
		return i;
	}

	protected void addDecoration(T decoration) {
		if (size() == capacity()) {
			increaseCapacity();
		}
		int i = Arrays.binarySearch(getOffsetsInOrder(), 0, size(), decoration.getOffset());
		if (i < 0) {
			i = -(i + 1);
		}
		if (i < size()) {
			shiftDecorationsOneIndexPosition(i);
			getDecorationsInOrder().set(i, decoration);
		} else {
			getDecorationsInOrder().add(decoration);
		}
		getOffsetsInOrder()[i] = decoration.getOffset();
	}

	protected void removeDecoration(T decoration) {
		int index = getDecorationsInOrder().indexOf(decoration);
		if (index >= 0) {
			getDecorationsInOrder().remove(index);
			long[] offsets = getOffsetsInOrder();
			for (int i = index; i < size(); i++) {
				offsets[i] = offsets[i + 1];
			}
		}
	}

	private void shiftDecorationsOneIndexPosition(int fromIndex) {
		if (size() == capacity()) {
			increaseCapacity();
		}
		long[] offsets = getOffsetsInOrder();
		List<T> decorations = getDecorationsInOrder();
		int n = size();
		decorations.add(null);
		for (int i = n; i > fromIndex; i--) {
			offsets[i] = offsets[i - 1];
			decorations.set(i, decorations.get(i - 1));
		}
	}

	private void increaseCapacity() {
		this.offsetsInOrder = Arrays.copyOf(getOffsetsInOrder(), 2 * capacity());
	}

	protected int size() {
		return getDecorationsInOrder().size();
	}

	private int capacity() {
		return getOffsetsInOrder().length;
	}

	private T getDecorationAt(int index) {
		return getDecorationsInOrder().get(index);
	}

	private long[] getOffsetsInOrder() {
		return offsetsInOrder;
	}

	private List<T> getDecorationsInOrder() {
		return decorationsInOrder;
	}

	private class DecorationsInOrderIterator implements Iterator<T> {

		private int index;

		private T lastReturnedDecoration;

		public DecorationsInOrderIterator() {
		}

		@Override
		public boolean hasNext() {
			return index < size();
		}

		@Override
		public T next() {
			if (!hasNext())
				throw new NoSuchElementException();
			lastReturnedDecoration = getDecorationAt(index++);
			return lastReturnedDecoration;
		}

		@Override
		public void remove() {
			if (lastReturnedDecoration != null) {
				removeDecoration(lastReturnedDecoration);
				lastReturnedDecoration = null;
				index--;
			}
		}

	}

}