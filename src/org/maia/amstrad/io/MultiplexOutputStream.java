package org.maia.amstrad.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Vector;

public class MultiplexOutputStream extends OutputStream {

	private List<OutputStream> outputStreams;

	public MultiplexOutputStream() {
		this.outputStreams = new Vector<OutputStream>();
	}

	public void addOutputStream(OutputStream outputStream) {
		getOutputStreams().add(outputStream);
	}

	@Override
	public void write(int b) throws IOException {
		for (OutputStream os : getOutputStreams()) {
			os.write(b);
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		for (OutputStream os : getOutputStreams()) {
			os.write(b);
		}
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (OutputStream os : getOutputStreams()) {
			os.write(b, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		for (OutputStream os : getOutputStreams()) {
			os.flush();
		}
	}

	@Override
	public void close() throws IOException {
		for (OutputStream os : getOutputStreams()) {
			os.close();
		}
	}

	private List<OutputStream> getOutputStreams() {
		return outputStreams;
	}

}