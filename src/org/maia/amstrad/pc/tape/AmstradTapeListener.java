package org.maia.amstrad.pc.tape;

import org.maia.amstrad.util.AmstradListener;

public interface AmstradTapeListener extends AmstradListener {

	void amstradTapeReading(AmstradTape tape);

	void amstradTapeStoppedReading(AmstradTape tape);

	void amstradTapeWriting(AmstradTape tape);

	void amstradTapeStoppedWriting(AmstradTape tape);

}