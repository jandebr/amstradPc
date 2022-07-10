package org.maia.amstrad.pc.basic;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;

public class BasicCompilerTest {

	public BasicCompilerTest() {
	}

	public static void main(String[] args) throws IOException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.installSimpleMenuBar();
		amstradPc.start(true);
		BasicRuntime basicRuntime = amstradPc.getBasicRuntime();
		basicRuntime.loadSourceCodeFromFile(new File("test.bas"));
		basicRuntime.list();
	}

}