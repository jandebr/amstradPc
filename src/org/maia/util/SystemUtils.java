package org.maia.util;

public class SystemUtils {

	private SystemUtils() {
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