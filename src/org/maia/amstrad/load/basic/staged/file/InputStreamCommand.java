package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;

public class InputStreamCommand extends FileCommand {

	private StringTypedVariableToken variable;

	private String variableArrayIndexString;

	public InputStreamCommand() {
		this(null);
	}

	private InputStreamCommand(StringTypedVariableToken variable) {
		setVariable(variable);
	}

	public static InputStreamCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		InputStreamCommand command = null;
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		int i = sequence.getFirstIndexOf(stf.createLiteral("#"));
		if (i >= 0 && i < sequence.size() - 1) {
			if (sequence.get(i + 1).equals(stf.createPositiveIntegerSingleDigitDecimal(9))) {
				i += 2;
				int j = sequence.getNextIndexOf(StringTypedVariableToken.class, i);
				if (j >= 0) {
					command = new InputStreamCommand((StringTypedVariableToken) sequence.get(j));
					// array index?
					if (j < sequence.size() - 1 && sequence.get(j + 1).equals(stf.createLiteral("("))) {
						int k = sequence.getNextIndexOf(stf.createLiteral(")"), j + 2);
						if (k >= 0) {
							command.setVariableArrayIndexString(sequence.subSequence(j + 1, k + 1).getSourceCode());
						}
					}
				}
			}
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("InputStreamCommand");
		if (hasVariable()) {
			sb.append(" reading into ");
			sb.append(getVariable().getSourceFragment());
			if (isVariableIndexed()) {
				sb.append(getVariableArrayIndexString());
			}
		}
		return sb.toString();
	}

	public boolean hasVariable() {
		return getVariable() != null;
	}

	public boolean isVariableIndexed() {
		return getVariableArrayIndexString() != null;
	}

	public StringTypedVariableToken getVariable() {
		return variable;
	}

	private void setVariable(StringTypedVariableToken variable) {
		this.variable = variable;
	}

	public String getVariableArrayIndexString() {
		return variableArrayIndexString;
	}

	private void setVariableArrayIndexString(String indexString) {
		this.variableArrayIndexString = indexString;
	}

}