package org.maia.amstrad.load.basic.staged.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextFileReader {

	private BufferedReader delegate;

	private String nextLine;

	public TextFileReader(File file) throws IOException {
		this.delegate = new BufferedReader(new FileReader(file));
		readLineUpfront();
	}

	public boolean isEndOfFile() {
		return getNextLine() == null;
	}

	public String readLine() throws IOException {
		String line = getNextLine();
		readLineUpfront();
		return line;
	}

	public void close() throws IOException {
		getDelegate().close();
	}

	private void readLineUpfront() throws IOException {
		setNextLine(getDelegate().readLine());
	}

	private BufferedReader getDelegate() {
		return delegate;
	}

	private String getNextLine() {
		return nextLine;
	}

	private void setNextLine(String nextLine) {
		this.nextLine = nextLine;
	}

}