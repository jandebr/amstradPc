package org.maia.amstrad.basic;

public class BasicMemoryFullException extends BasicException {

	public BasicMemoryFullException() {
		this("The Basic memory is full");
	}

	public BasicMemoryFullException(String message) {
		super(message);
	}

}