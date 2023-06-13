package org.maia.amstrad.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterable list of <code>AmstradListener</code> or any of its sub-interfaces. The primary use case is to provide one
 * consistent implementation of the <em>Observer</em> design pattern.
 * 
 * <p>
 * Iteration can be performed using the following code idiom:
 * 
 * <pre>{@code
 * AmstradListenerList<MyListener> listeners = new AmstradListenerList<MyListener>();
 * listeners.addListener(new MyListenerImpl());
 * ...
 * for (MyListener listener : listeners)
 *     listener.notifyOfSomething();
 * }</pre>
 * 
 * Iteration follows the order in which listeners were added
 * <p>
 * Unlike traditional use of {@link List} for storing listeners, this implementation is robust against concurrent
 * modifications while iterating (within the same thread or by other threads). The behavior is as follows:
 * <ul>
 * <li>When a listener is removed and is ahead of an open iterator, it will be skipped by that iterator</li>
 * <li>When a listener is added, the default behavior is that it will not be returned by open iterators. Newly created
 * iterators will include the new listener. The default behavior can be altered by
 * {@link #setIncludeAdditionsWhileIterating(boolean)} and passing the value <code>true</code>. This will take effect on
 * both open and newly created iterators. For this to also work in the special case that the last listener, when
 * invoked, wishes to add another listener <em>and</em> remove itself as a listener, it should do so in this specific
 * order. Since a listener usually does not know it's the last in sequence, this order is always recommended.</li>
 * </ul>
 * <p>
 * The implementation is thread-safe
 * </p>
 * 
 * @param <T>
 *            The element type of this list, a sub-interface of <code>AmstradListener</code>
 * 
 * @see AmstradListener
 */
public class AmstradListenerList<T extends AmstradListener> implements Iterable<T> {

	private ListenerElement headElement;

	private ListenerElement tailElement;

	private int nextElementSequenceNumber;

	private boolean includeAdditionsWhileIterating; // false by default

	private EmptyListenerIterator emptyListenerIterator;

	public AmstradListenerList() {
		clear();
	}

	public synchronized void clear() {
		setHeadElement(null);
		setTailElement(null);
		setNextElementSequenceNumber(0);
	}

	public synchronized void addListener(T listener) {
		ListenerElement element = new ListenerElement(listener);
		if (isEmpty()) {
			setHeadElement(element);
		} else {
			getTailElement().setNextElement(element);
		}
		setTailElement(element);
	}

	public synchronized void removeListener(T listener) {
		boolean removed = false;
		ListenerElement previous = null;
		ListenerElement current = getHeadElement();
		while (!removed && current != null) {
			ListenerElement next = current.getNextElement();
			if (current.getListener().equals(listener)) {
				if (previous == null) {
					setHeadElement(next); // removed at head
					if (next == null) {
						setTailElement(null); // list becomes empty
					}
				} else {
					previous.setNextElement(next);
					if (next == null) {
						setTailElement(previous); // removed at tail
					}
				}
				removed = true;
			} else {
				previous = current;
				current = next;
			}
		}
	}

	@Override
	public Iterator<T> iterator() {
		if (isEmpty()) {
			if (emptyListenerIterator == null) {
				emptyListenerIterator = new EmptyListenerIterator();
			}
			return emptyListenerIterator;
		} else {
			return new ListenerIterator();
		}
	}

	public boolean isEmpty() {
		return getHeadElement() == null;
	}

	private synchronized int pullNextElementSequenceNumber() {
		int n = getNextElementSequenceNumber();
		setNextElementSequenceNumber(n + 1);
		return n;
	}

	private ListenerElement getHeadElement() {
		return headElement;
	}

	private void setHeadElement(ListenerElement headElement) {
		this.headElement = headElement;
	}

	private ListenerElement getTailElement() {
		return tailElement;
	}

	private void setTailElement(ListenerElement tailElement) {
		this.tailElement = tailElement;
	}

	public boolean isIncludeAdditionsWhileIterating() {
		return includeAdditionsWhileIterating;
	}

	public void setIncludeAdditionsWhileIterating(boolean include) {
		this.includeAdditionsWhileIterating = include;
	}

	private int getNextElementSequenceNumber() {
		return nextElementSequenceNumber;
	}

	private void setNextElementSequenceNumber(int number) {
		this.nextElementSequenceNumber = number;
	}

	private class ListenerElement {

		private T listener;

		private ListenerElement nextElement;

		private int sequenceNumber;

		public ListenerElement(T listener) {
			this.listener = listener;
			this.sequenceNumber = pullNextElementSequenceNumber();
		}

		public ListenerElement getNextElement() {
			return nextElement;
		}

		public void setNextElement(ListenerElement nextElement) {
			this.nextElement = nextElement;
		}

		public T getListener() {
			return listener;
		}

		public int getSequenceNumber() {
			return sequenceNumber;
		}

	}

	private class ListenerIterator implements Iterator<T> {

		private ListenerElement initialHeadElement;

		private ListenerElement lastReturnedElement;

		private int maximumElementSequenceNumber;

		public ListenerIterator() {
			setInitialHeadElement(getHeadElement());
			if (isIncludeAdditionsWhileIterating()) {
				setMaximumElementSequenceNumber(Integer.MAX_VALUE);
			} else {
				ListenerElement tail = getTailElement();
				setMaximumElementSequenceNumber(tail != null ? tail.getSequenceNumber() : 0);
			}
		}

		@Override
		public boolean hasNext() {
			return findNextElement() != null;
		}

		@Override
		public T next() {
			ListenerElement element = findNextElement();
			if (element == null) {
				throw new NoSuchElementException();
			} else {
				setLastReturnedElement(element);
			}
			return element.getListener();
		}

		private ListenerElement findNextElement() {
			ListenerElement next = null;
			if (getLastReturnedElement() == null) {
				next = getInitialHeadElement();
			} else {
				next = getLastReturnedElement().getNextElement();
			}
			if (next != null && !isIncludeAdditionsWhileIterating()
					&& next.getSequenceNumber() > getMaximumElementSequenceNumber()) {
				next = null;
			}
			return next;
		}

		private ListenerElement getInitialHeadElement() {
			return initialHeadElement;
		}

		private void setInitialHeadElement(ListenerElement element) {
			this.initialHeadElement = element;
		}

		private ListenerElement getLastReturnedElement() {
			return lastReturnedElement;
		}

		private void setLastReturnedElement(ListenerElement element) {
			this.lastReturnedElement = element;
		}

		private int getMaximumElementSequenceNumber() {
			return maximumElementSequenceNumber;
		}

		private void setMaximumElementSequenceNumber(int number) {
			this.maximumElementSequenceNumber = number;
		}

	}

	private class EmptyListenerIterator implements Iterator<T> {

		public EmptyListenerIterator() {
		}

		@Override
		public final boolean hasNext() {
			return false;
		}

		@Override
		public final T next() {
			throw new NoSuchElementException();
		}

	}

}