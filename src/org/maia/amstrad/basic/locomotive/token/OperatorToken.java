package org.maia.amstrad.basic.locomotive.token;

import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceToken;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

public class OperatorToken extends LocomotiveBasicSourceToken {

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
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitOperator(this);
	}

	@Override
	public int hashCode() {
		return getSourceFragment().toUpperCase().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OperatorToken other = (OperatorToken) obj;
		return getSourceFragment().toUpperCase().equals(other.getSourceFragment().toUpperCase());
	}

}