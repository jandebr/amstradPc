package org.maia.amstrad.tape.read;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * An audio WAVE file as the recording of an Amstrad tape.
 * 
 * <p>
 * Assumptions
 * <ul>
 * <li>The number of sound channels is 1 (mono).</li>
 * <li>The sound data consists of 16-bit samples, stored as 2's-complement signed integers, ranging from -32768 to
 * 32767.</li>
 * <li>The byte ordering is little-endian.</li>
 * </ul>
 */
public class AudioWaveFile extends AudioFile {

	private RandomAccessFile file;

	private int sampleRate;

	private long numberOfSamples;

	private byte[] buffer;

	private int bufferLength;

	private long bufferOffset;

	private static final int BUFFER_SIZE = 64 * 1024; // in bytes

	private static final long HEADER_LENGTH = 44L; // in bytes

	private static final long SAMPLE_SIZE = 2L; // in bytes

	public AudioWaveFile(File sourceFile) throws IOException {
		super(sourceFile);
		this.file = new RandomAccessFile(sourceFile, "r");
		this.sampleRate = readSampleRate();
		this.numberOfSamples = readNumberOfSamples();
		this.buffer = new byte[BUFFER_SIZE];
	}

	private int readSampleRate() throws IOException {
		byte[] data = new byte[4];
		file.seek(24);
		file.read(data);
		return ((data[3] & 0xff) << 24) | ((data[2] & 0xff) << 16) | ((data[1] & 0xff) << 8) | (data[0] & 0xff);
	}

	private long readNumberOfSamples() throws IOException {
		return (file.length() - HEADER_LENGTH) / SAMPLE_SIZE;
	}

	@Override
	public int getSampleRate() {
		return sampleRate;
	}

	@Override
	public long getNumberOfSamples() {
		return numberOfSamples;
	}

	@Override
	public short getSample(long index) throws IOException {
		long offset = HEADER_LENGTH + index * SAMPLE_SIZE;
		if (offset >= bufferOffset && offset + SAMPLE_SIZE <= bufferOffset + bufferLength) {
			// read from buffer
			int bi = (int) (offset - bufferOffset);
			return (short) ((buffer[bi + 1] << 8) | (buffer[bi] & 0xff));
		} else {
			// fill buffer
			file.seek(offset);
			bufferOffset = offset;
			bufferLength = file.read(buffer);
			if (bufferLength < SAMPLE_SIZE) {
				return 0;
			} else {
				return (short) ((buffer[1] << 8) | (buffer[0] & 0xff));
			}
		}
	}

	@Override
	public void close() throws IOException {
		file.close();
	}

}