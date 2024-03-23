package org.maia.amstrad.tape.read;

import org.maia.amstrad.tape.model.Bit;

public interface AudioTapeInputStreamListener {

	void readBit(Bit bit, long sampleOffset, long sampleLength, AudioTapeInputStream is);

}