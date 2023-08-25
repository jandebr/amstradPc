package org.maia.amstrad.pc.impl.jemu;

import java.io.File;
import java.io.IOException;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceCode;
import org.maia.amstrad.pc.tape.AmstradTape;
import org.maia.amstrad.util.AmstradIO;

public class JemuTape extends AmstradTape {

	public JemuTape(JemuAmstradPc amstradPc) {
		super(amstradPc);
	}

	@Override
	public LocomotiveBasicSourceCode readSourceCodeFromFile(File sourceCodeFile) throws IOException, BasicException {
		return new LocomotiveBasicSourceCode(AmstradIO.readTextFileContents(sourceCodeFile));
	}

	@Override
	public LocomotiveBasicByteCode readByteCodeFromFile(File byteCodeFile) throws IOException, BasicException {
		return new LocomotiveBasicByteCode(AmstradIO.readBinaryFileContents(byteCodeFile));
	}

}