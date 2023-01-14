package org.maia.amstrad.basic;

import java.io.File;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.loader.AmstradProgramLoader;
import org.maia.amstrad.program.loader.AmstradProgramLoaderFactory;

public class BasicStagingTest {

	public static void main(String[] args) throws AmstradProgramException {
		BasicStagingTest test = new BasicStagingTest();
		test.run();
	}

	public BasicStagingTest() {
	}

	public void run() throws AmstradProgramException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		AmstradProgram program = new AmstradBasicProgramFile(new File("resources/test/staging/test.bas"));
		AmstradProgramLoader loader = AmstradProgramLoaderFactory.getInstance()
				.createStagedBasicProgramLoader(amstradPc);
		amstradPc.start();
		AmstradProgramRuntime rt = loader.load(program);
		rt.run();
	}

}