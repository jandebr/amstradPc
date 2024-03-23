package org.maia.amstrad.tape.read;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.maia.amstrad.tape.model.Bit;

public class AudioTapeInputStream extends TapeInputStream {

	private AudioFile audioFile;

	private float speedFactor; // e.g., 2.0 for double the speed

	private State state;

	private Stack<State> memorizedStates;

	private List<AudioTapeInputStreamListener> listeners;

	public static float DEFAULT_SPEED_FACTOR = 1.0f;

	private static short DEFAULT_WAVE_TOP = 20000;

	private static final short SILENCE_THRESHOLD = 5000;

	private static final int LONGWAVE_LENGTH_NORMALSPEED = 62;

	private static final float WAVE_THRESHOLD_FACTOR = 0.5f;

	public AudioTapeInputStream(AudioFile audioFile) {
		this(audioFile, DEFAULT_SPEED_FACTOR);
	}

	public AudioTapeInputStream(AudioFile audioFile, float speedFactor) {
		this.audioFile = audioFile;
		this.speedFactor = speedFactor;
		this.state = new State();
		this.memorizedStates = new Stack<State>();
		this.listeners = new Vector<AudioTapeInputStreamListener>();
	}

	public String toString() {
		return getAudioFile().getSourceFile().getName() + "@" + getSamplePosition();
	}

	public void addListener(AudioTapeInputStreamListener listener) {
		getListeners().add(listener);
	}

	public void removeListener(AudioTapeInputStreamListener listener) {
		getListeners().remove(listener);
	}

	private Integer nextWaveLength() throws IOException {
		Integer length = null;
		boolean seekx = false;
		while (length == null && getSamplePosition() < getNumberOfSamples()) {
			short y = getCurrentSample();
			getState().ascendToTop(y);
			short ynt = (short) (-WAVE_THRESHOLD_FACTOR * getState().getTop());
			if (y < ynt) {
				seekx = true;
				getState().startSeekingTop();
			} else if (seekx && y > 0) {
				getState().markSamplePositionAsX1();
				length = getState().getXdelta();
				seekx = false;
			}
			if (length == null)
				getState().incrementSamplePosition();
		}
		return length;
	}

	@Override
	public Bit nextBit() throws IOException {
		memorizeState();
		Bit bit = null;
		long offset = getSamplePosition();
		Integer dx = nextWaveLength();
		if (dx != null) {
			float dxf = dx.floatValue();
			float speed = getSpeedFactor();
			if (dxf < 50 / speed)
				bit = Bit.ZERO;
			else if (dx < 100 / speed)
				bit = Bit.ONE;
		}
		if (bit == null) {
			// no bit found, restore original state
			restoreMemorizedState();
		} else {
			discardMemorizedState();
			long length = getSamplePosition() - offset;
			fireReadBit(bit, offset, length);
		}
		return bit;
	}

	private void fireReadBit(Bit bit, long offset, long length) {
		for (AudioTapeInputStreamListener listener : getListeners())
			listener.readBit(bit, offset, length, this);
	}

	@Override
	public final Byte nextByte() throws IOException {
		memorizeState();
		Byte bite = super.nextByte();
		if (bite == null) {
			// no byte remaining, restore original state
			restoreMemorizedState();
		} else {
			discardMemorizedState();
		}
		return bite;
	}

	@Override
	public final Integer nextWord() throws IOException {
		memorizeState();
		Integer word = super.nextWord();
		if (word == null) {
			// no word remaining, restore original state
			restoreMemorizedState();
		} else {
			discardMemorizedState();
		}
		return word;
	}

	@Override
	public boolean seekSilence() throws IOException {
		int nw = 800; // window size
		int nt = 0; // below threshold in window
		int tt = 4; // maximum alternating peaks above threshold in window
		short sign = 1; // alternating sign
		short[] samples = new short[nw];
		long x = getSamplePosition();
		long n = getNumberOfSamples();
		int si = 0; // sample position offset
		while (x < n && (si < nw || nt > tt)) {
			if (si >= nw && samples[si % nw] != 0)
				nt--;
			if (sign * getSample(x) > SILENCE_THRESHOLD) {
				nt++;
				sign *= -1;
				samples[si % nw] = 1;
			} else {
				samples[si % nw] = 0;
			}
			si++;
			x++;
		}
		if (si >= nw && nt <= tt) {
			// rewind to start of silence
			setSamplePosition(getSamplePosition() + si - nw);
			return true;
		}
		return false; // state has not changed
	}

	@Override
	public boolean skipSilence() throws IOException {
		int nw = 800; // window size
		int nt = 0; // above threshold in window
		int tt = 10; // minimum alternating peaks above threshold in window
		short sign = 1; // alternating sign
		short[] samples = new short[nw];
		long x = getSamplePosition();
		long n = getNumberOfSamples();
		int si = 0; // sample position offset
		while (x < n && nt < tt) {
			if (si >= nw && samples[si % nw] != 0)
				nt--;
			if (sign * getSample(x) > SILENCE_THRESHOLD) {
				nt++;
				sign *= -1;
				samples[si % nw] = 1;
			} else {
				samples[si % nw] = 0;
			}
			si++;
			x++;
		}
		if (nt == tt) {
			updateDefaultWaveTop(x, n);
			updateSpeedFactor(x);
			// rewind to start of signal
			while (nt > 0) {
				if (samples[--si % nw] != 0)
					nt--;
			}
			setSamplePosition(getSamplePosition() + si);
			return true;
		}
		return false; // state has not changed
	}

	private void updateDefaultWaveTop(long x, long n) throws IOException {
		int samples = 10000;
		if (n - x < samples)
			return; // not enough samples left
		short[] histo = new short[3300];
		int count = 0;
		for (int i = 0; i < samples; i++) {
			short y = getSample(x++);
			if (y >= 0) {
				int bin = y / 10;
				histo[bin]++;
				count++;
			}
		}
		double cump = 0;
		int bin = 0;
		while (cump < 0.95 && bin < histo.length) {
			cump += histo[bin++] / (double) count;
		}
		short top = (short) ((bin - 1) * 10);
		if (top > SILENCE_THRESHOLD) {
			DEFAULT_WAVE_TOP = top;
		}
	}

	private void updateSpeedFactor(long x) throws IOException {
		memorizeState();
		setSamplePosition(x);
		int i = 0, sum = 0, n = 20;
		Integer dx;
		do {
			dx = nextWaveLength();
			if (dx != null) {
				sum += dx.intValue();
				i++;
			}
		} while (dx != null && i < n);
		if (i == n) {
			float avgDx = sum / (float) n;
			setSpeedFactor(LONGWAVE_LENGTH_NORMALSPEED / avgDx);
			// System.out.println("Update speed factor to " + getSpeedFactor());
		}
		restoreMemorizedState();
	}

	@Override
	public void close() throws IOException {
		// nothing to be done (keep underlying audio file open)
	}

	private void memorizeState() {
		getMemorizedStates().push(getState().clone());
	}

	private void discardMemorizedState() {
		getMemorizedStates().pop();
	}

	private void restoreMemorizedState() {
		setState(getMemorizedStates().pop().clone());
	}

	protected long getNumberOfSamples() throws IOException {
		return getAudioFile().getNumberOfSamples();
	}

	protected short getSample(long index) throws IOException {
		return getAudioFile().getSample(index);
	}

	protected short getCurrentSample() throws IOException {
		return getSample(getSamplePosition());
	}

	public long getSamplePosition() {
		return getState().getSamplePosition();
	}

	public void setSamplePosition(long samplePosition) {
		getState().moveToSamplePosition(samplePosition);
	}

	public AudioFile getAudioFile() {
		return audioFile;
	}

	public float getSpeedFactor() {
		return speedFactor;
	}

	private void setSpeedFactor(float speedFactor) {
		this.speedFactor = speedFactor;
	}

	private State getState() {
		return state;
	}

	private void setState(State state) {
		this.state = state;
	}

	private Stack<State> getMemorizedStates() {
		return memorizedStates;
	}

	private List<AudioTapeInputStreamListener> getListeners() {
		return listeners;
	}

	private static class State implements Cloneable {

		private long samplePosition;

		private long x0; // previous y=0 crossing

		private long x1; // last y=0 crossing

		private boolean seekTop;

		private short top;

		private short newTop;

		public State() {
			moveToSamplePosition(0L);
		}

		private State(long samplePosition, long x0, long x1, boolean seekTop, short top, short newTop) {
			this.samplePosition = samplePosition;
			this.x0 = x0;
			this.x1 = x1;
			this.seekTop = seekTop;
			this.top = top;
			this.newTop = newTop;
		}

		public State clone() {
			return new State(this.samplePosition, this.x0, this.x1, this.seekTop, this.top, this.newTop);
		}

		public void incrementSamplePosition() {
			this.samplePosition++;
		}

		public void moveToSamplePosition(long samplePosition) {
			this.samplePosition = samplePosition;
			this.x0 = -1L;
			this.x1 = -1L;
			this.seekTop = false;
			this.top = DEFAULT_WAVE_TOP;
			this.newTop = 0;
		}

		public void markSamplePositionAsX1() {
			this.x0 = this.x1;
			this.x1 = this.samplePosition;
		}

		public Integer getXdelta() {
			Integer delta = null;
			if (this.x0 >= 0) {
				delta = (int) (this.x1 - this.x0);
			}
			return delta;
		}

		public void startSeekingTop() {
			this.seekTop = true;
			this.newTop = 0;
		}

		public void ascendToTop(short sample) {
			if (!isSeekingTop() || sample <= 0)
				return;
			if (sample >= this.newTop) {
				this.newTop = sample;
			} else {
				stopSeekingTop();
			}
		}

		public void stopSeekingTop() {
			this.seekTop = false;
			this.top = this.newTop;
			this.newTop = 0;
		}

		public long getSamplePosition() {
			return samplePosition;
		}

		public boolean isSeekingTop() {
			return seekTop;
		}

		public short getTop() {
			return top;
		}

	}

}