package org.maia.amstrad.tape.write;

import java.io.IOException;

import org.maia.amstrad.tape.model.Bit;

public abstract class TapeOutputStream {

	protected TapeOutputStream() {
	}

	public abstract void writeSilence(long millis) throws IOException;

	public abstract void writeBit(Bit bit) throws IOException;

	public abstract void writeSpacer() throws IOException;

	public void close() throws IOException {
	}

}