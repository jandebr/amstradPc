package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;

public class InputStreamCommand extends FileCommand {

	private StringTypedVariableToken variable;

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
			sb.append(getVariable());
		}
		return sb.toString();
	}

	public boolean hasVariable() {
		return getVariable() != null;
	}

	public StringTypedVariableToken getVariable() {
		return variable;
	}

	private void setVariable(StringTypedVariableToken variable) {
		this.variable = variable;
	}

}