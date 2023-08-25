package org.maia.amstrad.pc.impl.jemu;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.AmstradPcStateListener;
import org.maia.amstrad.pc.keyboard.AmstradKeyboard;
import org.maia.amstrad.pc.keyboard.AmstradKeyboardEvent;

import jemu.core.device.BasicKeyboardPromptModus;
import jemu.core.device.Computer;
import jemu.core.device.ComputerKeyboardListener;
import jemu.ui.Autotype;

public abstract class JemuKeyboard extends AmstradKeyboard
		implements KeyListener, ComputerKeyboardListener, AmstradPcStateListener {

	private JemuKeyboardController controller;

	private int escapeKeyCounter;

	private boolean autotyping;

	private boolean onBasicPrompt;

	private long onBasicPromptSince;

	private boolean inBasicInterpretModus;

	protected JemuKeyboard(JemuAmstradPc amstradPc) {
		super(amstradPc);
		this.controller = createController();
		amstradPc.addStateListener(this);
	}

	protected abstract JemuKeyboardController createController();

	@Override
	public void amstradPcStarted(AmstradPc amstradPc) {
		connectKeyboardWhenPcStarted();
		resetEscapeKeyCounter();
	}

	protected abstract void connectKeyboardWhenPcStarted();

	@Override
	public boolean isTyping() {
		if (isAutotyping())
			return true;
		if (getOnBasicPromptSince() >= System.currentTimeMillis() - 100L)
			return true;
		return false;
	}

	@Override
	public synchronized void type(CharSequence text, boolean waitUntilTyped) {
		checkStarted();
		checkNotTerminated();
		setAutotyping(true);
		Autotype.typeText(text);
		resetEscapeKeyCounter();
		if (waitUntilTyped) {
			waitUntilAutotypeEnded();
		}
	}

	@Override
	public final void breakEscape() {
		checkStarted();
		checkNotTerminated();
		doBreakEscape();
	}

	protected abstract void doBreakEscape();

	private synchronized void waitUntilAutotypeEnded() {
		while (isAutotyping()) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void amstradPcPausing(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void amstradPcResuming(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void amstradPcRebooting(AmstradPc amstradPc) {
		resetEscapeKeyCounter();
		if (isAutotyping())
			notifyAutotypeEnded();
	}

	@Override
	public void amstradPcTerminated(AmstradPc amstradPc) {
		resetEscapeKeyCounter();
		if (isAutotyping())
			notifyAutotypeEnded();
	}

	@Override
	public void amstradPcProgramLoaded(AmstradPc amstradPc) {
		// no action
	}

	@Override
	public void keyPressed(KeyEvent e) {
		fireKeyboardEventDispatched(new AmstradKeyboardEvent(this, e));
	}

	@Override
	public void keyReleased(KeyEvent e) {
		fireKeyboardEventDispatched(new AmstradKeyboardEvent(this, e));
	}

	@Override
	public void keyTyped(KeyEvent e) {
		fireKeyboardEventDispatched(new AmstradKeyboardEvent(this, e));
	}

	@Override
	public void computerPressEscapeKey(Computer computer) {
		if (++escapeKeyCounter == 2) {
			fireKeyboardBreakEscaped();
		}
	}

	@Override
	public void computerSuppressEscapeKey(Computer computer) {
		resetEscapeKeyCounter();
	}

	@Override
	public void computerAutotypeStarted(Computer computer) {
		notifyAutotypeStarted();
	}

	@Override
	public void computerAutotypeEnded(Computer computer) {
		notifyAutotypeEnded();
	}

	@Override
	public void computerEnterBasicKeyboardPrompt(Computer computer, BasicKeyboardPromptModus modus) {
		setOnBasicPrompt(true);
		setOnBasicPromptSince(System.currentTimeMillis());
		setInBasicInterpretModus(BasicKeyboardPromptModus.INTERPRET.equals(modus));
	}

	@Override
	public void computerExitBasicKeyboardPrompt(Computer computer, BasicKeyboardPromptModus modus) {
		setOnBasicPrompt(false);
		setInBasicInterpretModus(BasicKeyboardPromptModus.INTERPRET.equals(modus));
	}

	@Override
	public JemuKeyboardController getController() {
		return controller;
	}

	private void notifyAutotypeStarted() {
		System.out.println("Autotype started");
		setAutotyping(true);
	}

	private synchronized void notifyAutotypeEnded() {
		System.out.println("Autotype ended");
		setAutotyping(false);
		notifyAll();
	}

	private void resetEscapeKeyCounter() {
		escapeKeyCounter = 0;
	}

	@Override
	public boolean isAutotyping() {
		return autotyping;
	}

	private void setAutotyping(boolean autotyping) {
		this.autotyping = autotyping;
	}

	public boolean isOnBasicPrompt() {
		return onBasicPrompt;
	}

	private void setOnBasicPrompt(boolean onBasicPrompt) {
		this.onBasicPrompt = onBasicPrompt;
	}

	private long getOnBasicPromptSince() {
		return onBasicPromptSince;
	}

	private void setOnBasicPromptSince(long since) {
		this.onBasicPromptSince = since;
	}

	public boolean isInBasicInterpretModus() {
		return inBasicInterpretModus;
	}

	private void setInBasicInterpretModus(boolean interpretModus) {
		this.inBasicInterpretModus = interpretModus;
	}

}