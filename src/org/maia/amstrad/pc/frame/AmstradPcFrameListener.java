package org.maia.amstrad.pc.frame;

import org.maia.util.GenericListener;

public interface AmstradPcFrameListener extends GenericListener {

	void popupMenuWillBecomeVisible(AmstradPcFrame frame);

	void popupMenuWillBecomeInvisible(AmstradPcFrame frame);

}