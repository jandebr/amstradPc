package org.maia.amstrad.basic;

import java.io.File;
import java.util.Collection;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.load.AmstradProgramLoader;
import org.maia.amstrad.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.load.basic.staged.EndingBasicAction;
import org.maia.amstrad.load.basic.staged.EndingBasicCodeDisclosure;
import org.maia.amstrad.load.basic.staged.file.DiscoveredFileReference;
import org.maia.amstrad.load.basic.staged.file.FileReferenceDiscoveryService;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class BasicStagingTest {

	public static void main(String[] args) throws AmstradProgramException {
		File dir = new File("resources/test/staging");
		AmstradFactory fac = AmstradFactory.getInstance();
		BasicStagingTest test = new BasicStagingTest();
		// test.run(fac.createBasicProgram(new File(dir, "looprun-1.bas")));
		test.run(fac.createBasicDescribedProgram(new File(dir, "chainmerge-1.bas"), new File(dir, "chainmerge-1.amd")));
		// test.discoverFileReferences(fac.createBasicProgram(new File(dir, "filerefs.bas")));
	}

	private BasicStagingTest() {
	}

	public void run(AmstradProgram program) throws AmstradProgramException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		AmstradProgramLoader loader = AmstradProgramLoaderFactory.getInstance().createStagedBasicProgramLoader(
				amstradPc, new EndingBasicActionImpl(), EndingBasicCodeDisclosure.STAGED_CODE, true);
		amstradPc.start();
		AmstradProgramRuntime rt = loader.load(program);
		// amstradPc.getBasicRuntime().sendKeyboardInputIfReady("LIST");
		rt.run();
	}

	public void discoverFileReferences(AmstradProgram program) throws AmstradProgramException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		Collection<DiscoveredFileReference> refs = new FileReferenceDiscoveryService(amstradPc).discover(program);
		for (DiscoveredFileReference ref : refs) {
			System.out.println(ref);
		}
		amstradPc.terminate();
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