package org.maia.amstrad.basic.locomotive;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.BasicLanguage;

public class LocomotiveBasicByteCode extends BasicByteCode {

	public LocomotiveBasicByteCode(byte[] bytes) {
		super(BasicLanguage.LOCOMOTIVE_BASIC, bytes);
	}

}