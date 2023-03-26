package org.maia.amstrad;

import java.io.File;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradProgramStoredInFile;

public class AmstradMain {

	public static void main(String[] args) throws Exception {
		if (AmstradFactory.getInstance().getAmstradContext().isKioskMode()) {
			AmstradKiosk.main(args);
		} else {
			AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
			AmstradPcFrame frame = amstradPc.displayInFrame(true);
			frame.installMenu();
			if (args.length == 0) {
				amstradPc.start();
			} else if (args.length == 1) {
				amstradPc.launch(new AmstradProgramStoredInFile(new File(args[0])));
			} else {
				System.err.println("Invalid startup arguments");
				System.exit(1);
			}
		}
	}

}