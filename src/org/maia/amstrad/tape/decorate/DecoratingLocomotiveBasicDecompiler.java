package org.maia.amstrad.tape.decorate;

import org.maia.amstrad.basic.BasicByteCode;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicDecompiler;
import org.maia.amstrad.tape.model.ByteCodeRange;
import org.maia.amstrad.tape.model.SourceCodePosition;
import org.maia.amstrad.tape.model.SourceCodeRange;

public class DecoratingLocomotiveBasicDecompiler extends LocomotiveBasicDecompiler {

	private SourcecodeBytecodeDecorator sourceCodeDecorator;

	public DecoratingLocomotiveBasicDecompiler() {
	}

	@Override
	protected void init(BasicByteCode byteCode) {
		super.init(byteCode);
		this.sourceCodeDecorator = new SourcecodeBytecodeDecorator();
	}

	@Override
	protected void addedSourceCodeToken(int lineNumber, CharSequence lineSoFar, int linePositionFrom,
			int linePositionUntil, int bytecodeOffset, int bytecodeLength) {
		super.addedSourceCodeToken(lineNumber, lineSoFar, linePositionFrom, linePositionUntil, bytecodeOffset,
				bytecodeLength);
		SourceCodePosition from = new SourceCodePosition(lineNumber, linePositionFrom);
		SourceCodePosition until = new SourceCodePosition(lineNumber, linePositionUntil);
		getSourceCodeDecorator().decorate(new SourceCodeRange(from, until),
				new ByteCodeRange(bytecodeOffset, bytecodeLength));
	}

	public SourcecodeBytecodeDecorator getSourceCodeDecorator() {
		return sourceCodeDecorator;
	}

}