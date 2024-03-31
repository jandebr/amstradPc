package org.maia.amstrad.program.load.basic.staged;

import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.LiteralRemarkToken;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.program.load.basic.staged.WaitResumeBasicPreprocessor.WaitResumeMacro;

public class EmulatorCommandBasicPreprocessor extends StagedBasicPreprocessor {

	public static final String COMMAND_PREFIX = "_";

	public static final String REBOOT_COMMAND = COMMAND_PREFIX + "REBOOT";

	public static final String TURBO_ON_COMMAND = COMMAND_PREFIX + "TURBO ON";

	public static final String TURBO_OFF_COMMAND = COMMAND_PREFIX + "TURBO OFF";

	public EmulatorCommandBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0; // reusing waitresume macro
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		return Collections.emptyList();
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		EmulatorCommandRuntimeListener listener = null;
		if (!session.hasMacrosAdded(EmulatorCommandMacro.class)) {
			int addrTrap = session.reserveMemory(1);
			listener = new EmulatorCommandRuntimeListener(session, addrTrap);
			session.addMacro(new EmulatorCommandMacro(listener));
			listener.install();
		} else {
			listener = session.getMacroAdded(EmulatorCommandMacro.class).getListener();
		}
		invokeOnEmulatorCommand(sourceCode, listener, session);
	}

	private void invokeOnEmulatorCommand(BasicSourceCode sourceCode, EmulatorCommandRuntimeListener listener,
			StagedBasicProgramLoaderSession session) throws BasicException {
		int addrTrap = listener.getMemoryTrapAddress();
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		BasicSourceToken REM = stf.createBasicKeyword("REM");
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(REM);
				if (i >= 0) {
					int j = sequence.getNextIndexOf(LiteralRemarkToken.class, i + 1);
					if (j >= 0) {
						EmulatorCommand command = parseFrom((LiteralRemarkToken) sequence.get(j));
						if (command != null) {
							int ref = listener.registerCommand(command).getReferenceNumber();
							sequence.replaceRange(i, sequence.size(),
									createWaitResumeMacroInvocationSequence(session, addrTrap, ref));
						}
					}
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	protected EmulatorCommand parseFrom(LiteralRemarkToken remark) throws BasicException {
		EmulatorCommand command = null;
		String str = remark.getSourceFragment().trim();
		if (str.startsWith(REBOOT_COMMAND)) {
			command = new RebootCommand();
		} else if (str.startsWith(TURBO_ON_COMMAND)) {
			command = new TurboOnCommand();
		} else if (str.startsWith(TURBO_OFF_COMMAND)) {
			command = new TurboOffCommand();
		}
		return command;
	}

	private static class EmulatorCommandMacro extends StagedBasicMacro {

		private EmulatorCommandRuntimeListener listener;

		public EmulatorCommandMacro(EmulatorCommandRuntimeListener listener) {
			this.listener = listener;
		}

		public EmulatorCommandRuntimeListener getListener() {
			return listener;
		}

	}

	private class EmulatorCommandRuntimeListener extends StagedBasicProgramTrappedRuntimeListener {

		public EmulatorCommandRuntimeListener(StagedBasicProgramLoaderSession session, int memoryTrapAddress) {
			super(session, memoryTrapAddress);
		}

		@Override
		protected StagedBasicMacroHandler createMacroHandler(StagedCommandResolver resolver) {
			EmulatorCommandMacro macro = getSession().getMacroAdded(EmulatorCommandMacro.class);
			return new EmulatorCommandMacroHandler(macro, getSession(), resolver);
		}

	}

	private class EmulatorCommandMacroHandler extends StagedBasicMacroHandler {

		public EmulatorCommandMacroHandler(EmulatorCommandMacro macro, StagedBasicProgramLoaderSession session,
				StagedCommandResolver resolver) {
			super(macro, session, resolver);
		}

		@Override
		public void handleMemoryTrap(AmstradMemory memory, int memoryAddress, byte memoryValue) {
			StagedCommand command = getResolver().resolve(memoryValue);
			if (command != null && command instanceof EmulatorCommand) {
				EmulatorCommand emulatorCommand = (EmulatorCommand) command;
				executeEmulatorCommand(emulatorCommand);
			}
		}

		private void executeEmulatorCommand(EmulatorCommand command) {
			System.out.println("Handling emulator command " + command.getName());
			command.execute(getSession());
		}

	}

	protected abstract class EmulatorCommand extends StagedCommand {

		protected EmulatorCommand() {
		}

		public abstract void execute(StagedBasicProgramLoaderSession session);

		protected void resumeRun(StagedBasicProgramLoaderSession session) {
			WaitResumeMacro macro = session.getMacroAdded(WaitResumeMacro.class);
			EmulatorCommandBasicPreprocessor.this.resumeRun(macro, session);
		}

		public abstract String getName();

	}

	protected class RebootCommand extends EmulatorCommand {

		public RebootCommand() {
		}

		@Override
		public void execute(StagedBasicProgramLoaderSession session) {
			session.getAmstradPc().reboot(false, false);
			// no need to resume run
		}

		@Override
		public String getName() {
			return REBOOT_COMMAND;
		}

	}

	protected class TurboOnCommand extends EmulatorCommand {

		public TurboOnCommand() {
		}

		@Override
		public void execute(StagedBasicProgramLoaderSession session) {
			session.getAmstradPc().setTurboMode(true);
			resumeRun(session);
		}

		@Override
		public String getName() {
			return TURBO_ON_COMMAND;
		}

	}

	protected class TurboOffCommand extends EmulatorCommand {

		public TurboOffCommand() {
		}

		@Override
		public void execute(StagedBasicProgramLoaderSession session) {
			session.getAmstradPc().setTurboMode(false);
			resumeRun(session);
		}

		@Override
		public String getName() {
			return TURBO_OFF_COMMAND;
		}

	}

}