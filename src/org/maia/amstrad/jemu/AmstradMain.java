package org.maia.amstrad.jemu;

import java.io.File;
import java.io.IOException;

public class AmstradMain {

	public static void main(String[] args) throws IOException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.installSimpleMenuBar();
		if (args.length == 0) {
			amstradPc.start(false);
		} else if (args.length == 1) {
			amstradPc.launch(new File(args[0]));
		} else if (args.length == 2 && amstradPc.isBasicSourceFile(new File(args[0]))
				&& amstradPc.isSnapshotFile(new File(args[1]))) {
			makeRunnableSnapshot(amstradPc, new File(args[0]), new File(args[1]));
		} else {
			System.err.println("Invalid startup arguments");
		}
	}

	public static void makeRunnableSnapshot(AmstradPc amstradPc, File basicFile, File snapshotFile) throws IOException {
		amstradPc.start(true);
		AmstradPcBasicRuntime basic = amstradPc.getBasicRuntime();
		basic.load(basicFile);
		basic.keyboardEnter("CLS: INPUT \"Press [enter] to start\",A$: RUN");
		sleep(500L);
		amstradPc.saveSnapshot(snapshotFile);
	}

	private static void sleep(long milliseconds) {
		if (milliseconds > 0L) {
			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
			}
		}
	}

}