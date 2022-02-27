package org.maia.amstrad.jemu;

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

}