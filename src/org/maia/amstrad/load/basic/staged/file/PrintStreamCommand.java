package org.maia.amstrad.load.basic.staged.file;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace.VariableNotFoundException;
import org.maia.amstrad.basic.locomotive.token.FloatingPointNumberToken;
import org.maia.amstrad.basic.locomotive.token.FloatingPointTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.IntegerTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.LiteralQuotedToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.UntypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.VariableToken;
import org.maia.amstrad.load.basic.staged.StagedBasicProgramLoaderSession;

public abstract class PrintStreamCommand extends FileCommand {

	public static PrintStreamCommand parseFrom(BasicSourceTokenSequence sequence) throws BasicException {
		PrintStreamCommand command = null;
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		int i = sequence.getFirstIndexOf(stf.createLiteral("#"));
		if (i >= 0 && i < sequence.size() - 1) {
			if (sequence.get(i + 1).equals(stf.createPositiveIntegerSingleDigitDecimal(9))) {
				i += 2;
				int j = sequence.getNextIndexOf(VariableToken.class, i);
				if (j >= 0) {
					command = new VariablePrintStreamCommand((VariableToken) sequence.get(j));
				} else {
					j = sequence.getNextIndexOf(LiteralQuotedToken.class, i);
					if (j >= 0) {
						command = new LiteralPrintStreamCommand((LiteralQuotedToken) sequence.get(j));
					}
				}
			}
		}
		return command;
	}

	@Override
	public String toString() {
		return "PrintStreamCommand";
	}

	public abstract String getValueToPrint(StagedBasicProgramLoaderSession session);

	private static class VariablePrintStreamCommand extends PrintStreamCommand {

		private VariableToken variable;

		public VariablePrintStreamCommand(VariableToken variable) {
			this.variable = variable;
		}

		@Override
		public String toString() {
			return super.toString() + " writing " + getVariable().getSourceFragment();
		}

		@Override
		public String getValueToPrint(StagedBasicProgramLoaderSession session) {
			String value = "";
			if (session.getBasicRuntime() instanceof LocomotiveBasicRuntime) {
				LocomotiveBasicVariableSpace vars = ((LocomotiveBasicRuntime) session.getBasicRuntime())
						.getVariableSpace();
				VariableToken var = getVariable();
				try {
					if (var instanceof IntegerTypedVariableToken) {
						value = String.valueOf(vars.getValue((IntegerTypedVariableToken) var));
					} else if (var instanceof FloatingPointTypedVariableToken) {
						value = FloatingPointNumberToken.format(vars.getValue((FloatingPointTypedVariableToken) var));
					} else if (var instanceof UntypedVariableToken) {
						value = FloatingPointNumberToken.format(vars.getValue((UntypedVariableToken) var));
					} else if (var instanceof StringTypedVariableToken) {
						value = vars.getValue((StringTypedVariableToken) var);
					}
				} catch (VariableNotFoundException e) {
					System.err.println(e);
				}
			}
			return value;
		}

		public VariableToken getVariable() {
			return variable;
		}

	}

	private static class LiteralPrintStreamCommand extends PrintStreamCommand {

		private LiteralQuotedToken literal;

		public LiteralPrintStreamCommand(LiteralQuotedToken literal) {
			this.literal = literal;
		}

		@Override
		public String toString() {
			return super.toString() + " writing " + getLiteral().getSourceFragment();
		}

		@Override
		public String getValueToPrint(StagedBasicProgramLoaderSession session) {
			return getLiteral().getLiteralBetweenQuotes();
		}

		public LiteralQuotedToken getLiteral() {
			return literal;
		}

	}

}