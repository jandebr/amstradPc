package org.maia.amstrad.basic;

import java.util.Arrays;

public class BasicSymbol {

	private int number;

	private int[] values;

	public BasicSymbol(int number, int... values) {
		this.number = number;
		this.values = toEightValues(values);
	}

	private static int[] toEightValues(int... values) {
		if (values.length == 8)
			return values;
		int[] v8 = new int[8];
		System.arraycopy(values, 0, v8, 0, Math.min(values.length, 8));
		return v8;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getNumber();
		result = prime * result + Arrays.hashCode(getValues());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicSymbol other = (BasicSymbol) obj;
		if (getNumber() != other.getNumber())
			return false;
		if (!Arrays.equals(getValues(), other.getValues()))
			return false;
		return true;
	}

	public int getNumber() {
		return number;
	}

	public int[] getValues() {
		return values;
	}

	public int getValue(int index) {
		if (index >= 0 && index < getValues().length) {
			return getValues()[index];
		} else {
			return 0;
		}
	}

}