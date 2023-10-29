package org.maia.amstrad.pc.tape;

import org.maia.util.GenericListener;

public interface AmstradTapeListener extends GenericListener {

	void amstradTapeReading(AmstradTape tape);

	void amstradTapeStoppedReading(AmstradTape tape);

	void amstradTapeWriting(AmstradTape tape);

	void amstradTapeStoppedWriting(AmstradTape tape);

}