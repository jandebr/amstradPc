package org.maia.amstrad.pc.basic.locomotive;

import org.maia.amstrad.pc.basic.BasicCompiler;

public class LocomotiveBasicCompiler extends LocomotiveBasicProcessor implements BasicCompiler {

	public LocomotiveBasicCompiler() {
	}

	@Override
	public byte[] compile(CharSequence sourceCode) {
		System.out.println("About to compile");
		System.out.println(getTokenMap().getTokensStartingWith('P'));
		return null;
	}

}