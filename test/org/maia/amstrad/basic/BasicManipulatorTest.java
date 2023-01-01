package org.maia.amstrad.basic;

import java.io.File;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCodeLine;
import org.maia.amstrad.basic.locomotive.LocomotiveSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.LineNumberReferenceToken;
import org.maia.amstrad.io.AmstradFileType;
import org.maia.amstrad.program.AmstradProgramException;

public class BasicManipulatorTest {

	private static BasicSourceToken GOTO;

	private static BasicSourceToken IF;

	static {
		LocomotiveSourceTokenFactory stf = LocomotiveSourceTokenFactory.getInstance();
		try {
			GOTO = stf.createBasicKeyword("GOTO");
			IF = stf.createBasicKeyword("IF");
		} catch (BasicSyntaxException e) {
			e.printStackTrace();
		}
	}

	public BasicManipulatorTest() {
	}

	public static void main(String[] args) throws BasicSyntaxException, AmstradProgramException {
		// manipulate();
		scanFilesInDirectory(new File("D:/tools/JavaCPC/programs"));
	}

	private static void manipulate() throws BasicSyntaxException, AmstradProgramException {
		BasicProgramFile program = new BasicProgramFile(new File("resources/test/manipulator/test.bas"));
		BasicSourceCode sourceCode = new LocomotiveBasicSourceCode(program.getPayload().asTextPayload().getText());
		manipulate(sourceCode);
		System.out.println(sourceCode);
		System.out.println();
		System.out.println(sourceCode.toStringInParsedForm());
	}

	private static void manipulate(BasicSourceCode sourceCode) throws BasicSyntaxException {
		int ln = sourceCode.getLargestLineNumber();
		LocomotiveSourceTokenFactory stf = LocomotiveSourceTokenFactory.getInstance();
		BasicSourceTokenSequence sequence = new BasicSourceTokenSequence();
		sequence.append(stf.createLineNumber(ln + 5), stf.createBasicKeyword("END"));
		sourceCode.addLine(new LocomotiveBasicSourceCodeLine(sequence.getSourceCode()));
	}

	private static void scanFilesInDirectory(File dir) throws BasicSyntaxException, AmstradProgramException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				scanFilesInDirectory(file);
			} else if (AmstradFileType.BASIC_SOURCE_CODE_FILE.matches(file)) {
				scanFile(file);
			}
		}
	}

	private static void scanFile(File basicFile) throws BasicSyntaxException, AmstradProgramException {
		System.out.println("Scanning " + basicFile.getPath() + "...");
		BasicSourceCode sourceCode = new LocomotiveBasicSourceCode(
				new BasicProgramFile(basicFile).getPayload().asTextPayload().getText());
		scanSourceCode(sourceCode);
		System.out.println();
	}

	private static void scanSourceCode(BasicSourceCode sourceCode) throws BasicSyntaxException {
		scanSourceCodeForGotoSelf(sourceCode);
	}

	private static void scanSourceCodeForGotoSelf(BasicSourceCode sourceCode) throws BasicSyntaxException {
		for (BasicSourceCodeLine line : sourceCode) {
			BasicSourceTokenSequence sequence = line.parse();
			if (!sequence.contains(IF)) {
				int i = sequence.getFirstIndexOf(GOTO);
				while (i >= 0) {
					i = sequence.getIndexFollowingWhitespace(i + 1);
					if (i >= 0 && sequence.get(i) instanceof LineNumberReferenceToken) {
						int ln = ((LineNumberReferenceToken) sequence.get(i)).getLineNumber();
						if (ln == line.getLineNumber()) {
							System.out.println(line);
						}
						i = sequence.getNextIndexOf(GOTO, i + 1);
					}
				}
			}
		}
	}

}