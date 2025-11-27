package org.maia.amstrad.gui.browser.carousel.action;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.gui.browser.carousel.animation.CarouselAnimation;
import org.maia.amstrad.gui.browser.carousel.api.CarouselRunProgramHost;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.AmstradMonitorMode;
import org.maia.amstrad.program.AmstradProgram;
import org.maia.amstrad.program.AmstradProgramException;
import org.maia.amstrad.program.load.AmstradProgramLoader;
import org.maia.amstrad.program.load.AmstradProgramLoaderFactory;
import org.maia.amstrad.program.load.AmstradProgramRuntime;
import org.maia.amstrad.program.load.basic.staged.EndingBasicAction;
import org.maia.amstrad.program.repo.AmstradProgramRepository.ProgramNode;

public class CarouselRunProgramAction extends CarouselAction {

	private ProgramNode programNode;

	public CarouselRunProgramAction(CarouselRunProgramHost host, ProgramNode programNode, CarouselAnimation animation) {
		super(host, animation);
		this.programNode = programNode;
	}

	@Override
	protected void doPerform() {
		CarouselRunProgramHost host = getHost();
		AmstradPc amstradPc = host.getAmstradPc();
		new Thread(new Runnable() {
			@Override
			public void run() {
				AmstradProgram program = getProgramNode().getProgram();
				AmstradMonitorMode mode = program.getPreferredMonitorMode();
				try {
					host.releaseKeyboard();
					amstradPc.reboot(true, true);
					getProgramLoader(program).load(program).run();
					host.getProgramBrowser().fireProgramRun(program);
					sleepCurrentThreadUntilMinimumAnimationDuration();
					host.close();
					if (mode != null) {
						amstradPc.getMonitor().setMode(mode);
					}
				} catch (AmstradProgramException exc) {
					exc.printStackTrace();
					host.acquireKeyboard();
					host.notifyProgramRunFailState(getProgramNode(), true);
				}
			}
		}).start();
	}

	private AmstradProgramLoader getProgramLoader(AmstradProgram program) {
		CarouselRunProgramHost host = getHost();
		AmstradPc amstradPc = host.getAmstradPc();
		if (host.getProgramBrowser().isStagedRun(program)) {
			return AmstradProgramLoaderFactory.getInstance().createStagedBasicProgramLoader(amstradPc,
					new EndingBasicAction() {

						@Override
						public void perform(AmstradProgramRuntime programRuntime) {
							AmstradFactory.getInstance().getAmstradContext().showProgramBrowser(amstradPc);
							host.notifyProgramRunFailState(getProgramNode(), programRuntime.getExitCode() != 0);
						}
					});
		} else {
			return AmstradProgramLoaderFactory.getInstance().createLoaderFor(program, amstradPc);
		}
	}

	@Override
	protected CarouselRunProgramHost getHost() {
		return (CarouselRunProgramHost) super.getHost();
	}

	public ProgramNode getProgramNode() {
		return programNode;
	}

}