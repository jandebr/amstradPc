package org.maia.amstrad.pc.basic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.maia.amstrad.pc.AmstradContext;
import org.maia.amstrad.pc.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.pc.basic.locomotive.LocomotiveBasicCompiler;

public class BasicCompilerTest {

	public BasicCompilerTest() {
	}

	public static void main(String[] args) throws IOException {
		File basFile = new File("test.bas");
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		amstradPc.start(true);
		BasicRuntime basicRuntime = amstradPc.getBasicRuntime();
		basicRuntime.keyboardTypeFileContents(basFile);
		byte[] referenceByteCode = basicRuntime.exportByteCode();
		byte[] compiledByteCode = new LocomotiveBasicCompiler().compile(AmstradContext.readTextFileContents(basFile));
		// printByteCode(referenceByteCode);
		printByteCodeComparison(referenceByteCode, compiledByteCode);
	}

	private static void printByteCode(byte[] byteCode) {
		BasicByteCodeFormatter fmt = new BasicByteCodeFormatter();
		System.out.println(fmt.format(byteCode));
	}

	private static void printByteCodeComparison(byte[] firstByteCode, byte[] secondByteCode) {
		int n1 = firstByteCode.length;
		int n2 = secondByteCode.length;
		boolean[] underline1 = new boolean[n1];
		boolean[] underline2 = new boolean[n2];
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
					underline2[i2 + i] = true;
				} else if (i >= r2) {
					underline1[i1 + i] = true;
				} else if (firstByteCode[i1 + i] != secondByteCode[i2 + i]) {
					underline1[i1 + i] = true;
					underline2[i2 + i] = true;
				}
			}
		}
		if (lo1.length > lo2.length) {
			Arrays.fill(underline1, lo1[lo2.length], n1, true);
		} else if (lo2.length > lo1.length) {
			Arrays.fill(underline2, lo2[lo1.length], n2, true);
		}
		BasicByteCodeFormatter fmt = new BasicByteCodeFormatter();
		System.out.println(fmt.format(firstByteCode, underline1));
		System.out.println("---");
		System.out.println();
		System.out.println(fmt.format(secondByteCode, underline2));
	}

	private static int[] findLineOffsets(byte[] byteCode) {
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

}