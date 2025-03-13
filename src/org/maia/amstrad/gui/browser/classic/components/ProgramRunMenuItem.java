package org.maia.amstrad.gui.browser.classic.components;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.load.AmstradProgramLoader;
import org.maia.amstrad.program.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.program.load.AmstradProgramRuntime;
import org.maia.amstrad.program.load.basic.staged.EndingBasicAction;

public class ProgramRunMenuItem extends ProgramLaunchMenuItem {

	public ProgramRunMenuItem(ProgramMenu menu) {
		super(menu, "Run");
	}

	@Override
	protected void launchProgram() throws AmstradProgramException {
		AmstradProgram program = getProgram();
		getProgramLoader().load(program).run();
		getBrowserDisplaySource().getProgramBrowser().fireProgramRun(program);
	}

	@Override
	protected AmstradProgramLoader getProgramLoader() {
		if (isStagedRun()) {
			return AmstradProgramLoaderFactory.getInstance().createStagedBasicProgramLoader(getAmstradPc(),
					new EndingBasicAction() {

						@Override
						public void perform(AmstradProgramRuntime programRuntime) {
							setFailed(programRuntime.getExitCode() != 0);
							if (getBrowserDisplaySource().getSystemSettings().isProgramSourceCodeAccessible()) {
								getMenu().addReturnMenuItem();
							}
							AmstradFactory.getInstance().getAmstradContext().showProgramBrowser(getAmstradPc());
						}
					});
		} else {
			return super.getProgramLoader();
		}
	}

	private boolean isStagedRun() {
		return getMenu().getBrowserDisplaySource().getProgramBrowser().isStagedRun(getProgram());
	}

}