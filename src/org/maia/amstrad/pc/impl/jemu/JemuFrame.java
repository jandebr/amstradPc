package org.maia.amstrad.pc.impl.jemu;

import org.maia.amstrad.pc.AmstradPcFrame;

public abstract class JemuFrame extends AmstradPcFrame {

	protected JemuFrame(JemuAmstradPc amstradPc, String title, boolean exitOnClose) {
		super(amstradPc, title, exitOnClose);
	}

}