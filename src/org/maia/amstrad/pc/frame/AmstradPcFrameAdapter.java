package org.maia.amstrad.pc.frame;

public abstract class AmstradPcFrameAdapter implements AmstradPcFrameListener {

	protected AmstradPcFrameAdapter() {
	}

	@Override
	public void popupMenuWillBecomeVisible(AmstradPcFrame frame) {
		// Subclasses can override this
	}

	@Override
	public void popupMenuWillBecomeInvisible(AmstradPcFrame frame) {
		// Subclasses can override this
	}

}