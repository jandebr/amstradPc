package org.maia.amstrad.basic;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.basic.locomotive.minify.LocomotiveBasicMinifierFactory;
import org.maia.amstrad.util.AmstradIO;

public class BasicMinifierTest {

	public static void main(String[] args) throws BasicException, IOException {
		BasicMinifierTest test = new BasicMinifierTest();
		File dir = new File("resources/test/minify");
		test.run(new File(dir, "test.bas"), new File(dir, "test-minified.bas"));
	}

	private BasicMinifierTest() {
	}

	public void run(File input, File output) throws BasicException, IOException {
		BasicSourceCode sourceCode = new LocomotiveBasicSourceCode(AmstradIO.readTextFileContents(input));
		BasicMinifier minifier = LocomotiveBasicMinifierFactory.getInstance()
				.createMinifier(LocomotiveBasicMinifierFactory.LEVEL_ULTRA);
		minifier.minify(sourceCode);
		AmstradIO.writeTextFileContents(output, sourceCode.getText());
	}

}