package org.maia.amstrad.pc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class AmstradPcBasicRuntime {

	protected AmstradPcBasicRuntime() {
	}

	public abstract void keyboardType(CharSequence text, boolean waitUntilTyped);

	public void keyboardType(CharSequence text) {
		keyboardType(text, true);
	}

	public void keyboardEnter(CharSequence text, boolean waitUntilTyped) {
		keyboardType(text + "\n", waitUntilTyped);
	}

	public void keyboardEnter(CharSequence text) {
		keyboardEnter(text, true);
	}

	public void keyboardEnter() {
		keyboardEnter("");
	}

	public void cls() {
		keyboardEnter("CLS");
	}

	public void list() {
		keyboardEnter("LIST");
	}

	public void run() {
		keyboardEnter("RUN");
	}

	public void run(File basicFile) throws IOException {
		load(basicFile);
		run();
	}

	public void load(File basicFile) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(basicFile));
		StringBuilder sb = new StringBuilder(2048);
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}
		reader.close();
		keyboardType(sb);
	}

}