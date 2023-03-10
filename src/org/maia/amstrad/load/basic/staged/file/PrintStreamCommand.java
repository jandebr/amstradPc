package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.VariableToken;

public class PrintStreamCommand extends FileCommand {

	private LiteralQuotedToken literal;

	private VariableToken variable;

	private String variableArrayIndexString;

	public PrintStreamCommand() {
	}

	private PrintStreamCommand(LiteralQuotedToken literal) {
		setLiteral(literal);
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
				int j = sequence.getNextIndexOf(VariableToken.class, i);
				int k = sequence.getNextIndexOf(LiteralQuotedToken.class, i);
				if (j >= 0 && (j < k || k < 0)) {
					command = new PrintStreamCommand((VariableToken) sequence.get(j));
					// array index?
					if (j < sequence.size() - 1 && sequence.get(j + 1).equals(stf.createLiteral("("))) {
						int l = sequence.getNextIndexOf(stf.createLiteral(")"), j + 2);
						if (l >= 0) {
							command.setVariableArrayIndexString(sequence.subSequence(j + 1, l + 1).getSourceCode());
						}
					}
				} else if (k >= 0) {
					command = new PrintStreamCommand((LiteralQuotedToken) sequence.get(k));
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
		} else if (hasLiteral()) {
			sb.append(" writing ");
			sb.append(getLiteral().getSourceFragment());
		}
		return sb.toString();
	}

	public boolean hasLiteral() {
		return getLiteral() != null;
	}

	public boolean hasVariable() {
		return getVariable() != null;
	}

	public boolean isVariableIndexed() {
		return getVariableArrayIndexString() != null;
	}

	public LiteralQuotedToken getLiteral() {
		return literal;
	}

	private void setLiteral(LiteralQuotedToken literal) {
		this.literal = literal;
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