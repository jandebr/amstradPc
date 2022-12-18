package org.maia.amstrad.basic;

public class BasicCompilationException extends BasicSyntaxException {

	public BasicCompilationException(String message, String text, int positionInText) {
		super(message, text, positionInText);
	}

}