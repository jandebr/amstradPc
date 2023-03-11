package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;
import org.maia.amstrad.basic.locomotive.token.VariableToken;

public class PrintStreamCommand extends FileCommand {

	private LiteralQuotedToken literalString;

	private NumericToken literalNumber;

	private VariableToken variable;

	private String variableArrayIndexString;

	public PrintStreamCommand() {
	}

	private PrintStreamCommand(LiteralQuotedToken literalString) {
		setLiteralString(literalString);
	}

	private PrintStreamCommand(NumericToken literalNumber) {
		setLiteralNumber(literalNumber);
	}

	private PrintStreamCommand(VariableToken variable) {
		setVariable(variable);
	}

	public static PrintStreamCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		PrintStreamCommand command = null;
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		int i = sequence.getFirstIndexOf(stf.createLiteral("#"));
		if (i >= 0 && i < sequence.size() - 1) {
			if (sequence.get(i + 1).equals(stf.createPositiveIntegerSingleDigitDecimal(9))) {
				i += 2;
				int jVar = sequence.getNextIndexOf(VariableToken.class, i);
				int jStr = sequence.getNextIndexOf(LiteralQuotedToken.class, i);
				int jNum = sequence.getNextIndexOf(NumericToken.class, i);
				if (jVar >= 0 && (jVar < jStr || jStr < 0) && (jVar < jNum || jNum < 0)) {
					// variable
					command = new PrintStreamCommand((VariableToken) sequence.get(jVar));
					// variable is indexed?
					if (jVar < sequence.size() - 1 && sequence.get(jVar + 1).equals(stf.createLiteral("("))) {
						int k = sequence.getNextIndexOf(stf.createLiteral(")"), jVar + 2);
						if (k >= 0) {
							command.setVariableArrayIndexString(sequence.subSequence(jVar + 1, k + 1).getSourceCode());
						}
					}
				} else if (jStr >= 0 && (jStr < jNum || jNum < 0)) {
					// literal string
					command = new PrintStreamCommand((LiteralQuotedToken) sequence.get(jStr));
				} else if (jNum >= 0) {
					// literal number
					command = new PrintStreamCommand((NumericToken) sequence.get(jNum));
				}
			}
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PrintStreamCommand");
		if (hasVariable()) {
			sb.append(" writing ");
			sb.append(getVariable().getSourceFragment());
			if (isVariableIndexed()) {
				sb.append(getVariableArrayIndexString());
			}
		} else if (hasLiteralString()) {
			sb.append(" writing ");
			sb.append(getLiteralString().getSourceFragment());
		} else if (hasLiteralNumber()) {
			sb.append(" writing ");
			sb.append(getLiteralNumber().getSourceFragment());
		}
		return sb.toString();
	}

	public boolean hasLiteralString() {
		return getLiteralString() != null;
	}

	public boolean hasLiteralNumber() {
		return getLiteralNumber() != null;
	}

	public boolean hasVariable() {
		return getVariable() != null;
	}

	public boolean isVariableIndexed() {
		return getVariableArrayIndexString() != null;
	}

	public LiteralQuotedToken getLiteralString() {
		return literalString;
	}

	private void setLiteralString(LiteralQuotedToken literalString) {
		this.literalString = literalString;
	}

	public NumericToken getLiteralNumber() {
		return literalNumber;
	}

	private void setLiteralNumber(NumericToken literalNumber) {
		this.literalNumber = literalNumber;
	}

	public VariableToken getVariable() {
		return variable;
	}

	private void setVariable(VariableToken variable) {
		this.variable = variable;
	}

	public String getVariableArrayIndexString() {
		return variableArrayIndexString;
	}

	private void setVariableArrayIndexString(String indexString) {
		this.variableArrayIndexString = indexString;
	}

}