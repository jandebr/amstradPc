package org.maia.amstrad.pc.event;

import java.util.EventListener;

public interface AmstradPcEventListener extends EventListener {

	void amstradPcEventDispatched(AmstradPcEvent event);

}