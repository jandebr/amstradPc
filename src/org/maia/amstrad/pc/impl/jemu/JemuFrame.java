package org.maia.amstrad.pc.impl.jemu;

import org.maia.amstrad.pc.frame.AmstradPcFrame;

public abstract class JemuFrame extends AmstradPcFrame {

	protected JemuFrame(JemuAmstradPc amstradPc, boolean exitOnClose) {
		super(amstradPc, "JavaCPC - Amstrad CPC Emulator", exitOnClose);
	}

}