package org.maia.amstrad.gui.browser.components;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.ProgramBrowserDisplaySource;
import org.maia.amstrad.load.AmstradProgramLoader;
import org.maia.amstrad.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.load.AmstradProgramRuntime;
import org.maia.amstrad.load.basic.staged.EndingBasicAction;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.AmstradProgramType;

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
							if (!getBrowser().isKioskMode()) {
								getBrowser().addReturnToProgramMenu();
							}
							AmstradFactory.getInstance().getAmstradContext().showProgramBrowser(getAmstradPc());
						}
					});
		} else {
			return super.getProgramLoader(program);
		}
	}

}