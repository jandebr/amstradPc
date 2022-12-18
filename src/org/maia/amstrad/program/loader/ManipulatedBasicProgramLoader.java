package org.maia.amstrad.program.loader;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicCompiler;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicDecompiler;
import org.maia.amstrad.basic.locomotive.source.BasicSourceCode;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;

public class ManipulatedBasicProgramLoader extends AbstractBasicProgramLoader {

	private List<BasicProgramManipulator> manipulators;

	public ManipulatedBasicProgramLoader(AmstradPc amstradPc) {
		super(amstradPc);
		this.manipulators = new Vector<BasicProgramManipulator>();
	}

	public void addManipulator(BasicProgramManipulator manipulator) {
		getManipulators().add(manipulator);
	}

	@Override
	protected AmstradProgramRuntime doLoad(AmstradProgram program) throws AmstradProgramException {
		AmstradProgramRuntime programRuntime = super.doLoad(program);
		sourceCodeLoaded(programRuntime);
		return programRuntime;
	}

	@Override
	protected CharSequence getSourceCodeToLoad(AmstradProgram program) throws AmstradProgramException {
		try {
			BasicSourceCode sourceCode = new BasicSourceCode(getOriginalSourceCode(program));
			manipulateSourceCode(program, sourceCode);
			return sourceCode.getText();
		} catch (BasicSyntaxException e) {
			throw new AmstradProgramException(program, "Failed source code manipulation of " + program.getProgramName(),
					e);
		}
	}

	@Override
	protected byte[] getByteCodeToLoad(AmstradProgram program) throws AmstradProgramException {
		try {
			CharSequence originalSourceCode = new LocomotiveBasicDecompiler().decompile(getOriginalByteCode(program));
			BasicSourceCode sourceCode = new BasicSourceCode(originalSourceCode);
			manipulateSourceCode(program, sourceCode);
			return new LocomotiveBasicCompiler().compile(sourceCode.getText());
		} catch (Exception e) {
			throw new AmstradProgramException(program, "Failed byte code manipulation of " + program.getProgramName(),
					e);
		}
	}

	protected void manipulateSourceCode(AmstradProgram program, BasicSourceCode sourceCode)
			throws BasicSyntaxException {
		for (BasicProgramManipulator manipulator : getManipulators()) {
			manipulator.manipulateSourceCode(program, sourceCode);
		}
	}

	protected void sourceCodeLoaded(AmstradProgramRuntime programRuntime) {
		for (BasicProgramManipulator manipulator : getManipulators()) {
			manipulator.sourceCodeLoaded(programRuntime);
		}
	}

	protected List<BasicProgramManipulator> getManipulators() {
		return manipulators;
	}

}