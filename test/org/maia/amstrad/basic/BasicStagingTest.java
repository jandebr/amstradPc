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
import org.maia.amstrad.program.loader.basic.staged.EndingBasicAction;
import org.maia.amstrad.program.loader.basic.staged.EndingBasicCodeDisclosure;

public class BasicStagingTest {

	public static void main(String[] args) throws AmstradProgramException {
		BasicStagingTest test = new BasicStagingTest();
		test.run();
	}

	public BasicStagingTest() {
	}

	public void run() throws AmstradProgramException {
		File basFile = new File("resources/test/staging/test.bas");
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		AmstradProgram program = new AmstradBasicProgramFile(basFile);
		AmstradProgramLoader loader = AmstradProgramLoaderFactory.getInstance().createStagedBasicProgramLoader(
				amstradPc, new EndingBasicActionImpl(), EndingBasicCodeDisclosure.STAGED_CODE, true);
		amstradPc.start();
		AmstradProgramRuntime rt = loader.load(program);
		rt.run();
	}

	private static class EndingBasicActionImpl implements EndingBasicAction {

		public EndingBasicActionImpl() {
		}

		@Override
		public void perform(AmstradProgramRuntime programRuntime) {
			System.out.println("ENDED " + programRuntime.getProgram().getProgramName());
		}

	}

}