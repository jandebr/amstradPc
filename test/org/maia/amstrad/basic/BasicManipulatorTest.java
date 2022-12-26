package org.maia.amstrad.basic;

import java.io.File;

import org.maia.amstrad.basic.locomotive.source.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.source.SourceTokenFactory;
import org.maia.amstrad.basic.locomotive.source.SourceTokenSequence;
import org.maia.amstrad.program.AmstradProgramException;

public class BasicManipulatorTest {

	public BasicManipulatorTest() {
	}

	public static void main(String[] args) throws BasicSyntaxException, AmstradProgramException {
		BasicProgramFile program = new BasicProgramFile(new File("resources/test/manipulator/test.bas"));
		BasicSourceCode sourceCode = new BasicSourceCode(program.getPayload().asTextPayload().getText());
		manipulate(sourceCode);
		System.out.println(sourceCode);
		System.out.println();
		System.out.println(sourceCode.toStringInParsedForm());
	}

	private static void manipulate(BasicSourceCode sourceCode) throws BasicSyntaxException {
		int ln = sourceCode.getLargestLineNumber();
		SourceTokenFactory stf = SourceTokenFactory.getInstance();
		SourceTokenSequence sequence = new SourceTokenSequence();
		sequence.append(stf.createLineNumber(ln + 5), stf.createBasicKeyword("END"));
		sourceCode.addLine(sequence.assemble());
	}

}