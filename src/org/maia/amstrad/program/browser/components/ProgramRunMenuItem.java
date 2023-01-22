package org.maia.amstrad.program.browser.components;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramRuntime;
import org.maia.amstrad.program.AmstradProgramType;
import org.maia.amstrad.program.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.program.loader.AmstradProgramLoader;
import org.maia.amstrad.program.loader.AmstradProgramLoaderFactory;
import org.maia.amstrad.program.loader.basic.staged.EndingBasicAction;

public class ProgramRunMenuItem extends ProgramLaunchMenuItem {

	public ProgramRunMenuItem(ProgramBrowserDisplaySource browser, AmstradProgram program) {
		super(browser, program, "Run");
	}

	@Override
	protected void launchProgram(AmstradProgram program) throws AmstradProgramException {
		getProgramLoader(program).load(program).run();
		getBrowser().notifyProgramRun(program);
	}

	@Override
	protected AmstradProgramLoader getProgramLoader(AmstradProgram program) {
		if (AmstradProgramType.BASIC_PROGRAM.equals(program.getProgramType())) {
			return AmstradProgramLoaderFactory.getInstance().createStagedBasicProgramLoader(getAmstradPc(),
					new EndingBasicAction() {

						@Override
						public void perform(AmstradProgramRuntime programRuntime) {
							getBrowser().addReturnToProgramMenu();
							AmstradFactory.getInstance().getAmstradContext().showProgramBrowser(getAmstradPc());
						}
					});
		} else {
			return super.getProgramLoader(program);
		}
	}

}