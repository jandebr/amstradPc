package org.maia.amstrad.program.loader;

import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.source.BasicSourceCode;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramRuntime;

public abstract class BasicProgramManipulator {

	protected BasicProgramManipulator() {
	}

	public abstract ManipulationSession createSession();

	public abstract class ManipulationSession {

		protected ManipulationSession() {
		}

		public abstract void manipulateSourceCode(AmstradProgram program, BasicSourceCode sourceCode)
				throws BasicSyntaxException;

		public abstract void sourceCodeLoaded(AmstradProgramRuntime programRuntime);

	}

}