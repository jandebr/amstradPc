package org.maia.amstrad.basic;

import java.io.File;

import org.maia.amstrad.basic.locomotive.source.BasicSourceCode;
import org.maia.amstrad.program.AmstradProgramException;

public class BasicManipulatorTest {

	public BasicManipulatorTest() {
	}

	public static void main(String[] args) throws BasicSyntaxException, AmstradProgramException {
		BasicProgramFile program = new BasicProgramFile(new File("resources/test/manipulator/test.bas"));
		BasicSourceCode sourceCode = new BasicSourceCode(program.getPayload().asTextPayload().getText());
		System.out.println(sourceCode);
		System.out.println();
		System.out.println(sourceCode.toStringInParsedForm());
	}

}