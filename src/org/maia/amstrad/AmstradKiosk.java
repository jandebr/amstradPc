package org.maia.amstrad;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.AmstradPcStateAdapter;
import org.maia.amstrad.pc.monitor.AmstradMonitor;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;

public class AmstradKiosk extends AmstradPcStateAdapter {

	public AmstradKiosk() {
	}

	public static void main(String[] args) {
		new AmstradKiosk().startAmstradPcInKioskMode();
	}

	public void startAmstradPcInKioskMode() {
		AmstradFactory.getInstance().getAmstradContext().setKioskMode(true);
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradMonitor monitor = amstradPc.getMonitor();
		monitor.setMonitorMode(AmstradMonitorMode.COLOR);
		monitor.setWindowAlwaysOnTop(true);
		amstradPc.addStateListener(this);
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.installMenu();
		amstradPc.start();
	}

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		AmstradFactory.getInstance().getAmstradContext().showProgramBrowser(amstradPc);
		amstradPc.getMonitor().makeWindowFullscreen();
	}

}