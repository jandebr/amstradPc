package org.maia.amstrad.gui.memory;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradWindowDisplaySource;

public class LocomotiveBasicMemoryDisplaySource extends AmstradWindowDisplaySource {

	public LocomotiveBasicMemoryDisplaySource(AmstradPc amstradPc) {
		super(amstradPc, "64K Basic Memory");
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		getAmstradPc().getMonitor().setMonitorMode(AmstradMonitorMode.COLOR);
		getAmstradPc().getMonitor().setMonitorBilinearEffect(false);
		getAmstradPc().getMonitor().setMonitorScanLinesEffect(false);
		canvas.border(1).paper(1);
	}

	@Override
	protected void renderWindowContent(AmstradDisplayCanvas canvas) {
		// TODO
		if (isItemListCursorBlinkOn()) {
			canvas.paper(1).pen(24).locate(1, 3).printChr(133);
		}
	}

}