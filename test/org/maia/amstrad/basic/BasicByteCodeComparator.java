package org.maia.amstrad.basic;

import java.util.Arrays;

public class BasicByteCodeComparator {

	public BasicByteCodeComparator() {
	}

	public ComparisonResult compare(byte[] firstByteCode, byte[] secondByteCode) {
		ComparisonResult result = new ComparisonResult(firstByteCode, secondByteCode);
		int n1 = firstByteCode.length;
		int n2 = secondByteCode.length;
		boolean[] differences1 = new boolean[n1];
		boolean[] differences2 = new boolean[n2];
		int[] lo1 = findLineOffsets(firstByteCode);
		int[] lo2 = findLineOffsets(secondByteCode);
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
				} else if (firstByteCode[i1 + i] != secondByteCode[i2 + i]) {
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

	private int[] findLineOffsets(byte[] byteCode) {
		if (byteCode.length < 2)
			return new int[0];
		int[] lineOffsets = new int[byteCode.length];
		int lineIndex = 0;
		int bi = 0;
		int n = (byteCode[0] & 0xff) | ((byteCode[1] << 8) & 0xff00);
		while (n > 0) {
			bi += n;
			if (bi + 1 < byteCode.length) {
				lineOffsets[++lineIndex] = bi;
				n = (byteCode[bi] & 0xff) | ((byteCode[bi + 1] << 8) & 0xff00);
			} else {
				n = 0;
			}
		}
		int[] result = new int[lineIndex + 1];
		System.arraycopy(lineOffsets, 0, result, 0, result.length);
		return result;
	}

	public static class ComparisonResult {

		private byte[] firstByteCode;

		private byte[] secondByteCode;

		private boolean[] firstDifferences;

		private boolean[] secondDifferences;

		public ComparisonResult(byte[] firstByteCode, byte[] secondByteCode) {
			this.firstByteCode = firstByteCode;
			this.secondByteCode = secondByteCode;
		}

		public boolean isIdentical() {
			int n1 = getFirstByteCode().length;
			int n2 = getSecondByteCode().length;
			if (n1 != n2)
				return false;
			for (int i = 0; i < n1; i++) {
				if (getFirstByteCode()[i] != getSecondByteCode()[i])
					return false;
			}
			return true;
		}

		public boolean isDifferent() {
			return !isIdentical();
		}

		public byte[] getFirstByteCode() {
			return firstByteCode;
		}

		public boolean[] getFirstDifferences() {
			return firstDifferences;
		}

		private void setFirstDifferences(boolean[] firstDifferences) {
			this.firstDifferences = firstDifferences;
		}

		public byte[] getSecondByteCode() {
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