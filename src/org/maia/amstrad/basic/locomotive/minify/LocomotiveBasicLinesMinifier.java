package org.maia.amstrad.basic.locomotive.minify;

import java.util.Set;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;

public class LocomotiveBasicLinesMinifier extends LocomotiveBasicMinifier {

	public static final double MINIMUM_INTENSITY = 0;

	public static final double MAXIMUM_INTENSITY = 1.0;

	public static final double DEFAULT_INTENSITY = MINIMUM_INTENSITY;

	private static BasicSourceToken IF;

	private static BasicSourceToken REM;

	private static BasicSourceToken REM_SHORTHAND;

	private static BasicSourceToken SEP;

	static {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		try {
			IF = stf.createBasicKeyword("IF");
			REM = stf.createBasicKeyword("REM");
			REM_SHORTHAND = stf.createBasicKeyword("'");
			SEP = stf.createInstructionSeparator();
		} catch (BasicSyntaxException e) {
			e.printStackTrace();
		}
	}

	private double intensity;

	public LocomotiveBasicLinesMinifier() {
		this(DEFAULT_INTENSITY);
	}

	public LocomotiveBasicLinesMinifier(double intensity) {
		setIntensity(intensity);
	}

	@Override
	public void minify(BasicSourceCode sourceCode) throws BasicException {
		if (sourceCode instanceof LocomotiveBasicSourceCode) {
			Set<Integer> referencedLineNumbers = ((LocomotiveBasicSourceCode) sourceCode).getReferencedLineNumbers();
			removeNonReferencedRemarkOnlyLines(sourceCode, referencedLineNumbers);
			int li = 0;
			while (li < sourceCode.getLineCount() - 1) {
				BasicSourceCodeLine line = sourceCode.getLineByIndex(li);
				BasicSourceCodeLine nextLine = sourceCode.getLineByIndex(li + 1);
				if (canJoinLines(line, nextLine, referencedLineNumbers)) {
					BasicSourceTokenSequence sequence = line.parse();
					BasicSourceTokenSequence nextSequence = nextLine.parse();
					sequence.append(SEP);
					if (nextSequence.startsWithLineNumber()) {
						sequence.append(nextSequence.subSequence(1, nextSequence.size()));
					} else {
						sequence.append(nextSequence);
					}
					updateLine(sourceCode, sequence);
					sourceCode.removeLineNumber(nextLine.getLineNumber());
				} else {
					li++;
				}
			}
		}
	}

	protected boolean canJoinLines(BasicSourceCodeLine line, BasicSourceCodeLine nextLine,
			Set<Integer> referencedLineNumbers) throws BasicException {
		if (referencedLineNumbers.contains(nextLine.getLineNumber()))
			return false;
		BasicSourceTokenSequence sequence = line.parse();
		if (sequence.contains(IF) || sequence.contains(REM) || sequence.contains(REM_SHORTHAND))
			return false;
		int joinedLineLength = line.getText().length() + nextLine.getText().length()
				- String.valueOf(nextLine.getLineNumber()).length();
		if (joinedLineLength > getMaximumJoinedLineLength(line, nextLine))
			return false;
		return true;
	}

	protected int getMaximumJoinedLineLength(BasicSourceCodeLine line, BasicSourceCodeLine nextLine) {
		double r = 0.25 + 0.75 * getIntensity();
		return (int) Math.floor(r * getLineLengthUpperBound());
	}

	protected int getLineLengthUpperBound() {
		return LocomotiveBasicRuntime.MAXIMUM_LINE_LENGTH_CHARACTERS;
	}

	private void removeNonReferencedRemarkOnlyLines(BasicSourceCode sourceCode, Set<Integer> referencedLineNumbers)
			throws BasicException {
		for (BasicSourceCodeLine line : sourceCode) {
			if (!referencedLineNumbers.contains(line.getLineNumber())) {
				BasicSourceTokenSequence sequence = line.parse();
				if (sequence.startsWithLineNumber()) {
					int i = sequence.getIndexFollowingWhitespace(1);
					if (i >= 0 && (sequence.get(i).equals(REM) || sequence.get(i).equals(REM_SHORTHAND))) {
						sourceCode.removeLineNumber(line.getLineNumber());
					}
				}
			}
		}
	}

	public double getIntensity() {
		return intensity;
	}

	public void setIntensity(double intensity) {
		this.intensity = Math.max(Math.min(intensity, MAXIMUM_INTENSITY), MINIMUM_INTENSITY);
	}

}