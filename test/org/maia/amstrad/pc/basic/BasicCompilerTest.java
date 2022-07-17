package org.maia.amstrad.pc.basic;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.maia.amstrad.pc.AmstradContext;
import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.basic.BasicByteCodeComparator.ComparisonResult;
import org.maia.amstrad.pc.basic.locomotive.LocomotiveBasicCompiler;

public class BasicCompilerTest {

	public BasicCompilerTest() {
	}

	public static void main(String[] args) throws IOException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		LocomotiveBasicCompiler compiler = new LocomotiveBasicCompiler();
		File dir = new File("resources/test/compiler");
		PrintWriter out = new PrintWriter(new File(dir, "outcome.txt"));
		testFilesInDirectory(dir, amstradPc, compiler, out);
		// testFile(new File(dir, "airwulf.bas"), amstradPc, compiler, out);
		out.close();
		amstradPc.terminate();
	}

	private static void testFilesInDirectory(File dir, AmstradPc amstradPc, BasicCompiler compiler, PrintWriter out)
			throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				testFilesInDirectory(file, amstradPc, compiler, out);
			} else if (amstradPc.isBasicSourceFile(file)) {
				testFile(file, amstradPc, compiler, out);
			}
		}
	}

	private static void testFile(File basicFile, AmstradPc amstradPc, BasicCompiler compiler, PrintWriter out)
			throws IOException {
		out.println(">> Testing " + basicFile.getPath());
		loadFileWithoutCompiler(basicFile, amstradPc);
		byte[] referenceByteCode = amstradPc.getBasicRuntime().exportByteCode();
		byte[] compiledByteCode = compiler.compile(AmstradContext.readTextFileContents(basicFile));
		outputByteCodeComparison(referenceByteCode, compiledByteCode, out);
		out.println();
		out.flush();
	}

	private static void loadFileWithoutCompiler(File basicFile, AmstradPc amstradPc) throws IOException {
		if (amstradPc.isStarted()) {
			amstradPc.reboot(true);
		} else {
			amstradPc.start(true);
		}
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