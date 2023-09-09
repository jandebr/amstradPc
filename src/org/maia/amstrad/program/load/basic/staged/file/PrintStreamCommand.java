package org.maia.amstrad.program.load.basic.staged.file;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;
import org.maia.amstrad.basic.locomotive.token.VariableToken;

public class PrintStreamCommand extends FileCommand {

	private List<Argument> arguments;

	public PrintStreamCommand() {
		this.arguments = new Vector<Argument>();
	}

	public static PrintStreamCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		PrintStreamCommand command = null;
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		int i = sequence.getFirstIndexOf(stf.createLiteral("#"));
		if (i >= 0 && i < sequence.size() - 1) {
			if (sequence.get(i + 1).equals(stf.createPositiveIntegerSingleDigitDecimal(9))) {
				i += 2;
				command = new PrintStreamCommand();
				Argument arg;
				do {
					arg = null;
					int jVar = sequence.getNextIndexOf(VariableToken.class, i);
					int jStr = sequence.getNextIndexOf(LiteralQuotedToken.class, i);
					int jNum = sequence.getNextIndexOf(NumericToken.class, i);
					if (jVar >= 0 && (jVar < jStr || jStr < 0) && (jVar < jNum || jNum < 0)) {
						// variable
						arg = new Argument((VariableToken) sequence.get(jVar));
						i = jVar + 1;
						// variable is indexed?
						if (jVar < sequence.size() - 1 && sequence.get(jVar + 1).equals(stf.createLiteral("("))) {
							int k = sequence.getNextIndexOf(stf.createLiteral(")"), jVar + 2);
							if (k >= 0) {
								arg.setVariableArrayIndexString(sequence.subSequence(jVar + 1, k + 1).getSourceCode());
								i = k + 1;
							}
						}
					} else if (jStr >= 0 && (jStr < jNum || jNum < 0)) {
						// literal string
						arg = new Argument((LiteralQuotedToken) sequence.get(jStr));
						i = jStr + 1;
					} else if (jNum >= 0) {
						// literal number
						arg = new Argument((NumericToken) sequence.get(jNum));
						i = jNum + 1;
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
		sb.append("PrintStreamCommand");
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

		private LiteralQuotedToken literalString;

		private NumericToken literalNumber;

		private VariableToken variable;

		private String variableArrayIndexString;

		public Argument(LiteralQuotedToken literalString) {
			setLiteralString(literalString);
		}

		public Argument(NumericToken literalNumber) {
			setLiteralNumber(literalNumber);
		}

		public Argument(VariableToken variable) {
			setVariable(variable);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			if (hasVariable()) {
				sb.append(getVariable().getSourceFragment());
				if (isVariableIndexed()) {
					sb.append(getVariableArrayIndexString());
				}
			} else if (hasLiteralString()) {
				sb.append(getLiteralString().getSourceFragment());
			} else if (hasLiteralNumber()) {
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

		public void setVariableArrayIndexString(String indexString) {
			this.variableArrayIndexString = indexString;
		}

	}

}