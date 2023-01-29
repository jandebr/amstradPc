package org.maia.amstrad.basic;

import java.io.File;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.load.AmstradProgramLoader;
import org.maia.amstrad.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.load.basic.staged.EndingBasicAction;
import org.maia.amstrad.load.basic.staged.EndingBasicCodeDisclosure;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class BasicStagingTest {

	public static void main(String[] args) throws AmstradProgramException {
		BasicStagingTest test = new BasicStagingTest();
		AmstradProgram program = new AmstradBasicProgramFile(new File("resources/test/staging/program-1.bas"));
		test.run(program);
	}

	public BasicStagingTest() {
	}

	public void run(AmstradProgram program) throws AmstradProgramException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		AmstradProgramLoader loader = AmstradProgramLoaderFactory.getInstance().createStagedBasicProgramLoader(
				amstradPc, new EndingBasicActionImpl(), EndingBasicCodeDisclosure.STAGED_CODE, true);
		amstradPc.start();
		AmstradProgramRuntime rt = loader.load(program);
		// rt.getAmstradPc().getBasicRuntime().sendKeyboardInputIfReady("LIST");
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