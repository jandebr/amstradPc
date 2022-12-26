package org.maia.amstrad.basic;

import java.io.File;

import org.maia.amstrad.basic.locomotive.source.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.source.BasicSourceCodeLine;
import org.maia.amstrad.basic.locomotive.source.LineNumberToken;
import org.maia.amstrad.basic.locomotive.source.SourceToken;
import org.maia.amstrad.basic.locomotive.source.SourceTokenFactory;
import org.maia.amstrad.basic.locomotive.source.SourceTokenSequence;
import org.maia.amstrad.io.AmstradFileType;
import org.maia.amstrad.program.AmstradProgramException;

public class BasicManipulatorTest {

	public BasicManipulatorTest() {
	}

	public static void main(String[] args) throws BasicSyntaxException, AmstradProgramException {
		// manipulate();
		scanFilesInDirectory(new File("D:/tools/JavaCPC/programs"));
	}

	private static void manipulate() throws BasicSyntaxException, AmstradProgramException {
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
		BasicSourceCode sourceCode = new BasicSourceCode(
				new BasicProgramFile(basicFile).getPayload().asTextPayload().getText());
		scanSourceCode(sourceCode);
		System.out.println();
	}

	private static void scanSourceCode(BasicSourceCode sourceCode) throws BasicSyntaxException {
		scanSourceCodeForGotoSelf(sourceCode);
	}

	private static void scanSourceCodeForGotoSelf(BasicSourceCode sourceCode) throws BasicSyntaxException {
		SourceToken gotoToken = SourceTokenFactory.getInstance().createBasicKeyword("GOTO");
		SourceToken ifToken = SourceTokenFactory.getInstance().createBasicKeyword("IF");
		for (BasicSourceCodeLine line : sourceCode) {
			SourceTokenSequence sequence = line.parse();
			if (!sequence.contains(ifToken)) {
				int i = sequence.getFirstIndexOf(gotoToken);
				while (i >= 0) {
					i = sequence.getIndexFollowingWhitespace(i + 1);
					if (i >= 0 && sequence.get(i) instanceof LineNumberToken) {
						int ln = ((LineNumberToken) sequence.get(i)).getValue();
						if (ln == line.getLineNumber()) {
							System.out.println(line);
						}
						i = sequence.getNextIndexOf(gotoToken, i + 1);
					}
				}
			}
		}
	}

}