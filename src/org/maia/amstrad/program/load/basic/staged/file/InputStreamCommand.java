package org.maia.amstrad.program.load.basic.staged.file;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.VariableToken;

public class InputStreamCommand extends FileCommand {

	private List<Argument> arguments;

	public InputStreamCommand() {
		this.arguments = new Vector<Argument>();
	}

	public static InputStreamCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		InputStreamCommand command = null;
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		int i = sequence.getFirstIndexOf(stf.createLiteral("#"));
		if (i >= 0 && i < sequence.size() - 1) {
			if (sequence.get(i + 1).equals(stf.createPositiveIntegerSingleDigitDecimal(9))) {
				i += 2;
				command = new InputStreamCommand();
				Argument arg;
				do {
					arg = null;
					int j = sequence.getNextIndexOf(VariableToken.class, i);
					if (j >= 0) {
						arg = new Argument((VariableToken) sequence.get(j));
						i = j + 1;
						// array index?
						if (j < sequence.size() - 1 && sequence.get(j + 1).equals(stf.createLiteral("("))) {
							int k = sequence.getNextIndexOf(stf.createLiteral(")"), j + 2);
							if (k >= 0) {
								arg.setVariableArrayIndexString(sequence.subSequence(j + 1, k + 1).getSourceCode());
								i = k + 1;
							}
						}
					}
					if (arg != null)
						command.addArgument(arg);
				} while (arg != null);
			}
		}
		return command;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("InputStreamCommand");
		for (int i = 0; i < getArguments().size(); i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(' ');
			sb.append(getArguments().get(i).toString());
		}
		return sb.toString();
	}

	private void addArgument(Argument arg) {
		getArguments().add(arg);
	}

	public List<Argument> getArguments() {
		return arguments;
	}

	public static class Argument {

		private VariableToken variable;

		private String variableArrayIndexString;

		public Argument(VariableToken variable) {
			setVariable(variable);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(getVariable().getSourceFragment());
			if (isVariableIndexed()) {
				sb.append(getVariableArrayIndexString());
			}
			return sb.toString();
		}

		public boolean isVariableIndexed() {
			return getVariableArrayIndexString() != null;
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

		public void setVariableArrayIndexString(String indexString) {
			this.variableArrayIndexString = indexString;
		}

	}

}