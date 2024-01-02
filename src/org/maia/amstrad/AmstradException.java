package org.maia.amstrad;

public class AmstradException extends Exception {

	public AmstradException(String message) {
		this(message, null);
	}

	public AmstradException(Throwable cause) {
		this(null, cause);
	}

	public AmstradException(String message, Throwable cause) {
		super(message, cause);
	}

}