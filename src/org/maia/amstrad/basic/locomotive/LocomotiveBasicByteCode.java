package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicLanguage;

public class LocomotiveBasicByteCode extends BasicByteCode {

	public LocomotiveBasicByteCode() {
		this(new byte[2]); // 0x0000
	}

	public LocomotiveBasicByteCode(byte[] bytes) {
		super(BasicLanguage.LOCOMOTIVE_BASIC, bytes);
	}

	public LocomotiveBasicByteCode fit() {
		if (getByteCount() < 2) {
			return new LocomotiveBasicByteCode();
		} else {
			int i = 0;
			int n = getWord(0);
			while (n > 0) {
				i += n;
				if (i + 1 < getByteCount()) {
					n = getWord(i);
				} else {
					n = 0;
				}
			}
			int len = i + 2;
			if (getByteCount() == len) {
				return this; // already fitted
			} else {
				byte[] fitted = new byte[len];
				System.arraycopy(getBytes(), 0, fitted, 0, Math.min(len, getByteCount()));
				return new LocomotiveBasicByteCode(fitted);
			}
		}
	}

	public int[] getLineOffsetIndices() {
		if (getByteCount() < 2) {
			return new int[0];
		} else {
			int[] lineOffsets = new int[getByteCount()];
			int lineIndex = 0;
			int i = 0;
			int n = getWord(0);
			while (n > 0) {
				i += n;
				if (i + 1 < getByteCount()) {
					lineOffsets[++lineIndex] = i;
					n = getWord(i);
				} else {
					n = 0;
				}
			}
			int[] result = new int[lineIndex + 1];
			System.arraycopy(lineOffsets, 0, result, 0, result.length);
			return result;
		}
	}

	@Override
	public String toString() {
		return new LocomotiveBasicByteCodeFormatter().format(this).toString();
	}

}