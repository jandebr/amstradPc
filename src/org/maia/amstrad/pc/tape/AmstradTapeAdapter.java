package org.maia.amstrad.pc.tape;

public abstract class AmstradTapeAdapter implements AmstradTapeListener {

	protected AmstradTapeAdapter() {
	}

	@Override
	public void amstradTapeReading(AmstradTape tape) {
		// Subclasses can override this
	}

	@Override
	public void amstradTapeStoppedReading(AmstradTape tape) {
		// Subclasses can override this
	}

	@Override
	public void amstradTapeWriting(AmstradTape tape) {
		// Subclasses can override this
	}

	@Override
	public void amstradTapeStoppedWriting(AmstradTape tape) {
		// Subclasses can override this
	}

}