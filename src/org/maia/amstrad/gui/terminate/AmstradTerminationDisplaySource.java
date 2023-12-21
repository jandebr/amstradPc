package org.maia.amstrad.gui.terminate;

import org.maia.amstrad.AmstradFactory;
import org.maia.amstrad.pc.AmstradPc;
import org.maia.amstrad.pc.monitor.display.AmstradDisplayCanvas;
import org.maia.amstrad.pc.monitor.display.source.AmstradAlternativeDisplaySourceType;
import org.maia.amstrad.pc.monitor.display.source.AmstradEmulatedDisplaySource;

public class AmstradTerminationDisplaySource extends AmstradEmulatedDisplaySource {

	private static final String SETTING_MESSAGE = "quit.animate.message";

	private static final char MESSAGE_SEPARATOR = '|';

	public static String DEFAULT_MESSAGE = "Treasure the Amstrad";

	private String message;

	private int messageCharactersPrinted;

	private long timeOffset;

	private long elapsedTimeForCursor;

	private long elapsedTimeForNextCharacter;

	private long elapsedTimePrintCompleted;

	private boolean forceQuit;

	private static String selectMessage() {
		String message = AmstradFactory.getInstance().getAmstradContext().getUserSettings().get(SETTING_MESSAGE,
				DEFAULT_MESSAGE);
		if (message.indexOf(MESSAGE_SEPARATOR) > 0) {
			String[] messages = message.split("\\" + MESSAGE_SEPARATOR);
			message = messages[(int) Math.floor(messages.length * Math.random())];
		}
		return message;
	}

	public AmstradTerminationDisplaySource(AmstradPc amstradPc) {
		this(amstradPc, selectMessage());
	}

	public AmstradTerminationDisplaySource(AmstradPc amstradPc, String message) {
		super(amstradPc);
		this.message = message;
	}

	@Override
	protected void init(AmstradDisplayCanvas canvas) {
		super.init(canvas);
		setMessageCharactersPrinted(0);
		setTimeOffset(System.currentTimeMillis());
		setElapsedTimeForCursor(1000L);
		setElapsedTimeForNextCharacter(getElapsedTimeForCursor() + 2000L);
	}

	@Override
	protected void renderContent(AmstradDisplayCanvas canvas) {
		if (isForceQuit())
			return;
		long etime = getElapsedTime();
		if (etime < getElapsedTimeForCursor())
			return;
		if (isMessagePrintCompleted() && etime >= getElapsedTimePrintCompleted() + 2500L)
			return;
		int n = getMessage().length();
		int m = getMessageCharactersPrinted();
		if (m < n && etime >= getElapsedTimeForNextCharacter()) {
			if (++m == n)
				setElapsedTimePrintCompleted(etime);
			setMessageCharactersPrinted(m);
			setElapsedTimeForNextCharacter(etime + 100L + Math.round(Math.pow(Math.random(), 3.0) * 400.0));
		} else if (m == n && etime >= getElapsedTimePrintCompleted() + 2000L) {
			setBackgroundColorIndex(0);
			canvas.border(0).pen(1);
		}
		canvas.locate(20 - n / 2, 12);
		canvas.print(getMessage().substring(0, m));
		if (m < n || etime < getElapsedTimePrintCompleted() + 1000L) {
			canvas.printChr(143); // cursor
		}
	}

	@Override
	public AmstradAlternativeDisplaySourceType getType() {
		return AmstradAlternativeDisplaySourceType.TERMINATION;
	}

	public void forceQuit() {
		setForceQuit(true);
	}

	public boolean isAnimationCompleted() {
		if (isForceQuit()) {
			return true;
		} else if (!isMessagePrintCompleted()) {
			return false;
		} else {
			return getElapsedTime() >= getElapsedTimePrintCompleted() + 3000L;
		}
	}

	private boolean isMessagePrintCompleted() {
		return getMessageCharactersPrinted() == getMessage().length();
	}

	private long getElapsedTime() {
		return System.currentTimeMillis() - getTimeOffset();
	}

	private String getMessage() {
		return message;
	}

	private int getMessageCharactersPrinted() {
		return messageCharactersPrinted;
	}

	private void setMessageCharactersPrinted(int nchars) {
		this.messageCharactersPrinted = nchars;
	}

	private long getTimeOffset() {
		return timeOffset;
	}

	private void setTimeOffset(long timeOffset) {
		this.timeOffset = timeOffset;
	}

	private long getElapsedTimeForCursor() {
		return elapsedTimeForCursor;
	}

	private void setElapsedTimeForCursor(long etime) {
		this.elapsedTimeForCursor = etime;
	}

	private long getElapsedTimeForNextCharacter() {
		return elapsedTimeForNextCharacter;
	}

	private void setElapsedTimeForNextCharacter(long etime) {
		this.elapsedTimeForNextCharacter = etime;
	}

	private long getElapsedTimePrintCompleted() {
		return elapsedTimePrintCompleted;
	}

	private void setElapsedTimePrintCompleted(long etime) {
		this.elapsedTimePrintCompleted = etime;
	}

	private boolean isForceQuit() {
		return forceQuit;
	}

	private void setForceQuit(boolean forceQuit) {
		this.forceQuit = forceQuit;
	}

}