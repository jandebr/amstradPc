package org.maia.amstrad.basic.locomotive.source;

import java.text.NumberFormat;
import java.util.Locale;

public class FloatingPointNumberToken extends NumericToken {

	private static NumberFormat formatter;

	static {
		formatter = NumberFormat.getNumberInstance(Locale.US);
		formatter.setMaximumFractionDigits(8);
		formatter.setGroupingUsed(false);
	}

	public static String format(double value) {
		return formatter.format(value);
	}

	public FloatingPointNumberToken(String sourceFragment) {
		super(sourceFragment);
	}

	@Override
	public void invite(SourceTokenVisitor visitor) {
		visitor.visitFloatingPointNumber(this);
	}

	public double getValue() {
		return parseAsDouble();
	}

}