package org.maia.amstrad.jemu;

import java.io.File;
import java.io.IOException;

public class AmstradMain {

	public static void main(String[] args) throws IOException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		if (args.length == 0) {
			start(amstradPc);
		} else if (args.length == 1) {
			launch(amstradPc, new File(args[0]));
		} else if (args.length == 2 && amstradPc.isBasicSourceFile(new File(args[0]))
				&& amstradPc.isSnapshotFile(new File(args[1]))) {
			makeRunnableSnapshot(amstradPc, new File(args[0]), new File(args[1]));
		} else {
			System.err.println("Invalid startup arguments");
		}
	}

	public static void start(AmstradPc amstradPc) {
		amstradPc.start(true);
	}

	public static void launch(AmstradPc amstradPc, File file) throws IOException {
		amstradPc.launch(file);
	}

	public static void makeRunnableSnapshot(AmstradPc amstradPc, File basicFile, File snapshotFile) throws IOException {
		amstradPc.start(true);
		AmstradPcBasicRuntime basic = amstradPc.getBasicRuntime();
		basic.load(basicFile);
		basic.keyboardEnter("CLS: INPUT \"Press [enter] to start\",A$: RUN");
		amstradPc.saveSnapshot(snapshotFile);
	}

}