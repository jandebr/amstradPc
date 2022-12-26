package org.maia.amstrad.basic.locomotive.source;

public class OperatorToken extends SourceToken {

	private static String[] operators = { ">", "=", ">=", "<", "<>", "<=", "+", "-", "*", "/", "^", "\\", "AND", "MOD",
			"OR", "XOR", "NOT" };

	public static boolean isOperator(String sourceFragment) {
		String usymbol = sourceFragment.toUpperCase();
		for (int i = 0; i < operators.length; i++) {
			if (operators[i].equals(usymbol))
				return true;
		}
		return false;
	}

	public OperatorToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitOperator(this);
	}

}