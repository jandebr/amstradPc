package org.maia.amstrad.load.basic.staged.file;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class TextFileWriter {

	private PrintWriter delegate;

	public TextFileWriter(File file) throws IOException {
		this.delegate = new PrintWriter(file);
	}

	public void writeLine(String line) throws IOException {
		getDelegate().println(line);
		getDelegate().flush();
	}

	public void close() throws IOException {
		getDelegate().flush();
		getDelegate().close();
	}

	private PrintWriter getDelegate() {
		return delegate;
	}

}