package org.maia.amstrad.pc;

import org.maia.amstrad.pc.display.AmstradAlternativeDisplaySource;

public class AmstradPcStateAdapter implements AmstradPcStateListener {

	protected AmstradPcStateAdapter() {
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
		// Subclasses can override this
	}

	@Override
	public void amstradPcDisplaySourceChanged(AmstradPc amstradPc,
			AmstradAlternativeDisplaySource alternativeDisplaySource) {
		// Subclasses can override this
	}

}