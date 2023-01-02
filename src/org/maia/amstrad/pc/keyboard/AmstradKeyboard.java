package org.maia.amstrad.pc.keyboard;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.io.AmstradIO;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcDevice;

public abstract class AmstradKeyboard extends AmstradPcDevice {

	protected AmstradKeyboard(AmstradPc amstradPc) {
		super(amstradPc);
	}

	public abstract void type(CharSequence text, boolean waitUntilTyped);

	public void type(CharSequence text) {
		type(text, true);
	}

	public void typeFileContents(File textFile, boolean waitUntilTyped) throws IOException {
		type(AmstradIO.readTextFileContents(textFile), waitUntilTyped);
	}

	public void typeFileContents(File textFile) throws IOException {
		typeFileContents(textFile, true);
	}

	public void enter(CharSequence text, boolean waitUntilTyped) {
		type(text + "\n", waitUntilTyped);
	}

	public void enter(CharSequence text) {
		enter(text, true);
	}

	public void enter(boolean waitUntilTyped) {
		enter("", waitUntilTyped);
	}

	public void enter() {
		enter(true);
	}

}