package org.maia.amstrad.program.load.basic.staged;

import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.maia.amstrad.basic.BasicException;
import org.maia.amstrad.basic.BasicLanguage;
import org.maia.amstrad.basic.BasicLineNumberScope;
import org.maia.amstrad.basic.BasicSourceCode;
import org.maia.amstrad.basic.BasicSourceCodeLine;
import org.maia.amstrad.basic.BasicSourceToken;
import org.maia.amstrad.basic.BasicSourceTokenSequence;
import org.maia.amstrad.basic.BasicSyntaxException;
import org.maia.amstrad.basic.locomotive.LocomotiveBasicSourceTokenFactory;
import org.maia.amstrad.basic.locomotive.token.BasicKeywordToken;
import org.maia.amstrad.basic.locomotive.token.NumericToken;
import org.maia.amstrad.basic.locomotive.token.SingleDigitDecimalToken;
import org.maia.amstrad.pc.joystick.AmstradJoystick;
import org.maia.amstrad.pc.joystick.AmstradJoystickController;
import org.maia.amstrad.pc.joystick.AmstradJoystickID;
import org.maia.amstrad.pc.joystick.keys.AmstradJoystickKeyEvent;
import org.maia.amstrad.pc.keyboard.KeyEventTarget;
import org.maia.amstrad.pc.memory.AmstradMemory;
import org.maia.amstrad.program.AmstradProgramMetaDataConstants;
import org.maia.amstrad.program.load.AmstradProgramRuntime;
import org.maia.util.SystemUtils;

/**
 * Direct joystick Basic preprocessor
 * 
 * <p>
 * Joystick <em>polling</em> in a Basic program is a common practice, however challenging
 * </p>
 * <p>
 * A high polling frequency may result in repeated program actions, for example a character making two steps with only
 * one joystick movement. The typical solution is to introduce a small delay in code at the start or end of a polling
 * cycle, for example using an empty <code>FOR</code> loop
 * 
 * <pre>
 * FOR a% = 1 TO 50: NEXT a%
 * </pre>
 * <p>
 * The downside of the delay is that quick joystick movements may sporadically go by unnoticed and cause no action.
 * Finding the best delay is a tradeoff and challenged by different sources of variability
 * <ul>
 * <li>Variability in polling frequency. For example, action instructions consume additional time, slowing down the
 * polling frequency</li>
 * <li>Variability caused by asynchronous keyboard scanning. Joystick movements are mapped to keys and scanned 50 times
 * per second by the CPC. The scanning frequency does not align with the polling frequency, which also introduces a
 * variable delay</li>
 * <li>Variability in emulated CPU clock speed, also considering the emulator is multi-threaded</li>
 * </ul>
 * These challenges represent real problems in a gaming context where speed and accuracy can decide the outcome of a
 * game
 * <p>
 * This preprocessor tries to overcome these challenges to some extent. In short,
 * <ul>
 * <li>Keyboard scanning (<code>JOY</code>) is replaced by a memory-mapped, near-instant joystick integration
 * (<code>PEEK</code>)</li>
 * <li>Quick joystick movements are <em>sticky</em> until they are checked in the program (or until a certain idle time
 * has passed)</li>
 * </ul>
 * <p>
 * This preprocessor will only act on those joysticks that are active at the time of staging as per
 * {@linkplain AmstradJoystick#isActive()}, so just before the program is run. Beware that direct joystick operation
 * during the program run is not compatible with alternative operation from the keyboard. Joysticks that are inactive
 * and become active while running the program, will be fully operational in the traditional way however do not benefit
 * from this preprocessor
 * </p>
 * <p>
 * This preprocessor can be entirely disabled for a program when setting the flag
 * {@linkplain AmstradProgramMetaDataConstants#AMD_FLAG_NODIRECTJOY AMD_FLAG_NODIRECTJOY} on the program
 * </p>
 */
public class DirectJoystickBasicPreprocessor extends StagedBasicPreprocessor {

	private static final int JOYSTICK_COUNT = 2;

	private static final long STICKY_RETENTION_WHEN_IDLE = 300L; // in milliseconds

	public DirectJoystickBasicPreprocessor() {
	}

	@Override
	public int getDesiredPreambleLineCount() {
		return 0;
	}

	@Override
	public boolean isApplicableToMergedCode() {
		return true;
	}

	@Override
	public Collection<BasicKeywordToken> getKeywordsActedOn() {
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		try {
			return Arrays.asList(stf.createBasicKeyword("JOY"));
		} catch (BasicSyntaxException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	@Override
	protected void stage(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session) throws BasicException {
		if (session.getProgram().isNoDirectJoystick())
			return;
		if (!session.hasMacrosAdded(DirectJoystickMacro.class)) {
			addDirectJoystickMacros(session);
		}
		invokeOnJoystickKeyword(sourceCode, session);
	}

	private void addDirectJoystickMacros(StagedBasicProgramLoaderSession session) {
		for (int joystickNumber = 0; joystickNumber < JOYSTICK_COUNT; joystickNumber++) {
			if (isActiveJoystick(joystickNumber, session)) {
				addDirectJoystickMacro(joystickNumber, session);
			}
		}
	}

	private void addDirectJoystickMacro(int joystickNumber, StagedBasicProgramLoaderSession session) {
		int currentValueAddress = session.reserveMemory(1);
		int stickyValueAddress = session.reserveMemory(1);
		int checkedValueAddress = session.reserveMemory(1);
		DirectJoystickMacro macro = new DirectJoystickMacro(joystickNumber, currentValueAddress, stickyValueAddress,
				checkedValueAddress);
		session.addMacro(macro);
		new DirectJoystickRuntimeListener(macro, session).install();
	}

	private void invokeOnJoystickKeyword(BasicSourceCode sourceCode, StagedBasicProgramLoaderSession session)
			throws BasicException {
		BasicLanguage language = sourceCode.getLanguage();
		BasicSourceToken JOY = createKeywordToken(language, "JOY");
		LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
		BasicLineNumberScope scope = session.getSnapshotScopeOfCodeExcludingMacros(sourceCode);
		for (BasicSourceCodeLine line : sourceCode) {
			if (scope.isInScope(line)) {
				BasicSourceTokenSequence sequence = line.parse();
				int i = sequence.getFirstIndexOf(JOY);
				while (i >= 0) {
					JoystickCommand command = JoystickCommand.parseFrom(sequence, i);
					if (command != null) {
						int joyNr = command.getJoystickNumber();
						DirectJoystickMacro macro = getJoystickMacro(joyNr, session);
						if (isActiveJoystick(joyNr, session) && macro != null) {
							BasicSourceTokenSequence replaceSeq = new BasicSourceTokenSequence();
							boolean check = command.hasEqualsValue() && command.getEqualsValue() > 0;
							boolean checkPar = check && command.isConditional() && !command.isSingularCondition();
							int checkAddr = check ? macro.getStickyValueAddress() : macro.getCurrentValueAddress();
							if (checkPar) {
								replaceSeq.append(stf.createLiteral("("));
							}
							replaceSeq.append(stf.createBasicKeyword("PEEK"), stf.createLiteral("("),
									stf.createPositiveInteger16BitHexadecimal(checkAddr), stf.createLiteral(")"));
							if (command.hasEqualsValue()) {
								if (command.getEqualsValue() > 0) {
									replaceSeq.append(stf.createLiteral(" "), stf.createOperator("AND"),
											stf.createLiteral(" "),
											stf.createPositiveInteger8BitDecimal(command.getEqualsValue()));
								} else {
									replaceSeq.append(stf.createOperator("="),
											stf.createPositiveIntegerSingleDigitDecimal(0));
								}
							}
							if (checkPar) {
								replaceSeq.append(stf.createLiteral(")"), stf.createOperator(">"),
										stf.createPositiveIntegerSingleDigitDecimal(0));
							}
							sequence.replaceRange(i, command.getEndIndex() + 1, replaceSeq);
							int lengthDiff = replaceSeq.size() - (command.getEndIndex() - i + 1);
							if (check && command.isConditional() && command.getThenIndex() >= 0) {
								BasicSourceTokenSequence insertSeq = new BasicSourceTokenSequence();
								insertSeq.append(stf.createBasicKeyword("POKE"), stf.createLiteral(" "),
										stf.createPositiveInteger16BitHexadecimal(macro.getCheckedValueAddress()),
										stf.createLiteral(","), stf.createBasicKeyword("PEEK"), stf.createLiteral("("),
										stf.createPositiveInteger16BitHexadecimal(macro.getCheckedValueAddress()),
										stf.createLiteral(")"), stf.createLiteral(" "), stf.createOperator("OR"),
										stf.createLiteral(" "),
										stf.createPositiveInteger8BitDecimal(command.getEqualsValue()),
										stf.createInstructionSeparator());
								insertSeq.append(stf.createBasicKeyword("POKE"), stf.createLiteral(" "),
										stf.createPositiveInteger16BitHexadecimal(macro.getStickyValueAddress()),
										stf.createLiteral(","), stf.createBasicKeyword("PEEK"), stf.createLiteral("("),
										stf.createPositiveInteger16BitHexadecimal(macro.getCurrentValueAddress()),
										stf.createLiteral(")"), stf.createInstructionSeparator());
								int ins = sequence.getIndexFollowingWhitespace(command.getThenIndex() + 1 + lengthDiff);
								sequence.insert(ins, insertSeq);
								if (isExceedingLineLimits(language, sequence)) {
									int ln = getNextAvailableLineNumber(sourceCode);
									int k = ins + insertSeq.size();
									BasicSourceTokenSequence thenSeq = sequence.subSequence(k, sequence.size());
									sequence.replaceRange(k, sequence.size(), stf.createBasicKeyword("GOSUB"),
											stf.createLiteral(" "), stf.createLineNumberReference(ln));
									thenSeq.insert(0, stf.createLineNumber(ln), stf.createLiteral(" "));
									thenSeq.append(stf.createInstructionSeparator(), stf.createBasicKeyword("RETURN"));
									addCodeLine(sourceCode, thenSeq);
								}
							}
						} else {
							// keep JOY instruction
						}
					}
					i = sequence.getNextIndexOf(JOY, i + 1);
				}
				if (sequence.isModified()) {
					addCodeLine(sourceCode, sequence);
				}
			}
		}
	}

	private boolean isActiveJoystick(int joystickNumber, StagedBasicProgramLoaderSession session) {
		AmstradJoystick joystick = getJoystick(joystickNumber, session);
		return joystick != null && joystick.isActive();
	}

	private AmstradJoystick getJoystick(int joystickNumber, StagedBasicProgramLoaderSession session) {
		AmstradJoystick joystick = null;
		if (joystickNumber == 0) {
			joystick = session.getAmstradPc().getJoystick(AmstradJoystickID.JOYSTICK0);
		} else if (joystickNumber == 1) {
			joystick = session.getAmstradPc().getJoystick(AmstradJoystickID.JOYSTICK1);
		}
		return joystick;
	}

	private DirectJoystickMacro getJoystickMacro(int joystickNumber, StagedBasicProgramLoaderSession session) {
		for (DirectJoystickMacro macro : session.getMacrosAdded(DirectJoystickMacro.class)) {
			if (macro.getJoystickNumber() == joystickNumber)
				return macro;
		}
		return null;
	}

	private static class JoystickCommand {

		private int joystickNumber;

		private Integer equalsValue;

		private int endIndex;

		private boolean conditional;

		private boolean singularCondition;

		private int thenIndex;

		private JoystickCommand(int joystickNumber, int endIndex) {
			this(joystickNumber, null, endIndex);
		}

		private JoystickCommand(int joystickNumber, Integer equalsValue, int endIndex) {
			this.joystickNumber = joystickNumber;
			this.equalsValue = equalsValue;
			this.endIndex = endIndex;
		}

		public static JoystickCommand parseFrom(BasicSourceTokenSequence sequence, int startIndex)
				throws BasicException {
			LocomotiveBasicSourceTokenFactory stf = LocomotiveBasicSourceTokenFactory.getInstance();
			if (!stf.createBasicKeyword("JOY").equals(sequence.get(startIndex)))
				return null;
			int i = sequence.getIndexFollowingWhitespace(startIndex + 1);
			if (i < 0 || !stf.createLiteral("(").equals(sequence.get(i)))
				return null;
			i = sequence.getIndexFollowingWhitespace(i + 1);
			if (i < 0 || !(sequence.get(i) instanceof SingleDigitDecimalToken))
				return null;
			int joystickNumber = ((SingleDigitDecimalToken) sequence.get(i)).getInt();
			i = sequence.getIndexFollowingWhitespace(i + 1);
			if (i < 0 || !stf.createLiteral(")").equals(sequence.get(i)))
				return null;
			JoystickCommand command = null;
			int endIndex = i;
			i = sequence.getIndexFollowingWhitespace(i + 1);
			if (i >= 0 && sequence.get(i).equals(stf.createOperator("="))) {
				i = sequence.getIndexFollowingWhitespace(i + 1);
				if (i >= 0 && sequence.get(i) instanceof NumericToken) {
					int equalsValue = ((NumericToken) sequence.get(i)).getInt();
					if (equalsValue >= 0 && equalsValue <= 255) {
						endIndex = i;
						command = new JoystickCommand(joystickNumber, equalsValue, endIndex);
					}
				}
			}
			if (command == null)
				command = new JoystickCommand(joystickNumber, endIndex);
			// Conditional
			int si = sequence.getPreviousIndexOf(stf.createInstructionSeparator(), startIndex - 1);
			int fi = sequence.getPreviousIndexOf(stf.createBasicKeyword("IF"), startIndex - 1);
			if (fi >= 0 && si < fi) {
				command.setConditional(true);
				command.setThenIndex(sequence.getNextIndexOf(stf.createBasicKeyword("THEN"), startIndex + 1));
				command.setSingularCondition(fi == sequence.getIndexPrecedingWhitespace(startIndex - 1)
						&& command.getThenIndex() == sequence.getIndexFollowingWhitespace(endIndex + 1));
			}
			return command;
		}

		public int getJoystickNumber() {
			return joystickNumber;
		}

		public boolean hasEqualsValue() {
			return getEqualsValue() != null;
		}

		public Integer getEqualsValue() {
			return equalsValue;
		}

		public int getEndIndex() {
			return endIndex;
		}

		public boolean isConditional() {
			return conditional;
		}

		private void setConditional(boolean conditional) {
			this.conditional = conditional;
		}

		public boolean isSingularCondition() {
			return singularCondition;
		}

		private void setSingularCondition(boolean singular) {
			this.singularCondition = singular;
		}

		public int getThenIndex() {
			return thenIndex;
		}

		private void setThenIndex(int index) {
			this.thenIndex = index;
		}

	}

	private static class DirectJoystickMacro extends StagedBasicMacro {

		private int joystickNumber;

		private int currentValueAddress;

		private int stickyValueAddress;

		private int checkedValueAddress;

		public DirectJoystickMacro(int joystickNumber, int currentValueAddress, int stickyValueAddress,
				int checkedValueAddress) {
			this.joystickNumber = joystickNumber;
			this.currentValueAddress = currentValueAddress;
			this.stickyValueAddress = stickyValueAddress;
			this.checkedValueAddress = checkedValueAddress;
		}

		public int getJoystickNumber() {
			return joystickNumber;
		}

		public int getCurrentValueAddress() {
			return currentValueAddress;
		}

		public int getStickyValueAddress() {
			return stickyValueAddress;
		}

		public int getCheckedValueAddress() {
			return checkedValueAddress;
		}

	}

	private class DirectJoystickRuntimeListener extends StagedBasicProgramRuntimeListener {

		private DirectJoystickMacro macro;

		private AmstradJoystick joystick;

		private KeyEventTarget originalTarget;

		private DirectJoystickKeyEventTarget directTarget;

		public DirectJoystickRuntimeListener(DirectJoystickMacro macro, StagedBasicProgramLoaderSession session) {
			super(session);
			this.macro = macro;
			this.joystick = DirectJoystickBasicPreprocessor.this.getJoystick(macro.getJoystickNumber(), session);
		}

		@Override
		public void amstradProgramIsAboutToRun(AmstradProgramRuntime programRuntime) {
			resetJoystick();
			DirectJoystickKeyEventTarget target = new DirectJoystickKeyEventTarget();
			setOriginalTarget(getJoystickController().getGamingKeyEventTarget());
			setDirectTarget(target);
			getJoystickController().setGamingKeyEventTarget(target);
			target.requestStart();
		}

		@Override
		public void amstradProgramIsDisposed(AmstradProgramRuntime programRuntime, boolean programRemainsLoaded) {
			getDirectTarget().requestStop();
			getJoystickController().setGamingKeyEventTarget(getOriginalTarget());
			resetJoystick();
		}

		protected void resetJoystick() {
			AmstradMemory memory = getMemory();
			memory.eraseByte(getCurrentValueAddress());
			memory.eraseByte(getStickyValueAddress());
			memory.eraseByte(getCheckedValueAddress());
		}

		protected AmstradMemory getMemory() {
			return getJoystick().getAmstradPc().getMemory();
		}

		protected AmstradJoystickController getJoystickController() {
			return getJoystick().getController();
		}

		protected int getCurrentValueAddress() {
			return getMacro().getCurrentValueAddress();
		}

		protected int getStickyValueAddress() {
			return getMacro().getStickyValueAddress();
		}

		protected int getCheckedValueAddress() {
			return getMacro().getCheckedValueAddress();
		}

		public DirectJoystickMacro getMacro() {
			return macro;
		}

		public AmstradJoystick getJoystick() {
			return joystick;
		}

		private KeyEventTarget getOriginalTarget() {
			return originalTarget;
		}

		private void setOriginalTarget(KeyEventTarget target) {
			this.originalTarget = target;
		}

		private DirectJoystickKeyEventTarget getDirectTarget() {
			return directTarget;
		}

		private void setDirectTarget(DirectJoystickKeyEventTarget target) {
			this.directTarget = target;
		}

		private class DirectJoystickKeyEventTarget extends Thread implements KeyEventTarget {

			private boolean stopRequested;

			private boolean trackingIdleJoystick;

			private long joystickIdleStartTime;

			public DirectJoystickKeyEventTarget() {
				super("DirectJoystickKeyEventTarget");
				setDaemon(true);
			}

			public void requestStart() {
				start();
			}

			public void requestStop() {
				setStopRequested(true);
			}

			@Override
			public void run() {
				while (!isStopRequested()) {
					SystemUtils.sleep(STICKY_RETENTION_WHEN_IDLE / 3);
					considerInvalidatingStickyBits();
				}
			}

			@Override
			public void pressKey(KeyEvent keyEvent) {
				getOriginalTarget().pressKey(keyEvent);
				if (keyEvent instanceof AmstradJoystickKeyEvent) {
					int joyValue = ((AmstradJoystickKeyEvent) keyEvent).getJoystickValue();
					synchronized (this) {
						setTrackingIdleJoystick(false);
						turnMemoryBitsOn(getCurrentValueAddress(), joyValue);
						turnMemoryBitsOn(getStickyValueAddress(), joyValue);
						turnMemoryBitsOff(getCheckedValueAddress(), joyValue);
					}
				}
			}

			@Override
			public void releaseKey(KeyEvent keyEvent) {
				getOriginalTarget().releaseKey(keyEvent);
				if (keyEvent instanceof AmstradJoystickKeyEvent) {
					int joyValue = ((AmstradJoystickKeyEvent) keyEvent).getJoystickValue();
					synchronized (this) {
						turnMemoryBitsOff(getCurrentValueAddress(), joyValue);
						if (isMemoryBitsOn(getCheckedValueAddress(), joyValue)) {
							turnMemoryBitsOff(getStickyValueAddress(), joyValue);
							turnMemoryBitsOff(getCheckedValueAddress(), joyValue);
						}
						if (isJoystickIdle()) {
							setTrackingIdleJoystick(true);
							setJoystickIdleStartTime(System.currentTimeMillis());
						}
					}
				}
			}

			private void considerInvalidatingStickyBits() {
				if (isTrackingIdleJoystick()) {
					if (System.currentTimeMillis() >= getJoystickIdleStartTime() + STICKY_RETENTION_WHEN_IDLE) {
						synchronized (this) {
							invalidateStickyBits();
							setTrackingIdleJoystick(false);
						}
					}
				}
			}

			private void invalidateStickyBits() {
				AmstradMemory memory = getMemory();
				memory.writeByte(getStickyValueAddress(), memory.readByte(getCurrentValueAddress()));
			}

			private void turnMemoryBitsOn(int address, int bits) {
				AmstradMemory memory = getMemory();
				int b = memory.readByte(address) & 0xff;
				b = b | bits;
				memory.writeByte(address, (byte) b);
			}

			private void turnMemoryBitsOff(int address, int bits) {
				AmstradMemory memory = getMemory();
				int b = memory.readByte(address) & 0xff;
				b = b & ~bits;
				memory.writeByte(address, (byte) b);
			}

			private boolean isMemoryBitsOn(int address, int bits) {
				AmstradMemory memory = getMemory();
				int b = memory.readByte(address) & 0xff;
				return (b & bits) == bits;
			}

			private boolean isJoystickIdle() {
				return getMemory().readByte(getCurrentValueAddress()) == 0;
			}

			public boolean isStopRequested() {
				return stopRequested;
			}

			private void setStopRequested(boolean stop) {
				this.stopRequested = stop;
			}

			private boolean isTrackingIdleJoystick() {
				return trackingIdleJoystick;
			}

			private void setTrackingIdleJoystick(boolean tracking) {
				this.trackingIdleJoystick = tracking;
			}

			private long getJoystickIdleStartTime() {
				return joystickIdleStartTime;
			}

			private void setJoystickIdleStartTime(long time) {
				this.joystickIdleStartTime = time;
			}

		}

	}

}