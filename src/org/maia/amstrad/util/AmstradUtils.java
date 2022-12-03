package org.maia.amstrad.util;

public class AmstradUtils {

	private AmstradUtils() {
	}

	public static void sleep(long milliseconds) {
		if (milliseconds > 0L) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

}