package org.maia.amstrad.pc.tape;

import java.util.List;
import java.util.Vector;

import org.maia.amstrad.pc.AmstradDevice;
import org.maia.amstrad.pc.AmstradPc;

public class AmstradTape extends AmstradDevice {

	private List<AmstradTapeListener> tapeListeners;

	private boolean reading;

	private boolean writing;

	private String filenameAtTapeHead;

	public AmstradTape(AmstradPc amstradPc) {
		super(amstradPc);
		this.tapeListeners = new Vector<AmstradTapeListener>();
	}

	public void addTapeListener(AmstradTapeListener listener) {
		getTapeListeners().add(listener);
	}

	public void removeTapeListener(AmstradTapeListener listener) {
		getTapeListeners().remove(listener);
	}

	public void notifyTapeReading(String filename) {
		setReading(true);
		setWriting(false);
		setFilenameAtTapeHead(filename);
		fireTapeReadingEvent();
	}

	public void notifyTapeStoppedReading() {
		setReading(false);
		setFilenameAtTapeHead(null);
		fireTapeStoppedReadingEvent();
	}

	public void notifyTapeWriting(String filename) {
		setWriting(true);
		setReading(false);
		setFilenameAtTapeHead(filename);
		fireTapeWritingEvent();
	}

	public void notifyTapeStoppedWriting() {
		setWriting(false);
		setFilenameAtTapeHead(null);
		fireTapeStoppedWritingEvent();
	}

	protected void fireTapeReadingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeReading(this);
	}

	protected void fireTapeStoppedReadingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeStoppedReading(this);
	}

	protected void fireTapeWritingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeWriting(this);
	}

	protected void fireTapeStoppedWritingEvent() {
		for (AmstradTapeListener listener : getTapeListeners())
			listener.amstradTapeStoppedWriting(this);
	}

	protected List<AmstradTapeListener> getTapeListeners() {
		return tapeListeners;
	}

	public boolean isActive() {
		return isReading() || isWriting();
	}

	public boolean isReading() {
		return reading;
	}

	private void setReading(boolean reading) {
		this.reading = reading;
	}

	public boolean isWriting() {
		return writing;
	}

	private void setWriting(boolean writing) {
		this.writing = writing;
	}

	public String getFilenameAtTapeHead() {
		return filenameAtTapeHead;
	}

	private void setFilenameAtTapeHead(String filename) {
		this.filenameAtTapeHead = filename;
	}

}