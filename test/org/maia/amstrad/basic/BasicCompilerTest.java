package org.maia.amstrad.basic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.maia.amstrad.basic.BasicByteCodeComparator.ComparisonResult;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicCompiler;
import org.maia.amstrad.io.AmstradFileType;
import org.maia.amstrad.io.AmstradIO;
import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.util.AmstradUtils;

public class BasicCompilerTest {

	public BasicCompilerTest() {
	}

	public static void main(String[] args) throws Exception {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		BasicCompiler compiler = new LocomotiveBasicCompiler();
		File dir = new File("resources/test/compiler");
		PrintWriter out = new PrintWriter(new File(dir, "outcome.txt"));
		testFilesInDirectory(dir, amstradPc, compiler, out);
		// testFile(new File(dir, "test-lineNr.bas"), amstradPc, compiler, out);
		out.close();
		amstradPc.terminate();
	}

	private static void testFilesInDirectory(File dir, AmstradPc amstradPc, BasicCompiler compiler, PrintWriter out)
			throws IOException, BasicSyntaxException {
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

	private static void testFile(File basicFile, AmstradPc amstradPc, BasicCompiler compiler, PrintWriter out)
			throws IOException, BasicSyntaxException {
		System.out.println("Testing " + basicFile.getPath() + "...");
		out.println(">> Testing " + basicFile.getPath());
		loadFileWithoutCompiler(basicFile, amstradPc);
		byte[] referenceByteCode = amstradPc.getBasicRuntime().exportByteCode();
		byte[] compiledByteCode = compiler.compile(AmstradIO.readTextFileContents(basicFile));
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
		AmstradUtils.sleep(100);
		amstradPc.getBasicRuntime().keyboardTypeFileContents(basicFile);
	}

	private static void outputByteCodeComparison(byte[] firstByteCode, byte[] secondByteCode, PrintWriter out) {
		ComparisonResult cr = new BasicByteCodeComparator().compare(firstByteCode, secondByteCode);
		if (cr.isIdentical()) {
			out.println("Identical");
		} else {
			out.println("Different");
			out.println("-- reference");
			BasicByteCodeFormatter fmt = new BasicByteCodeFormatter();
			out.print(fmt.format(firstByteCode, cr.getFirstDifferences(), true));
			out.println("-- compiled");
			out.print(fmt.format(secondByteCode, cr.getSecondDifferences(), true));
		}
	}

	private static void printByteCode(byte[] byteCode) {
		System.out.println(new BasicByteCodeFormatter().format(byteCode));
	}

}