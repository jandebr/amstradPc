package org.maia.amstrad;

import java.io.File;

import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;

public class AmstradMain {

	public static void main(String[] args) throws Exception {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.installMenuBar();
		if (args.length == 0) {
			amstradPc.start();
		} else if (args.length == 1) {
			amstradPc.launch(new File(args[0]));
		} else {
			System.err.println("Invalid startup arguments");
		}
	}

}