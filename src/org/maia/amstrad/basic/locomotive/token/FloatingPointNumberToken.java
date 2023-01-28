package org.maia.amstrad.basic.locomotive.token;

import java.text.NumberFormat;
import java.util.Locale;

import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenVisitor;

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

	public FloatingPointNumberToken(double value) {
		this(format(value));
	}

	@Override
	public void invite(LocomotiveBasicSourceTokenVisitor visitor) {
		visitor.visitFloatingPointNumber(this);
	}

	public double getValue() {
		return parseAsDouble();
	}

}