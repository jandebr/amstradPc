package org.maia.amstrad.basic;

import java.io.File;
import java.util.Set;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicRuntime;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicVariableSpace;
import org.maia.amstrad.basic.locomotive.token.FloatingPointTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.IntegerTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.StringTypedVariableToken;
import org.maia.amstrad.basic.locomotive.token.TypedVariableToken;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcFrame;
import org.maia.amstrad.program.AmstradBasicProgramFile;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;

public class BasicVariablesTest {

	public static void main(String[] args) throws AmstradProgramException, BasicException {
		BasicVariablesTest test = new BasicVariablesTest();
		AmstradProgram program = test.getTestProgram();
		test.run(program);
	}

	private BasicVariablesTest() {
	}

	private AmstradProgram getTestProgram() {
		File dir = new File("resources/test/variables");
		AmstradProgram program = new AmstradBasicProgramFile(new File(dir, "test.bas"));
		return program;
	}

	public void run(AmstradProgram program) throws AmstradProgramException, BasicException {
		AmstradPc amstradPc = AmstradFactory.getInstance().createAmstradPc();
		AmstradPcFrame frame = amstradPc.displayInFrame(true);
		frame.installMenuBar();
		amstradPc.launch(program);
		LocomotiveBasicRuntime runtime = (LocomotiveBasicRuntime) amstradPc.getBasicRuntime();
		runtime.waitUntilReady();
		LocomotiveBasicVariableSpace variableSpace = runtime.getVariableSpace();
		System.out.println(variableSpace);
		variableSpace.setValue(new IntegerTypedVariableToken("a%"), 245);
		variableSpace.setValue(new FloatingPointTypedVariableToken("fp!"), -0.86);
		variableSpace.setCharAt(new StringTypedVariableToken("text$"), 1, 'o');
		System.out.println(variableSpace);
		Set<TypedVariableToken> vars = variableSpace.getAllVariables();
		for (int i = 0; i < 10; i++) {
			IntegerTypedVariableToken newVar = LocomotiveBasicVariableSpace.generateNewIntegerVariable(vars);
			System.out.println(newVar);
			vars.add(newVar);
		}
		for (int i = 0; i < 10; i++) {
			StringTypedVariableToken newVar = LocomotiveBasicVariableSpace.generateNewStringVariable(vars);
			System.out.println(newVar);
			vars.add(newVar);
		}
	}

}