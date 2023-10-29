package org.maia.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An iterable list of <code>GenericListener</code> or any of its sub-interfaces. The primary use case is to provide a
 * consistent and reusable implementation for the <em>Observer</em> design pattern
 * 
 * <p>
 * Iteration can be performed using the traditional code idiom:
 * 
 * <pre>{@code
 * GenericListenerList<MyListener> listeners = new GenericListenerList<MyListener>();
 * listeners.addListener(new MyListenerImpl());
 * ...
 * for (MyListener listener : listeners)
 *     listener.notifyOfSomething();
 * }</pre>
 * 
 * Iteration follows the order in which listeners were added
 * <p>
 * A listener can only participate once in an <code>GenericListenerList</code>. This is checked when adding listeners
 * <p>
 * Unlike traditional {@link List} implementations for storing listeners, this implementation is robust against
 * concurrent modifications while iterating (by the same thread or by other threads). In particular, an
 * <code>GenericListenerList</code> will never throw any {@link ConcurrentModificationException}. The modification
 * behavior is defined as follows:
 * <ul>
 * <li>Listeners added by {@link #addListener(GenericListener)} are not returned by open iterators. It requires a new
 * iterator to include the added listener. This is the default behavior but it can be altered by
 * {@link #setIncludeAdditionsWhileIterating(boolean)} passing the value <code>true</code>. This has immediate effect on
 * both newly created iterators and open iterators with one exception. When all listeners ahead of an iterator have been
 * removed as well as its last returned listener, additions will no longer be visible to that (exhausted) iterator</li>
 * <li>Listeners removed by {@link #removeListener(GenericListener)} will no longer be returned by any iterator (unless
 * they are added again, see previous point)</li>
 * <li>Removing all listeners at once by {@link #removeAllListeners()} or {@link #clear()} will instantly exhaust all
 * open iterators</li>
 * </ul>
 * <p>
 * The implementation is thread-safe
 * </p>
 * 
 * @param <T>
 *            The type of listener contained in this list, a sub-interface of <code>GenericListener</code>
 * 
 * @see GenericListener
 */
public class GenericListenerList<T extends GenericListener> implements Iterable<T> {

	private ListenerElement headElement; // starting point for new iterators

	private ListenerElement tailElement; // attachment point for added listeners

	private int sequenceNumberForNextElement;

	private boolean includeAdditionsWhileIterating; // false by default

	private EmptyListenerIterator emptyListenerIterator;

	public GenericListenerList() {
	}

	/**
	 * Tells whether a given listener is a member of this list
	 * <p>
	 * The method of comparison is via the <code>listener.equals()</code> method
	 * </p>
	 * 
	 * @param listener
	 *            The listener to check
	 * @return <code>true</code> if the listener is part of this list, <code>false</code> otherwise
	 */
	public synchronized boolean containsListener(T listener) {
		ListenerElement current = getHeadElement();
		while (current != null) {
			if (current.getListener().equals(listener))
				return true;
			current = current.getNextElement();
		}
		return false;
	}

	/**
	 * Adds a listener to this list
	 * 
	 * @param listener
	 *            The listener to add
	 * @return <code>true</code> if this list changed as a result of the call, <code>false</code> if the listener was
	 *         already in this list
	 * @see #containsListener(GenericListener)
	 */
	public synchronized boolean addListener(T listener) {
		if (!containsListener(listener)) {
			ListenerElement element = new ListenerElement(listener, sequenceNumberForNextElement++);
			if (isEmpty()) {
				setHeadElement(element);
			} else {
				getTailElement().setNextElement(element);
			}
			setTailElement(element);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Removes a listener from this list
	 * 
	 * @param listener
	 *            The listener to remove
	 * @return <code>true</code> if this list changed as a result of the call, <code>false</code> if the listener was
	 *         not in this list
	 * @see #containsListener(GenericListener)
	 */
	public synchronized boolean removeListener(T listener) {
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
				current.setRemoved(true);
				removed = true;
			} else {
				previous = current;
				current = next;
			}
		}
		return removed;
	}

	/**
	 * Remove all listeners from this list
	 */
	public synchronized void removeAllListeners() {
		ListenerElement current = getHeadElement();
		while (current != null) {
			current.setRemoved(true);
			current = current.getNextElement();
		}
		setHeadElement(null);
		setTailElement(null);
	}

	/**
	 * Remove all listeners from this list
	 * <p>
	 * Identical to <code>removeAllListeners()</code> but the naming is more similar to that of the <code>List</code>
	 * interface
	 * </p>
	 */
	public synchronized void clear() {
		removeAllListeners();
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

	/**
	 * Tells whether no listeners are present in this list
	 * 
	 * @return <code>true</code> if there are no listeners in this list, <code>false</code> otherwise
	 */
	public boolean isEmpty() {
		return getHeadElement() == null;
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

	/**
	 * Tells whether added listeners take part in open iterators
	 * 
	 * @return <code>true</code> when added listeners take part in open iterators, <code>false</code> (default)
	 *         otherwise
	 */
	public boolean isIncludeAdditionsWhileIterating() {
		return includeAdditionsWhileIterating;
	}

	/**
	 * Sets whether added listeners take part in open iterators
	 * 
	 * @param include
	 *            <code>true</code> when added listeners should take part in open iterators, <code>false</code> if not
	 */
	public void setIncludeAdditionsWhileIterating(boolean include) {
		this.includeAdditionsWhileIterating = include;
	}

	private class ListenerElement {

		private T listener;

		private ListenerElement nextElement;

		private int sequenceNumber;

		private boolean removed;

		public ListenerElement(T listener, int sequenceNumber) {
			this.listener = listener;
			this.sequenceNumber = sequenceNumber;
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

		public boolean isRemoved() {
			return removed;
		}

		public void setRemoved(boolean removed) {
			this.removed = removed;
		}

	}

	private class ListenerIterator implements Iterator<T> {

		private ListenerElement initialHeadElement;

		private ListenerElement lastReturnedElement;

		private ListenerElement nextElement;

		private boolean nextElementDefined;

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
		public synchronized boolean hasNext() {
			return defineNextElement() != null;
		}

		@Override
		public synchronized T next() {
			if (hasNext()) {
				ListenerElement next = getNextElement();
				setNextElement(null);
				setNextElementDefined(false);
				setLastReturnedElement(next);
				return next.getListener();
			} else {
				throw new NoSuchElementException();
			}
		}

		private ListenerElement defineNextElement() {
			if (!isNextElementDefined()) {
				ListenerElement next = null;
				if (getLastReturnedElement() == null) {
					next = getInitialHeadElement();
				} else {
					next = getLastReturnedElement().getNextElement();
				}
				while (next != null && (next.isRemoved() || (!isIncludeAdditionsWhileIterating()
						&& next.getSequenceNumber() > getMaximumElementSequenceNumber()))) {
					next = next.getNextElement();
				}
				setNextElement(next);
				setNextElementDefined(true);
			}
			return getNextElement();
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

		private ListenerElement getNextElement() {
			return nextElement;
		}

		private void setNextElement(ListenerElement nextElement) {
			this.nextElement = nextElement;
		}

		private boolean isNextElementDefined() {
			return nextElementDefined;
		}

		private void setNextElementDefined(boolean nextElementDefined) {
			this.nextElementDefined = nextElementDefined;
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