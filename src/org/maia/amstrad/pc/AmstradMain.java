package org.maia.amstrad.pc;

import java.io.File;
import java.io.IOException;

public class AmstradMain {

	public static void main(String[] args) throws IOException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.installSimpleMenuBar();
		if (args.length == 0) {
			amstradPc.start(true);
		} else if (args.length == 1) {
			amstradPc.launch(new File(args[0]));
		} else {
			System.err.println("Invalid startup arguments");
		}
	}

}