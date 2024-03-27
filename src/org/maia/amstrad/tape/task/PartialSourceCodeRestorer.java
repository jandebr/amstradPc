package org.maia.amstrad.tape.task;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicDecompiler;
import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicDecompiler;
import org.maia.io.util.IOUtils;

public class PartialSourceCodeRestorer {

	public PartialSourceCodeRestorer() {
	}

	public void restorePartially(File byteCodeFile) throws IOException {
		BasicDecompiler decompiler = new LocomotiveBasicDecompiler();
		byte[] bytesOnTape = IOUtils.readBinaryFileContents(byteCodeFile);
		boolean success = false;
		int offset = 0;
		int n = bytesOnTape.length;
		while (!success && offset < Math.min(256, n)) {
			try {
				byte[] bytes = new byte[n - offset];
				System.arraycopy(bytesOnTape, offset, bytes, 0, bytes.length);
				BasicByteCode byteCode = new LocomotiveBasicByteCode(bytes);
				BasicSourceCode sourceCode = decompiler.decompile(byteCode);
				if (!sourceCode.isEmpty()) {
					System.out.println(sourceCode.getText());
					success = true;
				}
			} catch (BasicException e) {
				System.out.println(e);
			}
			if (!success)
				offset++;
		}
	}

}