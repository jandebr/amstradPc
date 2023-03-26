package org.maia.amstrad.pc.tape;

public interface AmstradTapeListener {

	void amstradTapeReading(AmstradTape tape);

	void amstradTapeStoppedReading(AmstradTape tape);

	void amstradTapeWriting(AmstradTape tape);

	void amstradTapeStoppedWriting(AmstradTape tape);

}