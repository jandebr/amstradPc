package org.maia.amstrad.tape.read;

import java.io.IOException;

import org.maia.amstrad.tape.model.Bit;

public abstract class TapeInputStream {

	public abstract Bit nextBit() throws IOException;

	public Byte nextByte() throws IOException {
		Byte bite = null;
		int b = 0;
		int v = 128;
		Bit bit = null;
		while (v != 0 && (bit = nextBit()) != null) {
			if (Bit.ONE.equals(bit))
				b += v;
			v /= 2;
		}
		if (v == 0)
			bite = (byte) b;
		return bite;
	}

	public Integer nextWord() throws IOException {
		Integer word = null;
		Byte b1 = nextByte();
		if (b1 != null) {
			Byte b2 = nextByte();
			if (b2 != null) {
				word = (b1.byteValue() & 0xff) | ((b2.byteValue() << 8) & 0xff00);
			}
		}
		return word;
	}

	public abstract boolean seekSilence() throws IOException;

	public abstract boolean skipSilence() throws IOException;

	public abstract void close() throws IOException;

}