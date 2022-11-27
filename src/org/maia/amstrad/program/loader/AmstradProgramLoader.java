package org.maia.amstrad.program.loader;

import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public interface AmstradProgramLoader {

	AmstradProgramRuntime load(AmstradProgram program) throws AmstradProgramException;

}