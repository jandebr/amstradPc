package org.maia.amstrad.pc.impl.jemu;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.pc.tape.AmstradTape;
import org.maia.util.io.IOUtils;

public class JemuTape extends AmstradTape {

	public JemuTape(JemuAmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public LocomotiveBasicSourceCode readSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException {
		return new LocomotiveBasicSourceCode(IOUtils.readTextFileContents(sourceCodeFile));
	}

	@Override
	public LocomotiveBasicByteCode readByteCodeFromFile(File byteCodeFile) throws IOException, BasicException {
		return new LocomotiveBasicByteCode(IOUtils.readBinaryFileContents(byteCodeFile));
	}

}