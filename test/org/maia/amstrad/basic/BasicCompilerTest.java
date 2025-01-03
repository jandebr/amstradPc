package org.maia.amstrad.basic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.AmstradFileType;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCodeComparator;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCodeComparator.ComparisonResult;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCodeFormatter;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicCompiler;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.io.util.IOUtils;
import org.maia.util.SystemUtils;

public class BasicCompilerTest {

	public BasicCompilerTest() {
	}

	public static void main(String[] args) throws Exception {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		LocomotiveBasicCompiler compiler = new LocomotiveBasicCompiler();
		File dir = new File("resources/test/compiler");
		PrintWriter out = new PrintWriter(new File(dir, "outcome.txt"));
		testFilesInDirectory(dir, amstradPc, compiler, out);
		// testFile(new File(dir, "test-data.bas"), amstradPc, compiler, out);
		out.close();
		amstradPc.terminate();
	}

	private static void testFilesInDirectory(File dir, AmstradPc amstradPc, LocomotiveBasicCompiler compiler,
			PrintWriter out) throws IOException, BasicException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				testFilesInDirectory(file, amstradPc, compiler, out);
			} else if (AmstradFileType.BASIC_SOURCE_CODE_FILE.matches(file)) {
				testFile(file, amstradPc, compiler, out);
			}
		}
	}

	private static void testFile(File basicFile, AmstradPc amstradPc, LocomotiveBasicCompiler compiler, PrintWriter out)
			throws IOException, BasicException {
		System.out.println("Testing " + basicFile.getPath() + "...");
		out.println(">> Testing " + basicFile.getPath());
		loadFileWithoutCompiler(basicFile, amstradPc);
		LocomotiveBasicByteCode referenceByteCode = (LocomotiveBasicByteCode) amstradPc.getBasicRuntime()
				.exportByteCode();
		LocomotiveBasicByteCode compiledByteCode = compiler
				.compile(new LocomotiveBasicSourceCode(IOUtils.readTextFileContents(basicFile)));
		outputByteCodeComparison(referenceByteCode, compiledByteCode, out);
		out.println();
		out.flush();
	}

	private static void loadFileWithoutCompiler(File basicFile, AmstradPc amstradPc) throws IOException {
		if (amstradPc.isStarted()) {
			amstradPc.reboot(true, true);
		} else {
			amstradPc.start(true, true);
		}
		SystemUtils.sleep(100);
		amstradPc.getKeyboard().typeFileContents(basicFile);
	}

	private static void outputByteCodeComparison(LocomotiveBasicByteCode firstByteCode,
			LocomotiveBasicByteCode secondByteCode, PrintWriter out) {
		ComparisonResult cr = new LocomotiveBasicByteCodeComparator().compare(firstByteCode, secondByteCode);
		if (cr.isIdentical()) {
			out.println("Identical");
		} else {
			out.println("Different");
			out.println("-- reference");
			LocomotiveBasicByteCodeFormatter fmt = new LocomotiveBasicByteCodeFormatter();
			out.print(fmt.format(firstByteCode, cr.getFirstDifferences(), true));
			out.println("-- compiled");
			out.print(fmt.format(secondByteCode, cr.getSecondDifferences(), true));
		}
	}

}