package org.maia.amstrad.util;

import org.maia.util.GenericListener;
import org.maia.util.GenericListenerList;

public class AmstradListenerTest {

	private GenericListenerList<MyListener> listeners;

	public static void main(String[] args) {
		new AmstradListenerTest().startTest();
	}

	public AmstradListenerTest() {
		this.listeners = new GenericListenerList<MyListener>();
		this.listeners.setIncludeAdditionsWhileIterating(true);
	}

	public void startTest() {
		addListener(new MyObserver("A"));
		addListener(new MyObserver("B"));
		notifyThem();
		System.out.println("---");
		notifyThem();
		System.out.println("---");
		notifyThem();
	}

	private void addListener(MyListener listener) {
		getListeners().addListener(listener);
	}

	private void removeListener(MyListener listener) {
		getListeners().removeListener(listener);
	}

	private void removeAllListeners() {
		getListeners().removeAllListeners();
	}

	private void notifyThem() {
		for (MyListener listener : getListeners()) {
			listener.notifyMe();
		}
	}

	private GenericListenerList<MyListener> getListeners() {
		return listeners;
	}

	private class MyObserver implements MyListener {

		private String name;

		public MyObserver(String name) {
			this.name = name;
		}

		@Override
		public void notifyMe() {
			System.out.println("NOTIFIED: " + getName());
			if ("A".equals(getName())) {
				removeAllListeners();
				addListener(new MyObserver("C"));
			} else if ("C".equals(getName())) {
				addListener(new MyObserver("D"));
				removeListener(this);
			}
		}

		public String getName() {
			return name;
		}

	}

	private static interface MyListener extends GenericListener {

		void notifyMe();

	}

}