package org.maia.amstrad.basic.locomotive;

import java.util.Arrays;

public class LocomotiveBasicByteCodeComparator {

	public LocomotiveBasicByteCodeComparator() {
	}

	public ComparisonResult compare(LocomotiveBasicByteCode firstByteCode, LocomotiveBasicByteCode secondByteCode) {
		ComparisonResult result = new ComparisonResult(firstByteCode, secondByteCode);
		int n1 = firstByteCode.getByteCount();
		int n2 = secondByteCode.getByteCount();
		boolean[] differences1 = new boolean[n1];
		boolean[] differences2 = new boolean[n2];
		int[] lo1 = firstByteCode.getLineOffsetIndices();
		int[] lo2 = secondByteCode.getLineOffsetIndices();
		for (int li = 0; li < Math.min(lo1.length, lo2.length); li++) {
			int i1 = lo1[li];
			int i2 = lo2[li];
			int j1 = li < lo1.length - 1 ? lo1[li + 1] : n1;
			int j2 = li < lo2.length - 1 ? lo2[li + 1] : n2;
			int r1 = j1 - i1;
			int r2 = j2 - i2;
			for (int i = 0; i < Math.max(r1, r2); i++) {
				if (i >= r1) {
					differences2[i2 + i] = true;
				} else if (i >= r2) {
					differences1[i1 + i] = true;
				} else if (firstByteCode.getByte(i1 + i) != secondByteCode.getByte(i2 + i)) {
					differences1[i1 + i] = true;
					differences2[i2 + i] = true;
				}
			}
		}
		if (lo1.length > lo2.length) {
			Arrays.fill(differences1, lo1[lo2.length], n1, true);
		} else if (lo2.length > lo1.length) {
			Arrays.fill(differences2, lo2[lo1.length], n2, true);
		}
		result.setFirstDifferences(differences1);
		result.setSecondDifferences(differences2);
		return result;
	}

	public static class ComparisonResult {

		private LocomotiveBasicByteCode firstByteCode;

		private LocomotiveBasicByteCode secondByteCode;

		private boolean[] firstDifferences;

		private boolean[] secondDifferences;

		public ComparisonResult(LocomotiveBasicByteCode firstByteCode, LocomotiveBasicByteCode secondByteCode) {
			this.firstByteCode = firstByteCode;
			this.secondByteCode = secondByteCode;
		}

		public boolean isIdentical() {
			int n1 = getFirstByteCode().getByteCount();
			int n2 = getSecondByteCode().getByteCount();
			if (n1 != n2)
				return false;
			for (int i = 0; i < n1; i++) {
				if (getFirstByteCode().getByte(i) != getSecondByteCode().getByte(i))
					return false;
			}
			return true;
		}

		public boolean isDifferent() {
			return !isIdentical();
		}

		public LocomotiveBasicByteCode getFirstByteCode() {
			return firstByteCode;
		}

		public boolean[] getFirstDifferences() {
			return firstDifferences;
		}

		private void setFirstDifferences(boolean[] firstDifferences) {
			this.firstDifferences = firstDifferences;
		}

		public LocomotiveBasicByteCode getSecondByteCode() {
			return secondByteCode;
		}

		public boolean[] getSecondDifferences() {
			return secondDifferences;
		}

		private void setSecondDifferences(boolean[] secondDifferences) {
			this.secondDifferences = secondDifferences;
		}

	}

}