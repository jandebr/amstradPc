package org.maia.amstrad.pc.display.browser;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.display.AmstradEmulatedDisplaySource;

public class ProgramBrowserDisplaySource extends AmstradEmulatedDisplaySource {

	public ProgramBrowserDisplaySource(AmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	protected void renderContent(AmstradDisplayCanvas canvas) {
		canvas.border(0).paper(7).pen(26);
		canvas.move(0, 0).draw(639, 399);
		canvas.move(0, 399).draw(639, 0);
	}

}