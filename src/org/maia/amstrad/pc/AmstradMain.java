package org.maia.amstrad.pc;

import java.io.File;

public class AmstradMain {

	public static void main(String[] args) throws Exception {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.installMenuBar();
		if (args.length == 0) {
			amstradPc.start(true);
		} else if (args.length == 1) {
			amstradPc.launch(new File(args[0]));
		} else {
			System.err.println("Invalid startup arguments");
		}
	}

}